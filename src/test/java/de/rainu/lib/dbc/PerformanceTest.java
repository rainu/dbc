package de.rainu.lib.dbc;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import de.rainu.lib.dbc.DBMap;
import de.rainu.lib.dbc.beans.ConnectionInfo;

@Ignore("Nur manuell ausfühen")
public class PerformanceTest {
	
	/**
	 * TEST-PROTOKOL:
	 * 
	 * Erste Tests haben ergeben, dass der Speicherverbrauch um ~20% verringert wird.
	 * Jedoch wird die Zeit um Faktor 42(!) erhöht. Dies liegt an der Tatsache, dass synchron
	 * in die Datenbank geschrieben wird. Eine Option wäre es bspw. die Schreib-Operationen
	 * in ein Thread auszulagern. 
	 * 
	 * Test1:
	 * Schreiben: 	3914
	 * Lesen: 		991
	 * Schreiben: 	2455
	 * Lesen: 		848
	 * Usage: 		17894208
	 * 
	 * Test2:
	 * Schreiben: 	53
	 * Lesen: 		22
	 * Schreiben: 	59
	 * Lesen: 		13
	 * Usage: 		21532416
	 */
	
	private static final ConnectionInfo INFO = new ConnectionInfo(
			"org.h2.Driver", 
			"jdbc:h2:" + System.getProperty("java.io.tmpdir") + "/dbc", 
			"sa", 
			"");
	
	private void fillIntoMap(Map<String, String> map){
		long time = System.currentTimeMillis();
		for(int i=0; i < 100000; i++){
			map.put(String.valueOf(i), "Hallo Welt");
		}
		System.out.println("Schreiben: " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		for(int i=0; i < 100000; i++){
			map.get(String.valueOf(i));
		}
		System.out.println("Lesen: " + (System.currentTimeMillis() - time));
	}
	
	@Test
	public void testFillAndGet() throws SQLException{
		DBMap<String, String> DBMap = new DBMap<String, String>(INFO);
		DBMap<String, String> DBMap2 = new DBMap<String, String>(INFO);
		fillIntoMap(DBMap);
		fillIntoMap(DBMap2);
		
		Runtime r = Runtime.getRuntime();

		r.gc();
		System.out.println(r.totalMemory() - r.freeMemory());
	}
	
	@Test
	public void testFillAndGet2(){
		Map<String, String> hashMap = new HashMap<String, String>();
		Map<String, String> hashMap2 = new HashMap<String, String>();
		fillIntoMap(hashMap);
		fillIntoMap(hashMap2);
		
		Runtime r = Runtime.getRuntime();
		
		r.gc();
		System.out.println(r.totalMemory() - r.freeMemory());
	} 
	
	public static void main(String[] args){
		List<String> test = new LinkedList<String>();
		
		test.add(5, "Test");
	}
	
}
