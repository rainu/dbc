package de.raysha.lib.dbc.map.interpreter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.raysha.lib.dbc.ColumnNames;

public class IntegerInterpreter implements Interpreter<Integer>,
											ColumnNames{

	@Override
	public String getValueColumnName() {
		return COL_INT_VALUE;
	}

	@Override
	public String getKeyColumnName() {
		return COL_INT_KEY;
	}

	@Override
	public void setParameter(PreparedStatement stmt, 
			int index, Object value) throws SQLException{
		
		stmt.setInt(index, (Integer)value);
	}

	@Override
	public Integer getFromResultSet(
			ResultSet result, int index) throws SQLException {
		
		return result.getInt(index);
	}

	@Override
	public Integer getFromResultSet(ResultSet result, String columnLabel)
			throws Exception {
		
		return result.getInt(columnLabel);
	}
}
