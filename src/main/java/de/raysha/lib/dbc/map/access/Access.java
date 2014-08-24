package de.raysha.lib.dbc.map.access;

import de.raysha.lib.dbc.ColumnNames;

/**
 * Die Implementierungen dieses Interfaces kapseln den Zugriff auf
 * die Datenbank. Je nach Value-Typ müssen die Daten in andere Spalten
 * geschrieben werden. 
 * 
 * @author rainu
 */
public interface Access extends ColumnNames{
	
	/**
	 * Liefert den Wert aus der Datenbank.
	 * 
	 * @param key Schlüssel unter dem der Wert gespeichert wurde.
	 * @return Wert in der Datenbank. Ist kein Eintrag vorhanden wird <b>null</b> geliefert.
	 * @throws Exception Wenn ein Fehler auftrat.
	 */
	public Object get(Object key) throws Exception;
	
	/**
	 * Fügt einen <b>neuen</b> Eintrag in die Datenbank hinzu.
	 * @param key Schlüssel unter dem der Eintrag hinterlegt werden soll.
	 * @param value Wert, welcher hinterlegt werden soll.
	 * @throws Exception Wenn ein Fehler auftrat.
	 */
	public void add(Object key, Object value) throws Exception;
	
	/**
	 * Aktualisiert einen <b>bestehenden</b> Eintrag in der Datenbank.
	 * @param key Schlüssel unter dem der Wert zu finden ist.
	 * @param value Neuer Wert, der gespeichert werden soll.
	 * @throws Exception Wenn ein Fehler auftrat.
	 */
	public void update(Object key, Object value) throws Exception;
}
