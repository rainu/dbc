package de.raysha.lib.dbc.map.interpreter;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import de.raysha.lib.dbc.ColumnNames;
import de.raysha.lib.dbc.ConvertHelper;

public class ObjectInterpreter implements Interpreter<Object>,
											ColumnNames{

	@Override
	public String getValueColumnName() {
		return COL_VALUE;
	}

	@Override
	public String getKeyColumnName() {
		return COL_KEY;
	}

	@Override
	public void setParameter(PreparedStatement stmt, 
			int index, Object value) throws Exception{
		
		stmt.setBinaryStream(index, ConvertHelper.convertObjectToInputStream(value));
	}

	@Override
	public Object getFromResultSet(
			ResultSet result, int index) throws Exception {
		
		InputStream in = result.getBinaryStream(index);
		try{
			return (Serializable)ConvertHelper.convertInputStreamToObject(in);
		}finally{
			if(in != null){
				in.close();
			}
		}
	}

	@Override
	public Serializable getFromResultSet(ResultSet result, String columnLabel)
			throws Exception {

		InputStream in = result.getBinaryStream(columnLabel);
		try{
			return (Serializable)ConvertHelper.convertInputStreamToObject(in);
		}finally{
			if(in != null){
				in.close();
			}
		}
	}
}
