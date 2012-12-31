package de.rainu.lib.dbc.beans;

import java.io.Serializable;
import java.util.Map.Entry;

import de.rainu.lib.dbc.DBMap;
import de.rainu.lib.dbc.DBMapEntrySet;

/**
 * Dies ist die {@link Entry}-Implementation für das {@link DBMapEntrySet}.
 * 
 * @author rainu
 *
 * @param <K> Typ des Schlüssels
 * @param <V> Typ des Wertes
 */
public class DBMapEntry<K extends Serializable, V extends Serializable>
	implements Entry<K, V> {

	private final DBMap<K, V> backend;
	private final K key;
	private V value;
	
	public DBMapEntry(DBMap<K, V> backend,
			K key, V value){
		this.backend = backend;
		this.key = key;
		this.value = value;
	}
	
	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V oldValue = this.value;
		backend.put(key, value);
		
		this.value = value;
		return oldValue;
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
		DBMapEntry other = (DBMapEntry) obj;
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
