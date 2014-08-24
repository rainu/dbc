package de.raysha.lib.dbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import de.raysha.lib.dbc.exception.BackendException;
import de.raysha.lib.dbc.map.interpreter.Interpreter;
import de.raysha.lib.dbc.map.interpreter.InterpreterProvider;

/**
 * Dies ist der Key-Iterator für eine IFMap.
 * 
 * @author rainu
 *
 * @param <E> Muss serialisierbar sein
 */
public class DBMapKeyIterator<E> implements Iterator<E>,
											ColumnNames{
	private final DBMap<E, ?> backend;
	private Map<Class<?>, Integer> columnClassRelation = new HashMap<Class<?>, Integer>();
	private final ResultSet view;
	private final int size;
	private int index = 0;
	
	private E lastElement;
	
	public DBMapKeyIterator(DBMap<E, ?> backend) throws SQLException {
		this.backend = backend;
		
		size = getSize();
		view = initView();
	}

	@Override
	protected void finalize() throws Throwable {
		view.close();
		
		super.finalize();
	}
	
	private int getSize() throws SQLException{
		ResultSet countSet = backend.connection.createStatement()
			.executeQuery(
				"SELECT count(*) FROM " + backend.tableName);
		
		countSet.first();
		return countSet.getInt(1);
	}
	
	private ResultSet initView() throws SQLException{
		ResultSet set = backend.connection.createStatement()
				.executeQuery(
					"SELECT " +
					COL_KEY_TYPE + ", " +
					COL_KEY + ", " +
					COL_BYTE_KEY + ", " +
					COL_BOOLEAN_KEY + ", " +
					COL_CHAR_KEY + ", " +
					COL_DOUBLE_KEY + ", " +
					COL_FLOAT_KEY + ", " +
					COL_INT_KEY + ", " +
					COL_LONG_KEY + ", " +
					COL_STRING_KEY +
					" FROM " + backend.tableName);
		
		columnClassRelation.put(null, 2);
		columnClassRelation.put(Byte.class, 3);
		columnClassRelation.put(Boolean.class, 4);
		columnClassRelation.put(Character.class, 5);
		columnClassRelation.put(Double.class, 6);
		columnClassRelation.put(Float.class, 7);
		columnClassRelation.put(Integer.class, 8);
		columnClassRelation.put(Long.class, 9);
		columnClassRelation.put(String.class, 10);
		
		return set;
	}
	
	
	@Override
	public boolean hasNext() {
		return index < size;
	}

	@Override
	public E next() {
		index++;
		try {
			if(!view.next()){
				throw new NoSuchElementException();
			}
		} catch (SQLException e) {
			throw new NoSuchElementException();
		}
		
		try{
			Class<?> keyClass = getCurrentKeyClass();
			E key = getCurrentKey(keyClass);

			lastElement = key;
			return key;
		}catch(Exception e){
			throw new BackendException("Could not iterate to next item!", e);
		}
	}
	
	private Class<?> getCurrentKeyClass() throws SQLException, ClassNotFoundException{
		String className = view.getString(1);
		return Class.forName(className);
	}
	
	@SuppressWarnings("unchecked")
	private E getCurrentKey(Class<?> keyClass) throws Exception{
		Interpreter<?> keyInterpreter = InterpreterProvider.getInstance()
					.getInterpreter(keyClass);
		
		int targetColumnIndex = getCoulumnIndexForClass(keyClass);
		return (E)keyInterpreter.getFromResultSet(view, targetColumnIndex);
	}
	
	private int getCoulumnIndexForClass(Class<?> keyClass){
		if(columnClassRelation.containsKey(keyClass)){
			return columnClassRelation.get(keyClass);
		}else{
			return columnClassRelation.get(null);
		}
	}

	@Override
	public void remove() {
		if(lastElement == null) throw new IllegalStateException("Call next() before remove anything!");
		
		if(lastElement != null){
			backend.remove(lastElement);
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
		DBMapKeyIterator other = (DBMapKeyIterator) obj;
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
