package de.rainu.lib.dbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import de.rainu.lib.dbc.DBMap;
import de.rainu.lib.dbc.DBMapValueCollection;
import de.rainu.lib.dbc.DBMapValueIterator;
import de.rainu.lib.dbc.beans.ConnectionInfo;

public class DBMapValueCollectionBlackboxTest {
	private final static String DB_PATH = "/tmp/dbc";
	
	DBMap<Integer, String> backend;
	Collection<String> toTest;
	
	@Before
	public void before() throws SQLException{
		//die (bestehende) Datenbank vorher entfernen
		new File(DB_PATH + ".h2.db").delete();
		backend = new DBMap<Integer, String>(new ConnectionInfo(
				"org.h2.Driver", 
				"jdbc:h2:" + DB_PATH, 
				"sa", 
				""));
		
		toTest = new DBMapValueCollection<String>(backend);
	}
	
	@Test
	public void add(){
		try{
			toTest.add("");
			fail("It should be thrown an exception!");
		}catch(UnsupportedOperationException e){}
	}
	
	@Test
	public void size() {
		assertTrue(toTest.size() == backend.size());
		
		backend.put(1, "1");
		assertTrue(toTest.size() == backend.size());
	}
	
	@Test
	public void contains(){
		assertFalse(toTest.contains(""));
		
		backend.put(1, "");
		assertTrue(toTest.contains(""));
		assertFalse(toTest.contains(null));
		
		backend.put(2, null);
		assertTrue(toTest.contains(null));
	}
	
	@Test
	public void clear(){
		backend.put(1, "1");
		assertFalse(toTest.isEmpty());
		
		toTest.clear();
		assertTrue(toTest.isEmpty());
	}
	
	@Test
	public void retainAll(){
		assertFalse(toTest.retainAll(Collections.EMPTY_LIST));
		
		backend.put(1, "");
		assertTrue(toTest.retainAll(Collections.EMPTY_LIST));
		assertTrue(toTest.size() == 0);
		
		backend.put(1, "1");
		backend.put(2, "2");
		backend.put(3, "3");
		
		assertTrue(toTest.retainAll(Arrays.asList("2")));
		assertEquals("2", backend.get(2));
	}

	@Test
	public void remove() {
		assertFalse(toTest.remove(""));
	
		backend.put(1, "1");
		backend.put(2, "1");
		assertTrue(toTest.remove("1"));
		assertFalse(toTest.contains("1"));
	}
	
	@Test
	public void removeAll() {
		try{
			assertFalse(toTest.removeAll(null));
			fail("It should be thrown an exception!");
		}catch(NullPointerException e){}
		
		assertFalse(toTest.removeAll(Collections.EMPTY_LIST));
		
		backend.put(1, "1");
		backend.put(2, "2");
		backend.put(3, "3");
		
		assertTrue(toTest.removeAll(Arrays.asList("2", "3")));
		assertFalse(toTest.contains("2"));
		assertFalse(toTest.contains("3"));
	}
	
	@Test
	public void iterator(){
		assertTrue(toTest.iterator() instanceof DBMapValueIterator);
	}
}
