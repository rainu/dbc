package de.rainu.lib.dbc.map.interpreter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Bei Update-/Insert-/Select- Statements unterscheided sich
 * dass man in unterschiedlichen Spalten nach dem entsprechenden
 * Wert schauen muss. Schlüssel- und Value-Werte werden je nach
 * Klasse in unterschiedlichen Spalten gepspeichert. Um nicht für
 * jede Kompination aus Schlüssel-Wert-Klassen eine extra Klasse
 * zu schreiben, die die Result-Sets befüllt und ausliest, sind
 * diese Interpreter gedacht. Sie liefern die entsprechenden 
 * Spaltennamen und handeln den Umgang mit den PreparedStatements.
 * 
 * @author rainu
 *
 * @param <T> der Typ für den der Interpreter zuständig ist.
 */
public interface Interpreter<T> {
	/**
	 * Liefert den Spaltennamen in dem der Wert gespeichert
	 * werden soll.
	 * 
	 * @return
	 */
	String getValueColumnName();
	
	/**
	 * Liefert den Schlüsselnamen in dem der Schlüssel-Wert
	 * gespeichert werden soll.
	 * 
	 * @return
	 */
	String getKeyColumnName();
	
	/**
	 * Setzt den Parameter in den angegebene Statement.
	 * 
	 * @param stmt Statement indem der Parameter gesetzt werden soll.
	 * @param index Index des Parameters, der gesetzt werden soll.
	 * @param value Wert der gesetzt werden soll.
	 * @throws Exception
	 */
	void setParameter(
			PreparedStatement stmt, 
			int index,
			Object value) throws Exception;
	
	/**
	 * Liest den Wert aus dem übergebenen ResultSets.
	 * 
	 * @param result ResultSet aus dem der Wert gelesen werden soll.
	 * @param index Index der Spalte unter dem der Wert zu finden sein soll.
	 * @return
	 * @throws Exception
	 */
	T getFromResultSet(ResultSet result, int index) throws Exception;
	
	/**
	 * Liest den Wert aus dem übergebenen ResultSets.
	 * 
	 * @param result ResultSet aus dem der Wert gelesen werden soll.
	 * @param columnLabel Name der Spalte unter dem der Wert zu finden sein soll.
	 * @return
	 * @throws Exception
	 */
	T getFromResultSet(ResultSet result, String columnLabel) throws Exception;
}
