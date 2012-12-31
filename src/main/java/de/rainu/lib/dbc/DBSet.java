package de.rainu.lib.dbc;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import de.rainu.lib.dbc.beans.ConnectionInfo;
import de.rainu.lib.dbc.exception.BackendException;

/**
 * Das DB(Database)-Set ist eine {@link Set} Implementierung.
 * Sie basiert auf der {@link DBMap} und deligiert entsprechend
 * alle Anfragen an diese weiter. Dazu nutzt diese Implementierung
 * die Tatsache, dass in einer Map keine gleichen Schlüssel gespeichert
 * werden können. Das heißt, dass dieses Set seine Objekte als
 * Schlüssel Speichert. Die Map sorgt dafür, dass es keine doppelten
 * Schlüssel gibt.
 * 
 * @author rainu
 *
 * @param <E>
 */
public class DBSet<E extends Serializable> 
	implements Set<E>, ColumnNames {
	
	private final DBMap<E, ?> backend;
	
	public DBSet(DBMap<E, ?> backend) {
		this.backend = backend;
	}
	
	public DBSet(ConnectionInfo info, String tableName, boolean dropIfExist, boolean debugMode){
		this(new DBMap<E, Serializable>(info, tableName, dropIfExist, debugMode));
	}
	
	public DBSet(ConnectionInfo info, String tableName, boolean dropIfExist){
		this(new DBMap<E, Serializable>(info, tableName, dropIfExist));
	}
	
	public DBSet(ConnectionInfo info, String tableName){
		this(new DBMap<E, Serializable>(info, tableName));
	}
	
	public DBSet(ConnectionInfo info){
		this(new DBMap<E, Serializable>(info));
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
		return backend.containsKey(o);
	}

	@Override
	public Iterator<E> iterator() {
		try{
			return new DBMapKeyIterator<E>(backend);
		}catch(SQLException e){
			throw new BackendException("Could not initialize backend-key-iterator!", e);
		}
	}

	@Override
	public Object[] toArray() {
		Object[] array = new Object[size()];
		
		int i=0;
		for(E curObject : this){
			array[i++] = curObject;
		}
		
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if(a == null) return null;
		if(size() > a.length) return (T[])Arrays.copyOf(toArray(), size(), a.getClass());
		
		Object[] array = toArray();
		for(int i=0; i < array.length; i++){
			a[i] = (T)array[i];
		}
		for(int i=array.length; i < a.length; i++){
			a[i] = null;
		}
		
		return a;
	}

	@Override
	public boolean add(E e) {
		if(!backend.containsKey(e)){
			backend.put(e, null);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean remove(Object o) {
		if(backend.containsKey(o)){
			backend.remove(o);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		if(c == this) return true;
		if(c == null) throw new NullPointerException();
		
		for(Object curObject : c){
			if(!contains(curObject)){
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if(c == this) return false;
		
		boolean changed = false;
		
		if(c == null) {
			throw new NullPointerException();
		}
		
		for(E curObject : c){
			if(add(curObject)){
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return backend.retainAllKeys(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if(c == this){
			boolean changed = size() > 0;
			clear();
			return changed;
		}
		
		boolean changed = false;
		if(c == null) throw new NullPointerException();
		
		for(Object toDelete : c){
			if(remove(toDelete)){
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public void clear() {
		backend.clear();
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
		DBSet other = (DBSet) obj;
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
