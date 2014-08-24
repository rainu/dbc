package de.raysha.lib.dbc.map.interpreter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.raysha.lib.dbc.ColumnNames;

public class BooleanInterpreter implements Interpreter<Boolean>,
											ColumnNames{

	@Override
	public String getValueColumnName() {
		return COL_BOOLEAN_VALUE;
	}

	@Override
	public String getKeyColumnName() {
		return COL_BOOLEAN_KEY;
	}

	@Override
	public void setParameter(PreparedStatement stmt, 
			int index, Object value) throws SQLException{
		
		stmt.setBoolean(index, (Boolean)value);
	}

	@Override
	public Boolean getFromResultSet(
			ResultSet result, int index) throws SQLException {
		
		return result.getBoolean(index);
	}

	@Override
	public Boolean getFromResultSet(
			ResultSet result, String columnLabel) throws SQLException {
		
		return result.getBoolean(columnLabel);
	}
}
