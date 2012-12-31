package de.rainu.lib.dbc;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import de.rainu.lib.dbc.beans.ConnectionInfo;
import de.rainu.lib.dbc.exception.BackendException;

/**
 * Das DB(Database)-List ist eine {@link List} Implementierung.
 * Sie basiert auf der {@link DBMap} und deligiert entsprechend
 * alle Anfragen an diese weiter. 
 *
 * @author rainu
 *
 * @param <E>
 */
public class DBList<E extends Serializable> extends AbstractList<E> implements List<E>, ColumnNames{
	protected DBMap<Integer, E> backend;
	private PreparedStatement indicesByValueAscStatement;
	private PreparedStatement indicesByValueDescStatement;
	private PreparedStatement indicesByNullValueAscStatement;
	private PreparedStatement indicesByNullValueDescStatement;
	private PreparedStatement incrementIndexStatement;
	private PreparedStatement decrementIndexStatement;
	private PreparedStatement removeRangeStatement;
	
	public DBList(DBMap<Integer, E> backend) {
		this.backend = backend;
		
		try{
			init();
		}catch(SQLException e){
			throw new BackendException("Could not initialise list.", e);
		}
	}
	
	public DBList(ConnectionInfo info, String tableName, boolean dropIfExist, boolean debugMode){
		this(new DBMap<Integer, E>(info, tableName, dropIfExist, debugMode));
	}
	
	public DBList(ConnectionInfo info, String tableName, boolean dropIfExist){
		this(new DBMap<Integer, E>(info, tableName, dropIfExist));
	}
	
	public DBList(ConnectionInfo info, String tableName){
		this(new DBMap<Integer, E>(info, tableName));
	}
	
	public DBList(ConnectionInfo info){
		this(new DBMap<Integer, E>(info));
	}
		
	@Override
	protected void finalize() throws Throwable {
		try{indicesByValueAscStatement.close();}catch(SQLException e){}
		try{indicesByValueDescStatement.close();}catch(SQLException e){}
		try{indicesByNullValueAscStatement.close();}catch(SQLException e){}
		try{indicesByNullValueDescStatement.close();}catch(SQLException e){}
		try{incrementIndexStatement.close();}catch(SQLException e){}
		try{decrementIndexStatement.close();}catch(SQLException e){}
		try{removeRangeStatement.close();}catch(SQLException e){}
		
		super.finalize();
	}
	
	private void init() throws SQLException{
		indicesByValueAscStatement = backend.connection.prepareStatement(
				"SELECT " + COL_INT_KEY + 
				" FROM " + backend.tableName +
				" WHERE " + COL_VALUE_HASH + " = ?" +
				" ORDER BY " + COL_INT_KEY + " ASC");
		indicesByValueDescStatement = backend.connection.prepareStatement(
				"SELECT " + COL_INT_KEY + 
				" FROM " + backend.tableName +
				" WHERE " + COL_VALUE_HASH + " = ?" +
				" ORDER BY " + COL_INT_KEY + " DESC");
		indicesByNullValueAscStatement = backend.connection.prepareStatement(
				"SELECT " + COL_INT_KEY + 
				" FROM " + backend.tableName +
				" WHERE " + COL_VALUE_HASH + " IS NULL" +
				" ORDER BY " + COL_INT_KEY + " ASC");
		indicesByNullValueDescStatement = backend.connection.prepareStatement(
				"SELECT " + COL_INT_KEY + 
				" FROM " + backend.tableName +
				" WHERE " + COL_VALUE_HASH + " IS NULL" +
				" ORDER BY " + COL_INT_KEY + " DESC");
		incrementIndexStatement = backend.connection.prepareStatement(
				"UPDATE " + backend.tableName + 
				" SET " + COL_INT_KEY + " = " + COL_INT_KEY + " + ?," +
					" " + COL_ID + " = " + COL_ID + " + ? " +
				" WHERE " + COL_INT_KEY + " >= ?");
		decrementIndexStatement = backend.connection.prepareStatement(
				"UPDATE " + backend.tableName + 
				" SET " + COL_INT_KEY + " = " + COL_INT_KEY + " - ?," +
					" " + COL_ID + " = " + COL_ID + " - ? " +
				" WHERE " + COL_INT_KEY + " >= ?");
		removeRangeStatement = backend.connection.prepareStatement(
				"DELETE FROM " + backend.tableName + 
				" WHERE " + COL_ID + " BETWEEN ? AND ?");
	}
	
	
	@Override
	public int size() {
		return backend.size();
	}

	@Override
	public boolean isEmpty() {
		return backend.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return backend.containsValue(o);
	}

	@Override
	public boolean add(E e) {
		backend.put(size(), e);
		return true;
	}

	@Override
	public void clear() {
		backend.clear();
	}
	
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		//Diese Methode wird in der Ursprungsversion
		//via iteration gelöst. Das kann man aber via
		//SQL wesentlich besser machen, da man bei dem
		//Iterator die Werte noch aus der Datenbank extrahiert
		//wird. Diesen Schritt kann (und muss) man sich sparen.
		
