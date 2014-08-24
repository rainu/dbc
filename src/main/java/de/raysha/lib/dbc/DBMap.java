package de.raysha.lib.dbc;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.raysha.lib.dbc.beans.ConnectionInfo;
import de.raysha.lib.dbc.beans.KeyValueClassPair;
import de.raysha.lib.dbc.exception.BackendException;
import de.raysha.lib.dbc.map.access.Access;
import de.raysha.lib.dbc.map.access.GeneralAccess;
import de.raysha.lib.dbc.map.access.NullAccess;
import de.raysha.lib.dbc.map.interpreter.Interpreter;
import de.raysha.lib.dbc.map.interpreter.InterpreterProvider;
import de.raysha.lib.dbc.meta.MetadataManager;

/**
 * Die DB(Database)-Map ist eine {@link Map} Implementierung.
 * Daten werden nicht in den Hauptspeicher sondern direkt in eine Datenbank 
 * hinterlegt. Bei dem Schlüssel ist zu beachten, dass der Hash-Wert
 * als Schlüssel zur Suche des Wertes verwendet wird. Außerdem werden die Objekte
 * (Schlüssel sowie als auch Wert) als MOMENT-AUFNAHME gespeichert. Das heißt, 
 * dass Änderungen am Objekt nicht automatisch in die Datenbank aktualisiert werden. 
 * Änderungen müssen manuell über den Put-Aufruf getätigt werden.
 * 
 * @author rainu
 *
 * @param <K> Typ des Schlüssels (Muss serialisierbar sein)
 * @param <V> Typ des Wertes (Muss serialisierbar sein)
 */
public class DBMap<K, V> extends AbstractDBMap<K, V> implements ColumnNames{
	private static final Map<String, Integer> cachedSizes = new HashMap<String, Integer>();
	
	private PreparedStatement containsKeyStatement;
	private PreparedStatement valueClassByKeyStatement;
	private PreparedStatement countStatement;
	private PreparedStatement containsValueStatement;
	private PreparedStatement containsNullStatement;
	private PreparedStatement clearStatement;
	private PreparedStatement removeStatement;
	
	private Map<KeyValueClassPair, Access> cachedAccess = new HashMap<KeyValueClassPair, Access>();
	
	protected final boolean debugMode;
	protected final boolean dropIfExists;
	protected boolean cacheSize;
	
	protected final MetadataManager metadataManager;
	
	public DBMap(ConnectionInfo info, String tableName, boolean dropIfExist, boolean debugMode){
		super(info, tableName);
		
		this.dropIfExists = dropIfExist;
		this.debugMode = debugMode;
		cacheSize(false);

		this.metadataManager = initMetadataManager();
		init();
	}

	private MetadataManager initMetadataManager() {
		try {
			return new MetadataManager(connection);
		} catch (SQLException e) {
			throw new BackendException("Could not initialise metadata-manager.", e);
		}
	}
	
	public DBMap(ConnectionInfo info, String tableName, boolean dropIfExist){
		this(info, tableName, dropIfExist, false);
	}
	
	public DBMap(ConnectionInfo info, String tableName){
		//Ich geh davon aus, wenn man diese beiden Werte schon definiert, dass man auch möchte,
		//dass die Tabelle nicht gelöscht wird
		this(info, tableName, false);
	}
	
	public DBMap(ConnectionInfo info){
		//Standarmäßig soll die Tabelle geleert werden, fals sie schon existiert
		this(info, null, true, false);
	}
	
	private void init(){
		createTable();
		checkMetadata();
		
		try{
			createPreparedStatements();
		}catch(SQLException e){
			throw new BackendException("Could not create prepared statments!", e);
		}
	}
	
	private void createTable(){
		try{
			connection.createStatement()
				.execute("CREATE TABLE " + tableName + " (" +
						COL_ID + " INT," +
						COL_KEY + " BLOB," +
						COL_KEY_STRING + " CLOB," +
						COL_VALUE + " BLOB," +
						COL_VALUE_STRING + " CLOB," +
						COL_KEY_TYPE + " NVARCHAR(MAX)," +
						COL_VALUE_TYPE + " NVARCHAR(MAX)," +
						COL_VALUE_HASH + " INT," +
						
						COL_INT_KEY + " INT," +
						COL_LONG_KEY + " BIGINT," +
						COL_FLOAT_KEY + " REAL," +
						COL_DOUBLE_KEY + " DOUBLE," +
						COL_BYTE_KEY + " TINYINT," +
						COL_CHAR_KEY + " NVARCHAR(1)," +
						COL_BOOLEAN_KEY +	" BOOLEAN," +
						COL_STRING_KEY + " CLOB," +
						
						COL_INT_VALUE + " INT," +
						COL_LONG_VALUE + " BIGINT," +
						COL_FLOAT_VALUE + " REAL," +
						COL_DOUBLE_VALUE + " DOUBLE," +
						COL_BYTE_VALUE + " TINYINT," +
						COL_CHAR_VALUE + " NVARCHAR(1)," +
						COL_BOOLEAN_VALUE +	" BOOLEAN," +
						COL_STRING_VALUE + " CLOB," +
						
						"PRIMARY KEY (" + COL_ID + ", " + COL_KEY_TYPE +")" +
						")");
		}catch(SQLException e){
			//Möglicherweise ist die Tabelle schon vorhanden
			//darf aber nur geleert werden, wenn die Tabelle eigentlich
			//nach dem herunterfahren wieder geleert werden sollte
			if(dropIfExists) {
				try{
					connection.createStatement().execute("TRUNCATE TABLE " + tableName);
				}catch (SQLException e1) {
					throw new BackendException("Could not truncate existing table!", e1);
				}
			}
		}
	}

