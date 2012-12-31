package de.rainu.lib.dbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import de.rainu.lib.dbc.beans.ConnectionInfo;
import de.rainu.lib.dbc.exception.BackendException;

public abstract class AbstractDBMap<K, V extends Serializable> implements Map<K, V>{
	private static final Map<Class<?>, Integer> RUNNING_INSTANCE_COUNT = new HashMap<Class<?>, Integer>();
	protected final String tableName;
	protected final Connection connection;
	private String jdbcUrl;
	
	protected AbstractDBMap(
			ConnectionInfo info,
			String tableName){
		
		loadClass(info.getClassName());
		this.connection = establisConnection(info);
		this.tableName = tableName == null ? 
				constructTableName() : tableName;
	}
	
	private void loadClass(String className){
		try {
			Class.forName(className);
		} catch (Exception e) {
			throw new BackendException("Could not load drive " + className, e);
		}
	}
	
	private Connection establisConnection(ConnectionInfo info) {
		try {
			return DriverManager
					.getConnection(info.getJdbcUrl(),
							info.getUser(), info.getPw());
		} catch (SQLException e) {
			throw new BackendException("Could not get connection: " + info, e);
		}
	}
	
	private int getInstanceCount() {
		//initialisierung
		if(!RUNNING_INSTANCE_COUNT.containsKey(getClass())){
			RUNNING_INSTANCE_COUNT.put(getClass(), 0);
		}
		
		int count = RUNNING_INSTANCE_COUNT.get(getClass());
		count++;
		RUNNING_INSTANCE_COUNT.put(getClass(), count);
		
		return count;
	}
	
	private String constructTableName(){
		int iCount = getInstanceCount();
		return getClass().getSimpleName() + "_" + iCount;
	}
		
	@Override
	protected void finalize() throws Throwable {
		if(connection != null){
			connection.close();
		}
		
		super.finalize();
	}
	
	public boolean isEmpty() {
		return size() <= 0;
	}

	protected String getJdbcUrl() {
		if(jdbcUrl == null){
			jdbcUrl = "unknown";
			try {
				jdbcUrl = connection.getMetaData().getURL();
			} catch (SQLException e) {}
		}
		
		return jdbcUrl;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		AbstractDBMap other = (AbstractDBMap) obj;
		if (getJdbcUrl() == null) {
			if (other.getJdbcUrl() != null)
				return false;
		} else if (!getJdbcUrl().equals(other.getJdbcUrl()))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getJdbcUrl() == null) ? 0 : getJdbcUrl().hashCode());
		result = prime * result
				+ ((tableName == null) ? 0 : tableName.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return 
			"[" + getClass().getSimpleName() + "]\n" +
			"Jdbc-URL: " + getJdbcUrl() + "\n" +
			"Table-Name: " + tableName;
	}
}
