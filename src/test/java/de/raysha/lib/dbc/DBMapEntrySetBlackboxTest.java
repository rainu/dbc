package de.raysha.lib.dbc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.raysha.lib.dbc.DBMap;
import de.raysha.lib.dbc.DBMapEntrySet;
import de.raysha.lib.dbc.DBMapEntrySetIterator;
import de.raysha.lib.dbc.beans.ConnectionInfo;

public class DBMapEntrySetBlackboxTest {
	private final static String DB_PATH = "/tmp/dbc";
	
	Map<Integer, String> backend;
	Set<Entry<Integer, String>> toTest;
	
	@Before
	public void before(){
		//die (bestehende) Datenbank vorher entfernen
		new File(DB_PATH + ".h2.db").delete();
		backend = new DBMap<Integer, String>(new ConnectionInfo(
				"org.h2.Driver", 
				"jdbc:h2:" + DB_PATH, 
				"sa", 
				""));
		toTest = new DBMapEntrySet<Integer, String>((DBMap)backend);
	}
	
	class TestEntry implements Entry<Integer, String>{
		private Integer key;
		private String value;
		
		public TestEntry(Integer key, String value){
			this.key = key;
			this.value = value;
		}
		
		@Override
		public Integer getKey() {
			return key;
		}
		@Override
		public String getValue() {
			return value;
		}
		@Override
		public String setValue(String value) {
			String old = value;
			this.value = value;
			
			return old;
		}
		
	}
	
	@Test
	public void add(){
		//we dont need to verify the result! (It does the BehaveAuditor)
		assertTrue(toTest.add(new TestEntry(1, "1")));
		
		backend.containsKey(1);
		backend.containsValue("1");
	}
	
	@Test
	public void remove(){
		assertFalse(toTest.remove(new TestEntry(1, "1")));
		
		backend.put(1, "1");
		assertTrue(toTest.remove(new TestEntry(1, "1")));
	}
	
	@Test
	public void removeAll(){
		try{
			toTest.removeAll(null);
			fail("It should be thrown an exception!");
		}catch(NullPointerException e){}
		
		backend.put(1, "1");
		assertTrue(toTest.removeAll(toTest));
		
		assertTrue(backend.isEmpty());
		
		backend.put(1, "1");
		backend.put(2, "2");
		backend.put(3, "3");
		
		assertTrue(toTest.removeAll(Arrays.asList(new TestEntry[]{
			new TestEntry(1, "1"), new TestEntry(3, "3")	
		})));
		
		assertTrue(backend.size() == 1);
		assertTrue(backend.containsKey(2));
	}
	
	@Test
	public void clear(){
		backend.put(1, "1");
		toTest.clear();
		
		assertTrue(backend.isEmpty());
	}
	
	@Test
	public void contains(){
		assertFalse(toTest.contains(new TestEntry(1, "1")));
		
		backend.put(1, "1");
		assertTrue(toTest.contains(new TestEntry(1, "1")));
	}
	
	@Test
	public void iterator(){
		assertTrue(toTest.iterator() instanceof DBMapEntrySetIterator);
	}
	
	@Test
	public void size(){
		assertTrue(backend.size() == toTest.size());
		
		backend.put(1, "1");
		assertTrue(backend.size() == toTest.size());
	}
}
