package de.rainu.lib.dbc.map.interpreter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.rainu.lib.dbc.ColumnNames;

public class StringInterpreter implements Interpreter<String>,
											ColumnNames{

	@Override
	public String getValueColumnName() {
		return COL_STRING_VALUE;
	}

	@Override
	public String getKeyColumnName() {
		return COL_STRING_KEY;
	}

	@Override
	public void setParameter(PreparedStatement stmt, 
			int index, Object value) throws SQLException{
		
		stmt.setString(index, (String)value);
	}

	@Override
	public String getFromResultSet(
			ResultSet result, int index) throws SQLException {
		
		return result.getString(index);
	}

	@Override
	public String getFromResultSet(ResultSet result, String columnLabel)
			throws Exception {
		
		return result.getString(columnLabel);
	}
}
