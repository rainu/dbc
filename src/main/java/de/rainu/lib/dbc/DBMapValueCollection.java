package de.rainu.lib.dbc;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import de.rainu.lib.dbc.exception.BackendException;

/**
 * Dies ist die Value-{@link Collection}-Implementierung für die
 * {@link DBMap}. Diese Klasse ist von der abstrakten Collection-Klasse 
 * abgeleitet. Es stellt sich hier jedoch die Aufgabe, dass man den 
 * Iterator so weit es geht <b>vermeidet</b>, da so unnötigerweise die 
 * Daten aus der Datenkbank wieder in den Speicher geladen werden!
 * 
 * @author rainu
 *
 * @param <E> Typ des Wertes (Muss serialisierbar sein)
 */
public class DBMapValueCollection<E> 
	extends AbstractCollection<E>
	implements Collection<E>, ColumnNames{

	private final DBMap<?, E> backend;
	private PreparedStatement removeStatement;
	private PreparedStatement removeNullStatement;
	
	public DBMapValueCollection(DBMap<?, E> backend) throws SQLException{
		this.backend = backend;
		initStatements();
	}
	
	@Override
	protected void finalize() throws Throwable {
		try{removeStatement.close();}catch(SQLException e){}
		try{removeNullStatement.close();}catch(SQLException e){}
		
		super.finalize();
	}
	
	private void initStatements() throws SQLException{
		removeStatement = backend.connection.prepareStatement(
				"DELETE FROM " + backend.tableName +
				" WHERE " + COL_VALUE_HASH + " = ?" +
				" AND " + COL_VALUE_TYPE + " = ?");
		removeNullStatement = backend.connection.prepareStatement(
				"DELETE FROM " + backend.tableName +
				" WHERE " + COL_VALUE_HASH + " IS NULL" +
				" AND " + COL_VALUE_TYPE + " IS NULL");
	}
	
	@Override
	public Iterator<E> iterator() {
		try {
			return new DBMapValueIterator<E>(backend);
		} catch (SQLException e) {
			throw new BackendException(
					"Could not initialize iterator.", e);
		}
	}
	
	@Override
	public boolean remove(Object o) {
		try{
			if(o != null) return removeObject(o);
			else return removeNull();
		}catch(SQLException e){
			throw new BackendException(
					"Could not remove values from backend.", e);
		}
	}

	private boolean removeObject(Object o) throws SQLException {
		removeStatement.setLong(1, o.hashCode());
		removeStatement.setString(2, o.getClass().getName());
		
		return removeStatement.executeUpdate() > 0;
	}
	
	private boolean removeNull() throws SQLException {
		return removeNullStatement.executeUpdate() > 0;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		if(c == this){
			boolean change = !isEmpty();
			
			clear();
			
			return change;
		}
		if(c == null) throw new NullPointerException();
		
		boolean modified = false;
		for(Object curValue : c){
			modified |= remove(curValue);
		}
		
		return modified;
	}
	
	@Override
	public int size() {
		return backend.size();
	}
	
	@Override
	public boolean contains(Object o) {
		return backend.containsValue(o);
	}
	
	@Override
	public void clear() {
		backend.clear();
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return backend.retainAllValues(c);
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
		DBMapValueCollection other = (DBMapValueCollection) obj;
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
