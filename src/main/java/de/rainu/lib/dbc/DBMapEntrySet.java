package de.rainu.lib.dbc;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import de.rainu.lib.dbc.exception.BackendException;

/**
 * Dies ist die EntrySet-Implementierung für die {@link DBMap}.
 * Diese Klasse ist von der abstrakten Set-Klasse abgeleitet. Es
 * stellt sich hier jedoch die Aufgabe, dass man den Iterator so
 * weit es geht <b>vermeidet</b>, da so unnötigerweise die Daten
 * aus der Datenkbank wieder in den Speicher geladen werden!
 * 
 * @author rainu
 *
 * @param <K> Typ des Schlüssels
 * @param <V> Typ des Wertes
 */
public class DBMapEntrySet<K extends Serializable, V extends Serializable>
	extends AbstractSet<Entry<K, V>>
	implements Set<Entry<K, V>>, ColumnNames {

	private final DBMap<K, V> backend;
	
	public DBMapEntrySet(DBMap<K, V> backend){
		this.backend = backend;
	}
	
	@Override
	public boolean add(Entry<K, V> e) {
		boolean contains = backend.containsKey(e.getKey());
		backend.put(e.getKey(), e.getValue());
		
		return !contains;
	}

	@Override
	public boolean remove(Object o) {
		@SuppressWarnings("rawtypes")
		Entry entry = (Entry)o;
		
		return backend.remove(entry.getKey()) != null;
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
	public boolean contains(Object o) {
		@SuppressWarnings("rawtypes")
		Entry entry = (Entry)o;
		
		return backend.containsKey(entry.getKey());
	}
	
	@Override
	public Iterator<Entry<K, V>> iterator() {
		try {
			return new DBMapEntrySetIterator<K, V>(backend);
		} catch (SQLException e) {
			throw new BackendException(
					"Could not initialize iterator.", e);
		}
	}

	@Override
	public int size() {
		return backend.size();
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
		DBMapEntrySet other = (DBMapEntrySet) obj;
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
