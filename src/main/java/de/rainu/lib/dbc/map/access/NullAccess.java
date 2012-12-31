package de.rainu.lib.dbc.map.access;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.rainu.lib.dbc.map.interpreter.Interpreter;

/**
 * Diese Klasse kapselt den Zugriff auf Null-Values.
 * 
 * @author rainu
 */
public class NullAccess implements Access {
	protected PreparedStatement insertStatement;
	protected PreparedStatement updateStatement;
	protected Interpreter<?> keyInterpreter;
	
	public NullAccess(Connection connection, String tableName, Interpreter<?> keyInterpreter) throws SQLException{
		this.keyInterpreter = keyInterpreter;
		
		init(connection, tableName);
	}
	
	@Override
	protected void finalize() throws Throwable {
		try{insertStatement.close();}catch(SQLException e){}
		try{updateStatement.close();}catch(SQLException e){}
		
		super.finalize();
	}
	
	private void init(Connection connection, String tableName) throws SQLException{
		insertStatement = connection.prepareStatement(
					"INSERT INTO " + tableName + "(" + 
							COL_ID + ", " + 
							keyInterpreter.getKeyColumnName() + ", " + 
							COL_KEY_TYPE + ")" +
						" VALUES(?, ?, ?)");

		updateStatement = connection.prepareStatement(
				"UPDATE " + tableName + " " + 
					"SET " +
							COL_BOOLEAN_VALUE + " = NULL, " +
							COL_BYTE_VALUE + " = NULL, " +
							COL_CHAR_VALUE + " = NULL, " +
							COL_DOUBLE_VALUE + " = NULL, " +
							COL_FLOAT_VALUE + " = NULL, " +
							COL_INT_VALUE + " = NULL, " +
							COL_LONG_VALUE + " = NULL, " +
							COL_STRING_VALUE + " = NULL, " +
							COL_VALUE + " = NULL, " +
							COL_VALUE_HASH + " = NULL, " +
							COL_VALUE_TYPE + " = NULL " +
					"WHERE " + COL_ID + " = ? AND " + COL_KEY_TYPE + " = ?");
	}
	
	@Override
	public Serializable get(Serializable key) throws Exception {
		//diese klasse liefert immer null
		return null;
	}

	@Override
	public void add(Serializable key, Serializable value) throws Exception {
		insertStatement.setInt(1, key.hashCode());
		keyInterpreter.setParameter(insertStatement, 2, key);
		insertStatement.setString(3, key.getClass().getName());
		
		insertStatement.executeUpdate();
	}

	@Override
	public void update(Serializable key, Serializable value) throws Exception {
		updateStatement.setInt(1, key.hashCode());
		updateStatement.setString(2, key.getClass().getName());
		
		updateStatement.executeUpdate();
	}

}
