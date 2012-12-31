package de.rainu.lib.dbc.beans;

public class KeyValueClassPair{
	private Class<?> keyClass;
	private Class<?> valueClass;
	
	public KeyValueClassPair(Class<?> keyClass, Class<?> valueClass) {
		super();
		this.keyClass = keyClass;
		this.valueClass = valueClass;
	}
	
	public Class<?> getKeyClass() {
		return keyClass;
	}
	public void setKeyClass(Class<?> keyClass) {
		this.keyClass = keyClass;
	}
	public Class<?> getValueClass() {
		return valueClass;
	}
	public void setValueClass(Class<?> valueClass) {
		this.valueClass = valueClass;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((keyClass == null) ? 0 : keyClass.hashCode());
		result = prime * result
				+ ((valueClass == null) ? 0 : valueClass.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyValueClassPair other = (KeyValueClassPair) obj;
		if (keyClass == null) {
			if (other.keyClass != null)
				return false;
		} else if (!keyClass.equals(other.keyClass))
			return false;
		if (valueClass == null) {
			if (other.valueClass != null)
				return false;
		} else if (!valueClass.equals(other.valueClass))
			return false;
		return true;
	}
}
