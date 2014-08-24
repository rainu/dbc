package de.raysha.lib.dbc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.raysha.lib.dbc.DBMap;
import de.raysha.lib.dbc.DBMapKeyIterator;
import de.raysha.lib.dbc.DBSet;
import de.raysha.lib.dbc.beans.ConnectionInfo;

public class DBSetBlackboxTest {
	private final static String DB_PATH = "/tmp/dbc";
	
	DBMap<String, String> backend;
	Set<String> toTest;
	
	@Before
	public void before(){
		//die (bestehende) Datenbank vorher entfernen
		new File(DB_PATH + ".h2.db").delete();
		backend = new DBMap<String, String>(new ConnectionInfo(
				"org.h2.Driver", 
				"jdbc:h2:" + DB_PATH, 
				"sa", 
				""));
		
		toTest = new DBSet<String>(backend);
		
		toTest = BehaveAuditor.initBehaveAuditor(
				Set.class,
				new HashSet<String>(), 
				toTest,
				"iterator", "toString");
	}
	
	@After
	public void clean(){
	}
	
	@Test
	public void size(){
		assertTrue(toTest.size() == 0);
		
		toTest.add("Test");
		assertTrue(toTest.size() == 1);
	}
	
	@Test
	public void isEmpty(){
		assertTrue(toTest.isEmpty());
		
		toTest.add("Test");
		assertFalse(toTest.isEmpty());
	}
	
	@Test
	public void contains(){
		final String test = "Test";
		
		assertFalse(toTest.contains(test));
		
		toTest.add("Test");
		assertTrue(toTest.contains(test));
	}
	
	@Test
	public void iterator(){
		assertTrue(toTest.iterator() instanceof DBMapKeyIterator);
	}
	
	@Test
	public void toArray(){
		assertTrue(Arrays.equals(toTest.toArray(), new Serializable[]{}));
		
		toTest.add("Test1");
		toTest.add("Test2");
		
		assertTrue(Arrays.equals(toTest.toArray(), 
				new String[]{"Test1", "Test2"}));
		
		String[] sArray = new String[2];
		String[] result = toTest.toArray(sArray);
		
		assertSame(sArray, result);
		assertTrue(Arrays.equals(sArray, 
				new String[]{"Test1", "Test2"}));
		
		sArray = new String[3];
		result = toTest.toArray(sArray);
		
		assertSame(sArray, result);
		assertTrue(Arrays.equals(sArray, 
				new String[]{"Test1", "Test2", null}));
		
		result = toTest.toArray(new String[]{});
		
		assertTrue(Arrays.equals(result, 
				new String[]{"Test1", "Test2"}));
	}
	
	@Test
	public void add(){
		assertTrue(toTest.add("Test1"));
		assertFalse(toTest.add("Test1"));
	}
	
	@Test
	public void remove(){
		assertFalse(toTest.remove("Test1"));
		
		toTest.add("Test1");
		assertTrue(toTest.remove("Test1"));
	}
	
	@Test
	public void clear(){
		toTest.add("Test1");
		toTest.add("Test2");
		toTest.clear();

		assertTrue(toTest.isEmpty());
	}
	
	@Test
	public void containsAll(){
		toTest.add("Test1");
		toTest.add("Test2");
		
		assertTrue(toTest.containsAll(Arrays.asList("Test1")));
		assertTrue(toTest.containsAll(Arrays.asList("Test1", "Test2")));
		assertFalse(toTest.containsAll(Arrays.asList("123")));
		
		assertTrue(toTest.containsAll(toTest));
		try{
			toTest.containsAll(null);
			fail("It should thrown an exception!");
		}catch(NullPointerException e){}
	}
	
	@Test
	public void addAll(){
		assertTrue(toTest.addAll(Arrays.asList("Test1", "Test2")));
		assertTrue(toTest.size() == 2);
		
		try{
			toTest.addAll(null);
			fail("It should be thrown an exception.");
		}catch(NullPointerException e){}
		assertFalse(toTest.addAll(toTest));
	}
	
	@Test
	public void retainAll(){
		toTest.add("Test1");
		toTest.add("Test2");
		toTest.add("Test3");
		
		assertFalse(toTest.retainAll(toTest));
		try{
			toTest.retainAll(null);
			fail("It should be thrown an exception.");
		}catch(NullPointerException e){}
		
		assertTrue(toTest.retainAll(Arrays.asList("Test2")));
		assertTrue(toTest.size() == 1);
	}
	
	@Test
	public void removeAll(){
		assertFalse(toTest.removeAll(toTest));
		try{
			toTest.removeAll(null);
			fail("It should be thrown an exception!");
		}catch(NullPointerException e){}
		
		toTest.add("Test1");
		toTest.add("Test2");
		toTest.add("Test3");
		
		assertTrue(toTest.removeAll(Arrays.asList("Test1")));
		assertTrue(toTest.size() == 2);
		
		assertTrue(toTest.removeAll(toTest));
	}
}
