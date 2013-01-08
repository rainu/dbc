package de.rainu.lib.dbc;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import de.rainu.lib.dbc.beans.DBMapEntry;
import de.rainu.lib.dbc.exception.BackendException;
import de.rainu.lib.dbc.map.interpreter.Interpreter;
import de.rainu.lib.dbc.map.interpreter.InterpreterProvider;

/**
 * Dies ist die {@link Iterator}-Implementation für das {@link DBMapEntrySet}.
 * 
 * @author rainu
 *
 * @param <K> Typ des Schlüssels
 * @param <V> Typ des Wertes
 */
public class DBMapEntrySetIterator <K extends Serializable, V extends Serializable>
	implements Iterator<Entry<K, V>>, ColumnNames{
	
	private final DBMap<K, V> backend;
	private final ResultSet set;
	private final PreparedStatement removeStatement;
	private int cursor = 0;
	private boolean started = false;

	/**
	 * Initialisiert einen Iterator. Ab diesen Zeitpunkt wird bereits ein
	 * ResultSet geöffnet. Dies sollte theoretisch dazu führen, dass dieser
	 * Iterator über eine <b>Momentaufnahme</b> iteriert. Konkurierende
	 * Datenbankzugriffe sollten daher keine weiteren Auswirkungen auf die
	 * aktuelle Instanz haben!
	 * 
	 * @param backend
	 * @throws SQLException
	 */
	public DBMapEntrySetIterator(DBMap<K, V> backend) throws SQLException{
		this.backend = backend;
		this.set = initSet();
		this.removeStatement = initRemoveStatement();
	}
	
	@Override
	protected void finalize() throws Throwable {
		try{set.close();}catch(SQLException e){}
		try{removeStatement.close();}catch(SQLException e){}
		
		super.finalize();
	}
	
	private ResultSet initSet() throws SQLException{
		return backend.connection.createStatement()
			.executeQuery("SELECT * FROM " + backend.tableName);
	}
	
	private PreparedStatement initRemoveStatement() throws SQLException{
		return backend.connection.prepareStatement(
				"DELETE FROM " + backend.tableName +
				" WHERE " + COL_ID + " = ?" +
				" AND " + COL_KEY_TYPE + " = ?");
	}
	
	@Override
	public boolean hasNext() {
		return cursor < backend.size();
	}

	@Override
	public Entry<K, V> next() {
		try{
			if(!set.next()){
				throw new NoSuchElementException();
			}
		}catch(SQLException e){
			throw new BackendException(
					"Could not iterate to next.", e);
		}

		started = true;
		cursor++;
		
		return new DBMapEntry<K, V>(
				backend,
				getCurrentKey(),
				getCurrentValue());
	}
	
	private K getCurrentKey(){
		try{
			Class<?> keyClass = extractKeyClassFromSet();
			return extractKeyFromSet(keyClass);
		}catch(Exception e){
			throw new BackendException(
					"Could not extract key from backend!", e);
		}
	}
	
	private V getCurrentValue(){
		try{
			Class<?> valueClass = extractValueClassFromSet();
			return extractValueFromSet(valueClass);
		}catch(Exception e){
			throw new BackendException(
					"Could not extract value from backend!", e);
		}
	}
	
	private Class<?> extractKeyClassFromSet() 
			throws SQLException, ClassNotFoundException{
		
		String keyClass = set.getString(COL_KEY_TYPE);
		
		if(keyClass == null) return null;
		return Class.forName(keyClass);
	}
	
	private Class<?> extractValueClassFromSet()
			throws SQLException, ClassNotFoundException{
		
		String valueClass = set.getString(COL_VALUE_TYPE);
		
		if(valueClass == null) return null;
		return Class.forName(valueClass);
	}
	
	private K extractKeyFromSet(Class<?> keyClass) throws Exception{
		@SuppressWarnings("unchecked")
		Interpreter<K> keyInterpreter = (Interpreter<K>)
				InterpreterProvider.getInstance()
				.getInterpreter(keyClass);

		String colName = keyInterpreter.getKeyColumnName();
		return keyInterpreter.getFromResultSet(set, colName);
	}

	private V extractValueFromSet(Class<?> valueClass) throws Exception{
		@SuppressWarnings("unchecked")
		Interpreter<V> valueInterpreter = (Interpreter<V>)
				InterpreterProvider.getInstance()
				.getInterpreter(valueClass);
		
		String colName = valueInterpreter.getValueColumnName();
		return valueInterpreter.getFromResultSet(set, colName);
	}
	
	@Override
	public void remove() {
		if(!started) throw new NoSuchElementException(
				"You must call next() before you can remove anything!");
		
		try{
			String keyString = set.getString(COL_KEY_TYPE);
			Long keyHash = set.getLong(COL_ID);
			
			removeStatement.setLong(1, keyHash);
			removeStatement.setString(2, keyString);
			removeStatement.executeUpdate();
		}catch(SQLException e){
			throw new BackendException(
					"Could not remove current entity!", e);
		}
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
		DBMapEntrySetIterator other = (DBMapEntrySetIterator) obj;
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