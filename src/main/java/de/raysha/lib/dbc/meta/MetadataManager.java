package de.raysha.lib.dbc.meta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.raysha.lib.dbc.exception.BackendException;

/**
 * Diese Klasse kümmert sich um das Management der Meta-Daten.
 * Zu diesem Zeitpunkt werden für jede Tabelle eine Versionsnummer
 * gespeichert. Dies hat den Zweck das man in zukünftigen Versionen
 * entsprechend handeln kann. Sollte sich am Tabellen-Schema in der
 * Zukunft etwas ändern, kann man somit ermitteln, auf welchem Stand
 * das aktuelle Tabellen-Schema besitzt und entsprechende Maßnahmen
 * ergreifen.
 * 
 * @author rainu
 *
 */
public class MetadataManager implements ColumnNames{
	public static final String TABLE_NAME = "DE_RAINU_METATABLE";

	private PreparedStatement tableMetadataStatement;
	private PreparedStatement metadataStatement;
	private PreparedStatement insertStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement removeStatement;
	
	public MetadataManager(Connection connection) throws SQLException{
		createTable(connection);
		initStatements(connection);
		insertOwnMetadata();
	}
	
	private void createTable(Connection connection){
		try{
			connection.createStatement().execute(
				"CREATE TABLE " + TABLE_NAME + " (" +
					COL_ID + " INT," +
					COL_TABLE_NAME + " NVARCHAR(MAX) NOT NULL," +
					COL_TABLE_VERSION + " VARCHAR(10) NOT NULL," +
					COL_TABLE_META_DATA + " CLOB," +
							
					"PRIMARY KEY(" + COL_ID + ")" +
				")");
		}catch(SQLException e){
			//die tabelle ist schon vorhanden
		}
	}
	
	private void initStatements(Connection connection) throws SQLException{
		this.tableMetadataStatement = connection.prepareStatement(
				"SELECT " + COL_ID + ", " + COL_TABLE_NAME + ", " + COL_TABLE_VERSION +
				" FROM " + TABLE_NAME +
				" WHERE " + COL_ID + " = ?");
		this.metadataStatement = connection.prepareStatement(
				"SELECT " + COL_TABLE_META_DATA +
				" FROM " + TABLE_NAME +
				" WHERE " + COL_ID + " = ?");
		this.insertStatement = connection.prepareStatement(
				"INSERT INTO " + TABLE_NAME +
				" (" + COL_ID + ", " + COL_TABLE_NAME + ", " + COL_TABLE_VERSION + ")" +
				"VALUES (?, ?, ?)");
		this.updateStatement = connection.prepareStatement(
				"UPDATE " + TABLE_NAME +
				" SET " + COL_TABLE_META_DATA + " = ?" +
				" WHERE " + COL_ID + " = ?");
		this.removeStatement = connection.prepareStatement(
				"DELETE FROM " + TABLE_NAME +
				" WHERE " + COL_ID + " = ?");
	}
	
	private void insertOwnMetadata(){
		if(getTableMetadata(TABLE_NAME) == null){
			insertMetadata(TABLE_NAME, "1.0");
		}
	}
	
	/**
	 * Liefert die Metadaten der gegebenen Tabelle.
	 * 
	 * @param tableName Name der Tabelle, deren Metadaten ermitetelt werden soll.
	 * @return <b>Null</b> wenn keine Daten gefunden werden konnten. ANdernfals die entsprechenden Metadaten.
	 */
	public TableMetadata getTableMetadata(String tableName){
		if(tableName == null) return null;
		
		try {
			tableMetadataStatement.setInt(1, tableName.hashCode());
			ResultSet set = tableMetadataStatement.executeQuery();
			
			return transformToTableMetadata(set);
		} catch (SQLException e) {
			throw new BackendException("Could not get TableMetadata for table '" + tableName + "'", e);
		}
	}
	
	private TableMetadata transformToTableMetadata(ResultSet set) throws SQLException{
		if(!set.next()) return null;
		
		TableMetadata bean = new TableMetadata(this);
		
		bean.setId(set.getLong(COL_ID));
		bean.setName(set.getString(COL_TABLE_NAME));
		bean.setVersion(set.getString(COL_TABLE_VERSION));
		
		return bean;
	}
	
	String getMetadata(Long metadataId){
		try{
			metadataStatement.setLong(1, metadataId);
			ResultSet set = metadataStatement.executeQuery();
			
			if(set.next()){
				return set.getString(COL_TABLE_META_DATA);
			}else{
				return null;
			}
		}catch(SQLException e){
			throw new BackendException("Could not read metadata by id " + metadataId, e);
		}
	}
	
	/**
	 * Fügt neue Metadaten einer Tabelle hinzu.
	 * 
	 * @param tableName Name der Tabelle.
	 * @param version Version der Tabelle.
	 * @return
	 */
	public TableMetadata insertMetadata(String tableName, String version){
		TableMetadata result = new TableMetadata(this);
		result.setId(new Long(tableName.hashCode()));
		result.setName(tableName);
		result.setVersion(version);
		
		try{
			insertStatement.setLong(1, result.getId());
			insertStatement.setString(2, result.getName());
			insertStatement.setString(3, result.getVersion());
			insertStatement.executeUpdate();
		}catch(SQLException e){
			throw new BackendException("Could not insert new metadata-entry: " + result, e);
		}
		
		return result;
		
	}
	
	/**
	 * Aktualisiert die Metadaten eines Eintrages.
	 * 
	 * @param tableName Name der Tabelle
	 * @param metadata Metadaten die eingetragen werden sollen.
	 */
	public void updateMetadata(String tableName, String metadata){
		try {
			updateStatement.setString(1, metadata);
			updateStatement.setLong(2, tableName.hashCode());
			updateStatement.executeUpdate();
		} catch (SQLException e) {
			throw new BackendException("Could not update metadata for table '" + tableName + "'", e);
		}
	}
	
	public void removeMetadata(String tableName){
		if(tableName == null) return;
		
		try{
			removeStatement.setInt(1, tableName.hashCode());
			removeStatement.executeUpdate();
		}catch(SQLException e){
			throw new BackendException("Could not delete metadata for table '" + tableName + "'", e);
		}
	}
}
