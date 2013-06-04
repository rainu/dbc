package de.rainu.lib.dbc.map.interpreter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.rainu.lib.dbc.ColumnNames;

public class DoubleInterpreter implements Interpreter<Double>,
											ColumnNames{

	@Override
	public String getValueColumnName() {
		return COL_DOUBLE_VALUE;
	}

	@Override
	public String getKeyColumnName() {
		return COL_DOUBLE_KEY;
	}

	@Override
	public void setParameter(PreparedStatement stmt, 
			int index, Object value) throws SQLException{
		
		stmt.setDouble(index, (Double)value);
	}

	@Override
	public Double getFromResultSet(
			ResultSet result, int index) throws SQLException {
		
		return result.getDouble(index);
	}

	@Override
	public Double getFromResultSet(ResultSet result, String columnLabel)
			throws Exception {
		
		return result.getDouble(columnLabel);
	}
}
