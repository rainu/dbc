package de.raysha.lib.dbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import de.raysha.lib.dbc.DBMap;
import de.raysha.lib.dbc.beans.ConnectionInfo;

public class DBMapBlackboxTest {
	private final static boolean DELETE_ON_EXIT = true;
	private final static String DB_PATH = "/tmp/dbc";
	private final static ConnectionInfo INFO = new ConnectionInfo(
			"org.h2.Driver", 
			"jdbc:h2:" + DB_PATH, 
			"sa", 
			"");
	
	@Before
	public void before(){
		//die (bestehende) Datenbank vorher entfernen
		new File(DB_PATH + ".h2.db").delete();
	}
	
	private <T extends Serializable> void testSimpleUse(DBMap<String, T> map, T value, T value2){
		final String testKey = "Test";
		
		assertNull(map.get(testKey));
		
		T preValue = map.put(testKey, value);
		
		assertTrue(map.containsKey(testKey));
		assertEquals(value, map.get(testKey));
		assertTrue(map.size() == 1);
		assertTrue(map.containsValue(value));
		assertFalse(map.containsValue(value2));
		assertNull(preValue);
		
		preValue = map.put(testKey, null);
		
		assertNull(map.get(testKey));
		assertTrue(map.size() == 1);
		assertFalse(map.containsValue(value));
		assertFalse(map.containsValue(value2));
		assertTrue(map.containsValue(null));
		assertEquals(value, preValue);
		
		preValue = map.put(testKey, value2);
		
		assertEquals(value2, map.get(testKey));
		assertTrue(map.size() == 1);
		assertFalse(map.containsValue(value));
		assertTrue(map.containsValue(value2));
		assertFalse(map.containsValue(null));
		assertNull(preValue);
		
		boolean iter = false;
		for(String key : map.keySet()){
			iter = true;
		}
		assertTrue(iter);
		
		map.clear();
		assertTrue(map.size() == 0);
		
		iter = false;
		for(String key : map.keySet()){
			iter = true;
		}
		assertFalse(iter);
		
		map.put(testKey, value);
		preValue = map.remove(testKey);
		assertEquals(value, preValue);
		assertFalse(map.containsKey(testKey));
	}
	
	private <T extends Serializable> void testPutAll(DBMap<String, T> map, Map<String, T> toPut){
		DBMap<String, T> iMap = new DBMap<String, T>(INFO, null, DELETE_ON_EXIT);
		
		int expectedCount = map.size() + toPut.size();
		
		map.putAll(toPut);
		iMap.putAll(map);
		
		for(String key : toPut.keySet()){
			assertEquals(toPut.get(key), map.get(key));
			assertEquals(toPut.get(key), iMap.get(key));
		}
		
		assertTrue(map.size() == iMap.size());
		assertTrue(expectedCount == map.size());
	}
	
	private <T extends Serializable> void testIter(DBMap<String, T> map){
		for(String key : map.keySet()){}
		for(T value : map.values()){}
		for(Entry<String, T> entry : map.entrySet()){}
	}
	
	@Test
	public void testIntegerSimpleUse() {
		DBMap<String, Integer> iMap = new DBMap<String, Integer>(INFO, null, DELETE_ON_EXIT);
		testSimpleUse(iMap, 13121989, 13082010);
		testPutAll(iMap, new HashMap<String, Integer>(){{
			put("1", 13121989); put("2", 13041990); put("3", 13082010);
		}});
		
		iMap = new DBMap<String, Integer>(INFO, null, DELETE_ON_EXIT);
		iMap.cacheSize(true);
		testSimpleUse(iMap, 13121989, 13082010);
		testPutAll(iMap, new HashMap<String, Integer>(){{
			put("1", 13121989); put("2", 13041990); put("3", 13082010);
		}});
		testIter(iMap);
	}
	
	@Test
	public void testLongSimpleUse() {
		DBMap<String, Long> lMap = new DBMap<String, Long>(INFO, null, DELETE_ON_EXIT);
		testSimpleUse(lMap, 13121989L, 13082010L);
		testPutAll(lMap, new HashMap<String, Long>(){{
			put("1", 13121989L); put("2", 13041990L); put("3", 13082010L);
		}});
		
		lMap = new DBMap<String, Long>(INFO, null, DELETE_ON_EXIT);
		lMap.cacheSize(true);
		testSimpleUse(lMap, 13121989L, 13082010L);
		testPutAll(lMap, new HashMap<String, Long>(){{
			put("1", 13121989L); put("2", 13041990L); put("3", 13082010L);
		}});
		testIter(lMap);
	}
	
	@Test
	public void testFloatSimpleUse() {
		DBMap<String, Float> fMap = new DBMap<String, Float>(INFO, null, DELETE_ON_EXIT);
		testSimpleUse(fMap, 12.13f, 13.10f);
		testPutAll(fMap, new HashMap<String, Float>(){{
			put("1", 13.121989f); put("2", 13.041990f); put("3", 13.082010f);
		}});
		
		fMap = new DBMap<String, Float>(INFO, null, DELETE_ON_EXIT);
		fMap.cacheSize(true);
		testSimpleUse(fMap, 12.13f, 13.10f);
		testPutAll(fMap, new HashMap<String, Float>(){{
			put("1", 13.121989f); put("2", 13.041990f); put("3", 13.082010f);
		}});
		testIter(fMap);
	}
	
