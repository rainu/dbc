package de.raysha.lib.dbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.raysha.lib.dbc.DBList;
import de.raysha.lib.dbc.DBMap;
import de.raysha.lib.dbc.beans.ConnectionInfo;

public class DBListBlackboxTest {
	private final static String DB_PATH = "/tmp/dbc";
	
	DBMap<Integer, String> backend;
	List<String> toTest;
	
	@Before
	public void before(){
		//die (bestehende) Datenbank vorher entfernen
		new File(DB_PATH + ".h2.db").delete();
		backend = new DBMap<Integer, String>(new ConnectionInfo(
				"org.h2.Driver", 
				"jdbc:h2:" + DB_PATH, 
				"sa", 
				""));
		
		toTest = new DBList<String>(backend);
		toTest = BehaveAuditor.initBehaveAuditor(
				List.class, 
				new ArrayList<String>(), 
				toTest, 
				"iterator", "toString", "listIterator", "subList");
	}	
	
	@Test
	public void size() {
		assertTrue(toTest.size() == 0);
		
		toTest.add("");
		assertTrue(toTest.size() == 1);
	}

	@Test
	public void isEmpty() {
		assertTrue(toTest.isEmpty());
		
		toTest.add("");
		assertFalse(toTest.isEmpty());
	}

	@Test
	public void contains() {
		assertFalse(toTest.contains(""));
		
		toTest.add("");
		assertTrue(toTest.contains(""));
		assertFalse(toTest.contains(null));
		
		toTest.add(null);
		assertTrue(toTest.contains(null));
	}

