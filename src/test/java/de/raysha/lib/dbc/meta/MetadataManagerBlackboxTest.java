package de.raysha.lib.dbc.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.raysha.lib.dbc.meta.MetadataManager;
import de.raysha.lib.dbc.meta.TableMetadata;

public class MetadataManagerBlackboxTest {

	static Connection connection;
	static MetadataManager toTest;
	
	@BeforeClass
	public static void init() throws Exception{
		Class.forName("org.h2.Driver");
		connection = DriverManager
				.getConnection("jdbc:h2:/tmp/dbc", "sa", "");
		
		toTest = new MetadataManager(connection);
	}
	
	@AfterClass
	public static void clean() throws SQLException{
		connection.close();
		new File("/tmp/dbc.h2.db").delete();
	}
	
	@Test
	public void testNull(){
		assertNull(toTest.getTableMetadata(null));
		assertNull(toTest.getTableMetadata("TABLE_0"));
	}
	
	@Test
	public void testAdd(){
		final String TABLE_NAME = "TABLE_1";
		toTest.insertMetadata(TABLE_NAME, "1.0");
		TableMetadata data = toTest.getTableMetadata(TABLE_NAME);
		
		assertEquals(TABLE_NAME, data.getName());
		assertEquals("1.0", data.getVersion());
		assertTrue(TABLE_NAME.hashCode() == data.getId());
		assertNull(data.getMetadata());
	}
	
	@Test
	public void testMetadata(){
		final String TABLE_NAME = "TABLE_2";
		toTest.insertMetadata(TABLE_NAME, "1.0");
		TableMetadata data = toTest.getTableMetadata(TABLE_NAME);
		toTest.updateMetadata(TABLE_NAME, "TEST_DATA");
		
		assertEquals("TEST_DATA", data.getMetadata());
	}
	
	@Test
	public void testRemove(){
		final String TABLE_NAME = "TABLE_3";
		
		assertNull(toTest.getTableMetadata(TABLE_NAME));
		toTest.insertMetadata(TABLE_NAME, "1.0");
		assertNotNull(toTest.getTableMetadata(TABLE_NAME));
		toTest.removeMetadata(TABLE_NAME);
		assertNull(toTest.getTableMetadata(TABLE_NAME));
	}
}
