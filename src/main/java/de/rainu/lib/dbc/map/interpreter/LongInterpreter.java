package de.rainu.lib.dbc.map.interpreter;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.rainu.lib.dbc.ColumnNames;

public class LongInterpreter implements Interpreter<Long>,
											ColumnNames{

	@Override
	public String getValueColumnName() {
		return COL_LONG_VALUE;
	}

	@Override
	public String getKeyColumnName() {
		return COL_LONG_KEY;
	}

	@Override
	public void setParameter(PreparedStatement stmt, 
			int index, Serializable value) throws SQLException{
		
		stmt.setLong(index, (Long)value);
	}

	@Override
	public Long getFromResultSet(
			ResultSet result, int index) throws SQLException {
		
		return result.getLong(index);
	}

	@Override
	public Long getFromResultSet(ResultSet result, String columnLabel)
			throws Exception {
		
		return result.getLong(columnLabel);
	}
}
