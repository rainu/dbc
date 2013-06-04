package de.rainu.lib.dbc.map.interpreter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.rainu.lib.dbc.ColumnNames;

public class ByteInterpreter implements Interpreter<Byte>,
											ColumnNames{

	@Override
	public String getValueColumnName() {
		return COL_BYTE_VALUE;
	}

	@Override
	public String getKeyColumnName() {
		return COL_BYTE_KEY;
	}

	@Override
	public void setParameter(PreparedStatement stmt, 
			int index, Object value) throws SQLException{
		
		stmt.setByte(index, (Byte)value);
	}

	@Override
	public Byte getFromResultSet(
			ResultSet result, int index) throws SQLException {
		
		return result.getByte(index);
	}

	@Override
	public Byte getFromResultSet(ResultSet result, String columnLabel)
			throws Exception {

		return result.getByte(columnLabel);
	}
}
