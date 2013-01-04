package de.rainu.lib.dbc;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import de.rainu.lib.dbc.DBMap;
import de.rainu.lib.dbc.beans.ConnectionInfo;
import de.rainu.lib.dbc.beans.DBMapEntry;

public class DBMapEntryBlackboxTest {
	private final static String DB_PATH = "/tmp/dbc";
	
	DBMap<Integer, String> backend;
	Entry<Integer, String> toTest;
	final Integer key = 13121989;
	final String initValue = "EntryTest";
	
	@Before
	public void before(){
		//die (bestehende) Datenbank vorher entfernen
		new File(DB_PATH + ".h2.db").delete();
		backend = new DBMap<Integer, String>(new ConnectionInfo(
				"org.h2.Driver", 
				"jdbc:h2:" + DB_PATH, 
				"sa", 
				""));
		
		toTest = new DBMapEntry<Integer, String>(backend, key, initValue);
	}
	
	@Test
	public void getKey(){
		assertEquals(key, toTest.getKey());
	}
	
	@Test
	public void getValue(){
		assertEquals(initValue, toTest.getValue());
	}
	
	@Test
	public void setValue(){
		assertEquals(initValue, toTest.getValue());
		
		final String newValue = initValue + "_New";
		toTest.setValue(newValue);
		assertEquals(newValue, toTest.getValue());
		
		assertEquals(newValue, backend.get(key));
	}
}
