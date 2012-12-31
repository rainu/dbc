package de.rainu.lib.dbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import de.rainu.lib.dbc.DBMap;
import de.rainu.lib.dbc.DBMapEntrySetIterator;
import de.rainu.lib.dbc.beans.ConnectionInfo;

public class DBMapEntrySetIteratorBlackboxTest {
	private final static String DB_PATH = "/tmp/dbc";
	
	DBMap<Integer, String> backend;
	Iterator<Entry<Integer, String>> toTest;
	
	@Before
	public void before() throws SQLException{
		//die (bestehende) Datenbank vorher entfernen
		new File(DB_PATH + ".h2.db").delete();
		backend = new DBMap<Integer, String>(new ConnectionInfo(
				"org.h2.Driver", 
				"jdbc:h2:" + DB_PATH, 
				"sa", 
				""));
		
		toTest = new DBMapEntrySetIterator<Integer, String>(backend);
	}	
	
	@Test
	public void hasNext(){
		assertFalse(toTest.hasNext());
		
		backend.put(1, "1");
		assertTrue(toTest.hasNext());
	}
	
	@Test
	public void next() throws SQLException{
		try{
			toTest.next();
			fail("It should be thrown an exception!");
		}catch(NoSuchElementException e){}
		
		backend.put(1, "1");
		toTest = new DBMapEntrySetIterator<Integer, String>(backend);
		
		Entry<Integer, String> result = toTest.next();
		assertEquals("1", result.getValue());
		assertTrue(1 == result.getKey());
		
		backend.clear();
		toTest = new DBMapEntrySetIterator<Integer, String>(backend);
		backend.put(1, "1");
		
		try{
			toTest.next();
			//hier kommt der Effekt der Momentaufnahme zu tragen! 
			//(siehe DOkumentation des Konstruktors)
			fail("It should be thrown an exception!");
		}catch(NoSuchElementException e){}
	}
	
	@Test
	public void remove() throws SQLException{
		try{
			toTest.remove();
			fail("It should be thrown an exception!");
		}catch(NoSuchElementException e){}
		
		backend.put(1, "1");
		toTest = new DBMapEntrySetIterator<Integer, String>(backend);
		toTest.next();
		toTest.remove();
		
		assertTrue(backend.isEmpty());
	}
}