	@Test
	public void testDoubleSimpleUse() {
		DBMap<String, Double> dMap = new DBMap<String, Double>(INFO, null, DELETE_ON_EXIT);
		testSimpleUse(dMap, 12.13d, 13.10d);
		testPutAll(dMap, new HashMap<String, Double>(){{
			put("1", 13.121989d); put("2", 13.041990d); put("3", 13.082010d);
		}});
		
		dMap = new DBMap<String, Double>(INFO, null, DELETE_ON_EXIT);
		dMap.cacheSize(true);
		testSimpleUse(dMap, 12.13d, 13.10d);
		testPutAll(dMap, new HashMap<String, Double>(){{
			put("1", 13.121989d); put("2", 13.041990d); put("3", 13.082010d);
		}});
		testIter(dMap);
	}
	
	@Test
	public void testByteSimpleUse() {
		DBMap<String, Byte> bMap = new DBMap<String, Byte>(INFO, null, DELETE_ON_EXIT);
		testSimpleUse(bMap, (byte)13, (byte)12);
		testPutAll(bMap, new HashMap<String, Byte>(){{
			put("1", (byte)89); put("2", (byte)90); put("3", (byte)10);
		}});
		
		bMap = new DBMap<String, Byte>(INFO, null, DELETE_ON_EXIT);
		bMap.cacheSize(true);
		testSimpleUse(bMap, (byte)13, (byte)12);
		testPutAll(bMap, new HashMap<String, Byte>(){{
			put("1", (byte)89); put("2", (byte)90); put("3", (byte)10);
		}});
		testIter(bMap);
	}
	
	@Test
	public void testCharSimpleUse() {
		DBMap<String, Character> cMap = new DBMap<String, Character>(INFO, null, DELETE_ON_EXIT);
		testSimpleUse(cMap, '\u03FA', '\u03FB');
		testPutAll(cMap, new HashMap<String, Character>(){{
			put("1", '\u04FA'); put("2", '\u05FA'); put("3", '\u06FA');
		}});
		
		cMap = new DBMap<String, Character>(INFO, null, DELETE_ON_EXIT);
		cMap.cacheSize(true);
		testSimpleUse(cMap, '\u03FA', '\u03FB');
		testPutAll(cMap, new HashMap<String, Character>(){{
			put("1", '\u04FA'); put("2", '\u05FA'); put("3", '\u06FA');
		}});
		testIter(cMap);
	}
	
	@Test
	public void testBooleanSimpleUse() {
		DBMap<String, Boolean> bMap = new DBMap<String, Boolean>(INFO, null, DELETE_ON_EXIT);
		testSimpleUse(bMap, true, false);
		testPutAll(bMap, new HashMap<String, Boolean>(){{
			put("1", true); put("2", false); put("3", true);
		}});
		
		bMap = new DBMap<String, Boolean>(INFO, null, DELETE_ON_EXIT);
		bMap.cacheSize(true);
		testSimpleUse(bMap, true, false);
		testPutAll(bMap, new HashMap<String, Boolean>(){{
			put("1", true); put("2", false); put("3", true);
		}});
		testIter(bMap);
	}
	
	@Test
	public void testStringSimpleUse() {
		DBMap<String, String> sMap = new DBMap<String, String>(INFO, null, DELETE_ON_EXIT);
		testSimpleUse(sMap, "13121989", "13108010");
		testPutAll(sMap, new HashMap<String, String>(){{
			put("1", "13121989"); put("2", "13041990"); put("3", "13082010");
		}});
		
		sMap = new DBMap<String, String>(INFO, null, DELETE_ON_EXIT);
		sMap.cacheSize(true);
		testSimpleUse(sMap, "13121989", "13108010");
		testPutAll(sMap, new HashMap<String, String>(){{
			put("1", "13121989"); put("2", "13041990"); put("3", "13082010");
		}});
		testIter(sMap);
	}
	
	@Test
	public void testObjectSimpleUse() {
		DBMap<String, Serializable> oMap = new DBMap<String, Serializable>(INFO, null, DELETE_ON_EXIT, true);
		testSimpleUse(oMap, new BigInteger("1312198900000000000"), new BigInteger("1310801000000000000"));
		testPutAll(oMap, new HashMap<String, Serializable>(){{
			put("1", new BigInteger("13121989")); 
			put("2", new BigInteger("13041990")); 
			put("3", new BigInteger("13082010"));
		}});
		
		oMap = new DBMap<String, Serializable>(INFO, null, DELETE_ON_EXIT, true);
		oMap.cacheSize(true);
		testSimpleUse(oMap, new BigInteger("1312198900000000000"), new BigInteger("1310801000000000000"));
		testPutAll(oMap, new HashMap<String, Serializable>(){{
			put("1", new BigInteger("13121989")); 
			put("2", new BigInteger("13041990")); 
			put("3", new BigInteger("13082010"));
		}});
		testIter(oMap);
	}
}
