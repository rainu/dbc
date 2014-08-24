package de.raysha.lib.dbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import de.raysha.lib.dbc.DBMap;
import de.raysha.lib.dbc.DBMapKeyIterator;
import de.raysha.lib.dbc.beans.ConnectionInfo;

public class DBMapKeyIteratorBlackboxTest {
private final static String DB_PATH = "/tmp/dbc";
	
	DBMap<String, String> backend;
	DBMapKeyIterator<String> toTest;
	
	@Before
	public void before() throws SQLException{
		//die (bestehende) Datenbank vorher entfernen
		new File(DB_PATH + ".h2.db").delete();
		backend = new DBMap<String, String>(new ConnectionInfo(
				"org.h2.Driver", 
				"jdbc:h2:" + DB_PATH, 
				"sa", 
				""));
		
		toTest = new DBMapKeyIterator<String>(backend);
	}
	
	@Test
	public void next() throws SQLException{
		try{
			toTest.next();
			fail("It should be thrown an exception!");
		}catch(NoSuchElementException e){}

		backend.put("Test", null);
		backend.put("Test2", null);
		toTest = new DBMapKeyIterator<String>(backend); 
		
		assertEquals("Test", toTest.next());
		assertEquals("Test2", toTest.next());
	}
	
	@Test
	public void hasNext() throws SQLException{
		assertFalse(toTest.hasNext());

		backend.put("Test", null);
		backend.put("Test2", null);
		toTest = new DBMapKeyIterator<String>(backend); 
		
		assertTrue(toTest.hasNext()); toTest.next();
		assertTrue(toTest.hasNext()); toTest.next();
		assertFalse(toTest.hasNext());
	}
	
	@Test
	public void remove() throws SQLException{
		try{
			toTest.remove();
			fail("It should be thrown an exception.");
		}catch(IllegalStateException e){}

		backend.put("Test", null);
		toTest = new DBMapKeyIterator<String>(backend); 
		
		toTest.next();
		toTest.remove();
		assertFalse(backend.containsKey("Test"));
	}
}
