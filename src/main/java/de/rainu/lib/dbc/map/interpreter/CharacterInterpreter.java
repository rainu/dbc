package de.rainu.lib.dbc.map.interpreter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.rainu.lib.dbc.ColumnNames;

public class CharacterInterpreter implements Interpreter<Character>,
											ColumnNames{

	@Override
	public String getValueColumnName() {
		return COL_CHAR_VALUE;
	}

	@Override
	public String getKeyColumnName() {
		return COL_CHAR_KEY;
	}

	@Override
	public void setParameter(PreparedStatement stmt, 
			int index, Object value) throws SQLException{
		
		stmt.setString(index, value != null ? String.valueOf(value) : null);
	}

	@Override
	public Character getFromResultSet(
			ResultSet result, int index) throws SQLException {
		
		String value = result.getString(index);
		if(value != null && !value.isEmpty()){
			return value.charAt(0);
		}
		
		return null;
	}

	@Override
	public Character getFromResultSet(ResultSet result, String columnLabel)
			throws Exception {
		
		String value = result.getString(columnLabel);
		if(value != null && !value.isEmpty()){
			return value.charAt(0);
		}
		
		return null;
	}
}
