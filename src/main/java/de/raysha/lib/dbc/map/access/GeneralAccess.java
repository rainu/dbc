package de.raysha.lib.dbc.map.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.raysha.lib.dbc.map.interpreter.Interpreter;

/**
 * Diese Klasse kapselt den Zugriff der Datenbank.
 * 
 * @author rainu
 */
public class GeneralAccess implements Access {
	protected PreparedStatement insertStatement;
	protected PreparedStatement updateStatement;
	protected PreparedStatement selectStatement;
	private final boolean debugMode;
	
	private final Interpreter<?> keyInterpreter;
	private final Interpreter<?> valueInterpreter;
	
	public GeneralAccess(Connection connection, String tableName, boolean debugMode, 
			Interpreter<?> keyProvider, Interpreter<?> valueProvider) throws SQLException{
		
		this.debugMode = debugMode;
		this.keyInterpreter = keyProvider;
		this.valueInterpreter = valueProvider;
		
		init(connection, tableName);
	}
	
	@Override
	protected void finalize() throws Throwable {
		try{insertStatement.close();}catch(SQLException e){}
		try{updateStatement.close();}catch(SQLException e){}
		try{selectStatement.close();}catch(SQLException e){}
		
		super.finalize();
	}
	
	private void init(Connection connection, String tableName) throws SQLException{
		if(debugMode){
			insertStatement = connection.prepareStatement(
					"INSERT INTO " + tableName + "(" + 
							COL_ID + ", " + 
							keyInterpreter.getKeyColumnName() + ", " + 
							COL_KEY_TYPE + ", " + 
							
							COL_VALUE_HASH + ", " +
							valueInterpreter.getValueColumnName() + ", " + 
							COL_VALUE_TYPE + ", " +
							
							COL_KEY_STRING + ", " +
							COL_VALUE_STRING + ")" +
						" VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
		}else{
			insertStatement = connection.prepareStatement(
					"INSERT INTO " + tableName + "(" + 
							COL_ID + ", " + 
							keyInterpreter.getKeyColumnName() + ", " + 
							COL_KEY_TYPE + ", " + 
							
							COL_VALUE_HASH + ", " +
							valueInterpreter.getValueColumnName() + ", " + 
							COL_VALUE_TYPE + ")" +
						" VALUES(?, ?, ?, ?, ?, ?)");
		}
		
		if(debugMode){
			updateStatement = connection.prepareStatement(
					"UPDATE " + tableName + 
					" SET " +
							valueInterpreter.getValueColumnName() + " = ?, " +
							 COL_VALUE_HASH + " = ?, " +
							 COL_VALUE_TYPE + " = ?, " +
						COL_VALUE_STRING + " = ?" +
					"WHERE " + COL_ID + " = ? and " + COL_KEY_TYPE + " = ?");
		}else{
			updateStatement = connection.prepareStatement(
					"UPDATE " + tableName + " " + 
						"SET " + 
								valueInterpreter.getValueColumnName() + " = ?, " +
								 COL_VALUE_HASH + " = ?, " +
								 COL_VALUE_TYPE + " = ? " +
						"WHERE " + COL_ID + " = ? and " + COL_KEY_TYPE + " = ?");
		}
		
		selectStatement = connection.prepareStatement(
				"SELECT " + valueInterpreter.getValueColumnName() + " FROM " + tableName + " " +
						"WHERE " + COL_ID + " = ? and " + COL_KEY_TYPE + " = ?");
	}

	@Override
	public void add(Object key, Object value) throws Exception {
		insertStatement.setInt(1, key.hashCode());
		keyInterpreter.setParameter(insertStatement, 2, key);
		insertStatement.setString(3, key.getClass().getName());
		insertStatement.setInt(4, value.hashCode());
		valueInterpreter.setParameter(insertStatement, 5, value);
		insertStatement.setString(6, value.getClass().getName());
		
		if(debugMode){
			insertStatement.setString(7, key.toString());
			insertStatement.setString(8, value.toString());
		}
		
		insertStatement.executeUpdate();
	}
	
	@Override
	public void update(Object key, Object value) throws Exception {
		valueInterpreter.setParameter(updateStatement, 1, value);
		updateStatement.setInt(2, value.hashCode());
		updateStatement.setString(3, value.getClass().getName());
		
		if(debugMode){
			updateStatement.setString(4, value.toString());
			updateStatement.setInt(5, key.hashCode());
			updateStatement.setString(6, key.getClass().getName());
		}else{
			updateStatement.setInt(4, key.hashCode());
			updateStatement.setString(5, key.getClass().getName());
		}
		
		updateStatement.executeUpdate();
	}
	
	@Override
	public Object get(Object key) throws Exception {
		selectStatement.setInt(1, key.hashCode());
		selectStatement.setString(2, key.getClass().getName());
		
		ResultSet set = selectStatement.executeQuery();
		
		if(set.first()){
			return valueInterpreter.getFromResultSet(set, 1);
		}
		
		return null;
	}

}
