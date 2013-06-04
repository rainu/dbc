package de.rainu.lib.dbc.map.interpreter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.rainu.lib.dbc.ColumnNames;

public class FloatInterpreter implements Interpreter<Float>,
											ColumnNames{

	@Override
	public String getValueColumnName() {
		return COL_FLOAT_VALUE;
	}

	@Override
	public String getKeyColumnName() {
		return COL_FLOAT_KEY;
	}

	@Override
	public void setParameter(PreparedStatement stmt, 
			int index, Object value) throws SQLException{
		
		stmt.setFloat(index, (Float)value);
	}

	@Override
	public Float getFromResultSet(
			ResultSet result, int index) throws SQLException {
		
		return result.getFloat(index);
	}

	@Override
	public Float getFromResultSet(ResultSet result, String columnLabel)
			throws Exception {
		
		return result.getFloat(columnLabel);
	}
}