	@Test
	public void toArray() {
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
	public void add() {
		assertTrue(toTest.add(""));
		assertTrue(toTest.contains(""));
	}

	@Test
	public void remove() {
		assertFalse(toTest.remove(""));
	
		toTest.add("1");
		toTest.add("1");
		assertTrue(toTest.remove("1"));
		assertTrue(toTest.contains("1"));
	
	}

	@Test
	public void removeIndex() {
		try{
			toTest.remove(0);
			fail("It should be thrown an index-out-of-bounce-exception!");
		}catch(IndexOutOfBoundsException e){}
		
		final String toAdd = "Test";
		toTest.add(toAdd);
		assertEquals(toAdd, toTest.remove(0));
		assertTrue(toTest.isEmpty());
	}
	
	@Test
	public void containsAll() {
		assertTrue(toTest.containsAll(Collections.EMPTY_LIST));
		try{
			toTest.containsAll(null);
			fail("It should be thrown an exception!");
		}catch(NullPointerException e){}
		
		toTest.add("1");
		toTest.add("2");
		toTest.add("3");
		
		assertTrue(toTest.containsAll(Arrays.asList("1", "2")));
		assertFalse(toTest.containsAll(Arrays.asList("1", "5")));
	}

	@Test
	public void addAll() {
		try{
			toTest.addAll(null);
			fail("It should thrown an exception!");
		}catch(NullPointerException e){}
		assertFalse(toTest.addAll(Collections.EMPTY_LIST));
		
		assertTrue(toTest.addAll(Arrays.asList("1", "2")));
		assertTrue(toTest.containsAll(Arrays.asList("1", "2")));
		
		assertEquals("1", toTest.get(0));
		assertEquals("2", toTest.get(1));
	}

	@Test
	public void addAllIndex() {
		try{
			toTest.addAll(1, Arrays.asList("1"));
			fail("It should be thrown an index-out-of-bounce-exception!");
		}catch(IndexOutOfBoundsException e){}
		
		
		toTest.add("1");
		toTest.add("2");
		toTest.add("3");
		
		assertTrue(toTest.addAll(1, Arrays.asList("1.1", "1.2")));
		assertEquals("1.1", toTest.get(1));
		assertEquals("1.2", toTest.get(2));
		assertEquals("2", toTest.get(3));
		assertEquals("3", toTest.get(4));
	}

	@Test
	public void removeAll() {
		assertFalse(toTest.removeAll(null));
		assertFalse(toTest.removeAll(Collections.EMPTY_LIST));
		
		toTest.add("1");
		toTest.add("2");
		toTest.add("3");
		
		assertTrue(toTest.removeAll(Arrays.asList("2", "3")));
		assertFalse(toTest.contains("2"));
		assertFalse(toTest.contains("3"));
	}

	@Test
	public void retainAll() {
		assertFalse(toTest.retainAll(null));
		assertFalse(toTest.retainAll(Collections.EMPTY_LIST));
		
		toTest.add("");
		assertTrue(toTest.retainAll(Collections.EMPTY_LIST));
		assertTrue(toTest.size() == 0);
		
		toTest.add("1");
		toTest.add("2");
		toTest.add("3");
		
		assertTrue(toTest.retainAll(Arrays.asList("2")));
		assertEquals("2", toTest.get(0));
	}

	@Test
	public void clear() {
		toTest.add("1");
		assertFalse(toTest.isEmpty());
		
		toTest.clear();
		assertTrue(toTest.isEmpty());
	}

	@Test
	public void get() {
		try{
			toTest.get(1);
			fail("It should be thrown an exception!");
		}catch(IndexOutOfBoundsException e){}
		
		toTest.add("1");
		assertEquals("1", toTest.get(0));
	}

	@Test
	public void set() {
		try{
			toTest.set(1, "test");
			fail("It should be thrown an exception!");
		}catch(IndexOutOfBoundsException e){}
		
		toTest.add("1");
		assertEquals("1", toTest.get(0));
		
		toTest.set(0, "2");
		assertEquals("2", toTest.get(0));
	}

	@Test
	public void add2() {
		try{
			toTest.add(1, "test");
			fail("It should be thrown an exception!");
		}catch(IndexOutOfBoundsException e){}
		
		toTest.add("1");
		toTest.add("3");
		
		toTest.add(1, "2");
		assertEquals("2", toTest.get(1));
	}

	@Test
	public void indexOf() {
		assertTrue(-1 == toTest.indexOf(null));
		assertTrue(-1 == toTest.indexOf(""));
		
		toTest.add("1");
		assertTrue(0 == toTest.indexOf("1"));
		
		toTest.add("1");
		assertTrue(0 == toTest.indexOf("1"));
	}

	@Test
	public void lastIndexOf() {
		assertTrue(-1 == toTest.lastIndexOf(null));
		assertTrue(-1 == toTest.lastIndexOf(""));
		
		toTest.add("1");
		assertTrue(0 == toTest.lastIndexOf("1"));
		
		toTest.add("1");
		assertTrue(1 == toTest.lastIndexOf("1"));
	}

	@Test
	public void removeRange(){
		//muss hier gemacht werden, da nur
		//auf einer ebene reflektiert wird
		//somit wäre subList.clear() nicht betroffen
		toTest = new DBList<String>(backend);
		toTest.add("1");
		toTest.add("2");
		toTest.add("3");
		toTest.add("4");
		toTest.add("5");
		
		toTest.subList(1, 3).clear();
		assertTrue(toTest.size() == 2);
		assertEquals("1", toTest.get(0));
		assertEquals("5", toTest.get(1));
	}
	
	@Test
	public void listIterator() {
		try{
			toTest.listIterator(1);
			fail("It should be thrown an exception!");
		}catch(IndexOutOfBoundsException e){}
		
		toTest.add("");
		toTest.listIterator(0);
	}
	
	@Test
	public void subList() {
		try{
			toTest.subList(0, 1);
			fail("It should be thrown an exception!");
		}catch(IndexOutOfBoundsException e){}
		
		toTest.add("");
		toTest.add("");
		toTest.add("");
		assertTrue(toTest.subList(0, 0).isEmpty());
		
		try{
			toTest.subList(-1, 0);
			fail("It should be thrown an exception!");
		}catch(IndexOutOfBoundsException e){}
		
		try{
			toTest.subList(2, 1);
			fail("It should be thrown an exception!");
		}catch(IllegalArgumentException e){}
	}
}