	private void checkMetadata(){
		if(metadataManager.getTableMetadata(tableName) != null){
			metadataManager.removeMetadata(tableName);
		}
		
		metadataManager.insertMetadata(tableName, "1.0");
	}
	
	private void createPreparedStatements() throws SQLException{
		containsKeyStatement = connection.prepareStatement(
				"SELECT count(*) " +
				" FROM " + tableName + 
				" WHERE " + COL_ID + " = ? and " + COL_KEY_TYPE + " = ?");
		valueClassByKeyStatement = connection.prepareStatement(
				"SELECT " + COL_VALUE_TYPE + 
				" FROM " + tableName + 
				" WHERE " + COL_ID + " = ? and " +
					COL_KEY_TYPE + " = ?");
		countStatement = connection.prepareStatement(
				"SELECT count(*)" +
				" FROM " + tableName);
		containsValueStatement = connection.prepareStatement(
				"SELECT count(*)" +
				" FROM " + tableName +
				" WHERE " + COL_VALUE_HASH + " = ? and " +
					COL_VALUE_TYPE + " = ?");
		containsNullStatement = connection.prepareStatement(
				"SELECT count(*)" +
				" FROM " + tableName +
				" WHERE " + COL_BOOLEAN_VALUE + " IS NULL and " +
						COL_BYTE_VALUE + " IS NULL and " +
						COL_CHAR_VALUE + " IS NULL and " +
						COL_DOUBLE_VALUE + " IS NULL and " +
						COL_FLOAT_VALUE + " IS NULL and " +
						COL_INT_VALUE + " IS NULL and " +
						COL_LONG_VALUE + " IS NULL and " +
						COL_STRING_VALUE + " IS NULL and " +
						COL_VALUE + " IS NULL");
		clearStatement = connection.prepareStatement(
				"TRUNCATE TABLE " + tableName);
		removeStatement = connection.prepareStatement(
				"DELETE FROM " + tableName + 
				" WHERE " + COL_ID + " = ? and " +
						COL_KEY_TYPE + " = ?");
	}
	
	@Override
	public V put(K key, V value) {
		if(key == null) return null;

		//laut map-"spezifikation" soll man den Wert, der
		//vorher gespeichert war, zurückliefern
		V preValue = get(key);
		
		try{
			Access access = getAccess(key.getClass(), 
					value != null ? value.getClass() : null);
			
			//aufgabe an access deligieren
			if(containsKey(key)){
				access.update(key, value);
			}else{
				access.add(key, value);
				resetCachedSize();
			}
		}catch(Exception e){
			throw new BackendException("Could not put value into backend!", e);
		}
		
		return preValue;
	}
	
	protected Interpreter<?> getInterpreter(Class<?> clazz){
		return InterpreterProvider.getInstance().getInterpreter(clazz);
	}
	
	protected Access getAccess(Class<?> keyClass, Class<?> valueClass) throws SQLException{
		Access access = null;
		
		if(valueClass != null){
			access = getGeneralAccess(keyClass, valueClass);
		}else{
			access = getNullAccess(keyClass);
		}
		
		return access;
	}
	
	protected Access getGeneralAccess(Class<?> keyClass, Class<?> valueClass) throws SQLException {
		KeyValueClassPair pair = new KeyValueClassPair(keyClass, valueClass);
		if(!cachedAccess.containsKey(pair)){
			Access access = new GeneralAccess(connection, tableName, debugMode,
					getInterpreter(keyClass),
					getInterpreter(valueClass));
			
			cachedAccess.put(pair, access);
		}
		
		return cachedAccess.get(pair);
	}

