package de.rainu.lib.dbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Map;

import org.junit.Test;

import de.rainu.lib.dbc.DBMap;
import de.rainu.lib.dbc.beans.ConnectionInfo;

public class DBMapExtendedObjectTest {

	private static ConnectionInfo INFO = new ConnectionInfo(
			"org.h2.Driver", 
			"jdbc:h2:/tmp/dbc", 
			"sa", 
			"");
	
	public static class TestClassOne implements Serializable{
		private static final long serialVersionUID = -740125568074967552L;

		String value;
		
		@Override
		public int hashCode() {
			return 0;
		}
	}
	
	public static class TestClassTwo implements Serializable{
		private static final long serialVersionUID = -1265772049610679934L;
		
		String value;
		
		@Override
		public int hashCode() {
			return 0;
		}
	}
	
	public static class TestClassThree extends TestClassTwo implements Serializable{
		private static final long serialVersionUID = -1080548383769771247L;
	}
	
	@Test
	public void testEqualHashes(){
		/**
		 * Es muss sichergestellt sein, dass 2 Key-Objekte unterschiedlicher
		 * Klasse aber mit selbem Hash auch seperat gespeichert werden!
		 */
		Map<Serializable, String> map = new DBMap<Serializable, String>(INFO);
		TestClassOne one = new TestClassOne(); one.value = "TestClassOne";
		TestClassTwo two = new TestClassTwo(); two.value = "TestClassTwo";
		TestClassThree three = new TestClassThree(); three.value = "TestClassThree";
		
		map.put(one, "ONE");
		map.put(two, "TWO");
		map.put(three, "THREE");
		
		assertEquals("ONE", map.get(one));
		assertEquals("TWO", map.get(two));
		assertEquals("THREE", map.get(three));
		
		map.put(one, "ONE-ONE");
		assertEquals("ONE-ONE", map.get(one));
	}
	
	@Test
	public void testMultipleUsage(){
		Map<String, String> instance1 = new DBMap<String, String>(INFO, "MULTIPLE_USE");
		Map<String, String> instance2 = new DBMap<String, String>(INFO, "MULTIPLE_USE");
		
		final String key = "test";
		final String value = "Test123";
		
		assertTrue(instance1.size() == instance2.size());
		instance1.put(key, value);
		
		assertEquals(value, instance2.get(key));
		assertTrue(instance1.size() == instance2.size());
		
		instance2.remove(key);
		assertTrue(instance1.size() == instance2.size());
		assertFalse(instance1.containsKey(key));
		assertFalse(instance2.containsKey(key));
	}
}