		try {
			removeRangeStatement.setInt(1, fromIndex);
			removeRangeStatement.setInt(2, toIndex);
			removeRangeStatement.executeUpdate();
			
			decrementIndex(fromIndex, toIndex - fromIndex + 1);
			backend.resetCachedSize();
		} catch (SQLException e) {
			throw new BackendException(
					"Could not remove range " + fromIndex + " - " + toIndex + ".",
					e);
		}
	}


	@Override
	public E get(int index) {
		checkIndex(index);
		
		return backend.get(index);
	}


	@Override
	public E set(int index, E element) {
		checkIndex(index);
		
		E lastValue = backend.get(index);
		
		backend.put(index, element);
		return lastValue;
	}


	@Override
	public void add(int index, E element) {
		checkIndex(index);
		
		try{
			incrementIndex(index, 1);
		}catch(SQLException e){
			throw new BackendException(
					"Could not order indices in backend." +
					"AT THIS MOMENT THIS LIST COULD BE INCONSISTENT!",
					e);
		}
		
		backend.put(index, element);
	}


	@Override
	public E remove(int index) {
		checkIndex(index);
		
		E element = backend.get(index);
		backend.remove(index);
		
		try{
			decrementIndex(index, 1);
	
			return element;
		}catch(SQLException e){
			throw new BackendException(
					"Could not order indices in backend." +
					"AT THIS MOMENT THIS LIST IS INCONSISTENT!",
					e);
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if(c == this){
			boolean change = !isEmpty();
			
			clear();
			
			return change;
		}
		boolean modified = false;
		if(c != null) for(Object curValue : c){
			modified |= remove(curValue);
		}
		
		return modified;
	}

	@Override
	public int indexOf(Object o) {
		boolean contains = backend.containsValue(o);
		if(!contains) return -1;
		
		try{
			int index = o == null ?
						getFirstIndexForNullValue() :
						getFirstIndexForValue(o);
						
			return index;
		}catch(SQLException e){
			e.printStackTrace();
			return -1;
		}
	}


	@Override
	public int lastIndexOf(Object o) {
		boolean contains = backend.containsValue(o);
		if(!contains) return -1;
		
		try{
			int index = o == null ?
						getLastIndexForNullValue() :
						getLastIndexForValue(o);
						
			return index;
		}catch(SQLException e){
			return -1;
		}
	}
		
	private Integer getFirstIndexForValue(Object value) throws SQLException{
		indicesByValueAscStatement.setInt(1, value.hashCode());
		
		ResultSet result = indicesByValueAscStatement.executeQuery();
		if(!result.first()) return null;	//kein element gefunden!
		
		return result.getInt(1);
	}
	
	private Integer getFirstIndexForNullValue() throws SQLException{
		ResultSet result = indicesByNullValueAscStatement.executeQuery();
		if(!result.first()) return null;	//kein element gefunden!
		
		return result.getInt(1);
	}
	
	private Integer getLastIndexForValue(Object value) throws SQLException{
		indicesByValueDescStatement.setInt(1, value.hashCode());
		
		ResultSet result = indicesByValueDescStatement.executeQuery();
		if(!result.first()) return null;	//kein element gefunden!
		
		return result.getInt(1);
	}
	
	private Integer getLastIndexForNullValue() throws SQLException{
		ResultSet result = indicesByNullValueDescStatement.executeQuery();
		if(!result.first()) return null;	//kein element gefunden!
		
		return result.getInt(1);
	}
	
	private void checkIndex(int index){
		if(index < 0){
			throw new IndexOutOfBoundsException("Index must be greater then 0");
		}
		if(index >= size()){
			throw new IndexOutOfBoundsException("Index must be lesser then size.");
		}
	}
	
	private void incrementIndex(int fromIndex, int toIncrement) throws SQLException{
		//Wir müssen auch darauf achten, dass wir den hash-code des
		//schlüssels (der index) angepasst wird. Hier nutzen wir die
		//Tatsache, das der Hashcode eines Integers gleich der eigentliche
		//Wert ist. Somit müssen wir also nur darauf auchten, dass der Hashcode
		//auch etsprechend des Indizes verändert wird!
		incrementIndexStatement.setInt(1, toIncrement);
		incrementIndexStatement.setInt(2, toIncrement);
		incrementIndexStatement.setInt(3, fromIndex);
		incrementIndexStatement.executeUpdate();
	}
	
	private void decrementIndex(int fromIndex, int toDecrement) throws SQLException{
		//Wir müssen auch darauf achten, dass wir den hash-code des
		//schlüssels (der index) angepasst wird. Hier nutzen wir die
		//Tatsache, das der Hashcode eines Integers gleich der eigentliche
		//Wert ist. Somit müssen wir also nur darauf auchten, dass der Hashcode
		//auch etsprechend des Indizes verändert wird!
		decrementIndexStatement.setInt(1, toDecrement);
		decrementIndexStatement.setInt(2, toDecrement);
		decrementIndexStatement.setInt(3, fromIndex);
		decrementIndexStatement.executeUpdate();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = getClass().hashCode();
		result = prime * result + ((backend == null) ? 0 : backend.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		DBList other = (DBList) obj;
		if (backend == null) {
			if (other.backend != null)
				return false;
		} else if (!backend.equals(other.backend))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + "]\n" +
				"Backend: " + backend != null ? backend.toString() : "-";
	}
}