	protected Access getNullAccess(Class<?> keyClass) throws SQLException {
		KeyValueClassPair pair = new KeyValueClassPair(keyClass, null);
		if(!cachedAccess.containsKey(pair)){
			Access access = new NullAccess(connection, tableName, 
					getInterpreter(keyClass));
			
			cachedAccess.put(pair, access);
		}
		
		return cachedAccess.get(pair);
	}

	@Override
	public boolean containsKey(Object key) {
		if(key == null) return false;
		
		try{
			containsKeyStatement.setInt(1, key.hashCode());
			containsKeyStatement.setString(2, key.getClass().getName());
			ResultSet result = containsKeyStatement.executeQuery();
			
			result.first();
			return result.getInt(1) > 0;
		}catch(SQLException e){
			throw new BackendException("Could not communicate with backend!", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		if(key == null) return null;
		if(!containsKey(key)) return null;
		
		try {
			Class<?> valueClass = getValueClassByKey(key);
			Access access = getAccess(key.getClass(), valueClass);
			
			return (V) access.get((Serializable)key);
		} catch (Exception e) {
			throw new BackendException("Could not get data from backend!", e);
		}
	}
	
	protected Class<?> getValueClassByKey(Object key){
		try{
			valueClassByKeyStatement.setInt(1, key.hashCode());
			valueClassByKeyStatement.setString(2, key.getClass().getName());
			
			ResultSet result = valueClassByKeyStatement.executeQuery();
			result.first();
			
			String className = result.getString(1);
			return className != null ? Class.forName(className) : null;
		}catch(Exception e){
			throw new BackendException("Could not determine value-class by key(" + key + ")!", e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void putAll(Map<? extends K, ? extends V> m) {
		if(m == this) return;	//vermeiden, dass man in sich selbst putet
		
		if(m != null){
			if(m instanceof DBMap){
				try {
					putAllSQL((DBMap)m);
				} catch (SQLException e) {
					throw new BackendException("Coult not transfer values!", e);
				}
			}else{
				putAllLoop(m);
			}
			
			resetCachedSize();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void putAllSQL(DBMap source) throws SQLException{
		final String TRANSFER_STATEMENT = 
				"INSERT INTO " + tableName + " SELECT * FROM " + source.tableName;
		final String DELETE_STATEMENT = 
				"DELETE FROM " + tableName + " WHERE " + COL_ID + " IN (" +
						"SELECT " + COL_ID + " FROM " + source.tableName + 
					")";
		
		connection.createStatement().execute(DELETE_STATEMENT);
		connection.createStatement().execute(TRANSFER_STATEMENT);
	}

	private void putAllLoop(Map<? extends K, ? extends V> m) {
		for(Entry<? extends K, ? extends V> curEntry : m.entrySet()){
			put(curEntry.getKey(), curEntry.getValue());
		}
	}
	
	private String constructCachedSizesKey(){
		return getJdbcUrl() + tableName;
	}
	
	/**
	 * Diese Methode setzt den zwischengespeicherten
	 * Size-Wert zurück, sodass beim nächsten Size()
	 * der Wert aus der Datenbank neu gelesen wird.
	 * 
	 * Size() ist eine sehr häufig verwendete
	 * Methode. Jedesmal ein SQL-Statement ab-
	 * zusenden kostet unnötige Zeit. Da wir hier
	 * die volle Kontrolle über die Schreib-Methoden
	 * haben, wissen wir genau, wann sich die Größe
	 * ändert.
	 */
	void resetCachedSize(){
		final String cachedSizesKey = constructCachedSizesKey();
		
		cachedSizes.remove(cachedSizesKey);
	}
	
	@Override
	public int size() {
		if(!cacheSize) return getSize();
			
		//Size() ist eine sehr häufig verwendete
		//Methode. Jedesmal ein SQL-Statement ab-
		//zusenden kostet unnötige Zeit. Da wir hier
		//die volle Kontrolle über die Schreib-Methoden
		//haben, wissen wir genau, wann sich die Größe
		//ändert.
		final String cachedSizesKey = constructCachedSizesKey();
		
		if(!cachedSizes.containsKey(cachedSizesKey)){
			cachedSizes.put(cachedSizesKey, getSize());
		}
		
		return cachedSizes.get(cachedSizesKey);
	}
	
	private int getSize(){
		try{
			ResultSet result = countStatement.executeQuery();
			result.first();
			
			return result.getInt(1);
		}catch(SQLException e){
			throw new BackendException("Coult not determine count!", e);
		}
	}

	@Override
	public boolean containsValue(Object value) {
		try{
			if(value != null){
				return _containsVaule(value);
			}else{
				return containsNullValues();
			}
		}catch(SQLException e){
			throw new BackendException("Could not check if backend contains value!", e);
		}
	}
	
	private boolean _containsVaule(Object value) throws SQLException{
		containsValueStatement.setInt(1, value.hashCode());
		containsValueStatement.setString(2, value.getClass().getName());
		
		ResultSet result = containsValueStatement.executeQuery();
		result.first();
		
		return result.getInt(1) > 0;
	}
	
	private boolean containsNullValues() throws SQLException{
		ResultSet result = containsNullStatement.executeQuery();
		result.first();
		
		return result.getInt(1) > 0;
	}
	
	@Override
	public void clear() {
		try{
			clearStatement.execute();
			resetCachedSize();
		}catch(SQLException e){
			throw new BackendException("Could not clear backend!", e);
		}
	}

	@Override
	public V remove(Object key) {
		if(key == null) return null;
		
		try{
			//laut map-"Spezifikation" müssen wir den letzen gespeicherten
			//wert zurückliefern
			V preValue = get(key);
			
			removeStatement.setInt(1, key.hashCode());
			removeStatement.setString(2, key.getClass().getName());
			
			removeStatement.executeUpdate();
			resetCachedSize();
			
			return preValue;
		}catch(SQLException e){
			throw new BackendException("Could not delete entry from backend!", e);
		}
	}
	
	public boolean retainAllKeys(Collection<?> c){
		if(c == null) throw new NullPointerException();
		if(c.isEmpty()){
			boolean wasEmpty = isEmpty();
			clear();
			
			return !wasEmpty;
		}
		
		String sql = constructRetainStatement(c,
				COL_ID, COL_KEY_TYPE);
		try{
			int effectedRows = connection.createStatement()
				.executeUpdate(sql);
			
			resetCachedSize();
			return effectedRows > 0;
		}catch(SQLException e){
			throw new BackendException(
					"Could not execute statement for retainAll()", e);
		}
	}
	
	public boolean retainAllValues(Collection<?> c) {
		if(c == null) throw new NullPointerException();
		if(c.isEmpty()){
			boolean wasEmpty = isEmpty();
			clear();
			
			return !wasEmpty;
		}
		
		String sql = constructRetainStatement(c,
				COL_VALUE_HASH, COL_VALUE_TYPE);
		try{
			int effectedRows = connection.createStatement()
				.executeUpdate(sql);
			
			resetCachedSize();
			return effectedRows > 0;
		}catch(SQLException e){
			throw new BackendException(
					"Could not execute statement for retainAll()", e);
		}
	}
	
	private String constructRetainStatement(Collection<?> c,
			String colHash, String colType) {
		StringBuilder builder = new StringBuilder(
				"DELETE FROM " + tableName +
				" WHERE ");
		
		int i=0;
		for(Object curObject : c){
			if(curObject != null){
				builder.append("NOT ");
				appendObjectCondition(builder, curObject,
						colHash, colType);
			}else{
				builder.append("NOT ");
				appendNullCondition(builder,
						colHash, colType);
			}
			
			if((i + 1) < c.size()){
				builder.append(" AND ");
			}
			
			i++;
		}
		
		return builder.toString();
	}

	private void appendObjectCondition(StringBuilder builder, Object curObject,
			String colHash, String colType) {
		builder.append("(");
		builder.append(colHash);
		builder.append(" = ");
		builder.append(curObject.hashCode());
		builder.append(" AND ");
		builder.append(colType);
		builder.append(" = '");
		builder.append(curObject.getClass().getName());
		builder.append("')");
	}
	
	private void appendNullCondition(StringBuilder builder,
			String colHash, String colType) {
		builder.append("(");
		builder.append(colHash);
		builder.append(" IS NULL AND ");
		builder.append(colType);
		builder.append(" IS NULL)");
	}
	
	@Override
	public Set<K> keySet() {
		return new DBSet<K>(this);
	}

	@Override
	public Collection<V> values() {
		try {
			return new DBMapValueCollection<V>(this);
		} catch (SQLException e) {
			throw new BackendException(
					"Could not initialize values-collection.", e);
		}
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return new DBMapEntrySet<K, V>(this);
	}
	
	/**
	 * Legt fest, ob die Size (@see DBMap#size()) zwischengespeichert wird.
	 * Sollte die Größe nicht zwischengespeichert werden, wird bei jedem size()-
	 * Aufruf eine Datenbankabfrage ausgeführt! Man sollte die Größe zwischenspeichern,
	 * wenn man sicherstellen kann, dass kein anderer Zugriff auf die zugrunde liegende
	 * Datenbank stattfindet.
	 * 
	 * @param cacheSize True zwischenspeichert die Größe. Andernfals wird die Größe immer per
	 * SQL-Statement erfragt.
	 */
	public void cacheSize(boolean cacheSize){
		this.cacheSize = cacheSize;
	}
}
