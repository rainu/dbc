package de.rainu.lib.dbc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.rainu.lib.dbc.DBMap;
import de.rainu.lib.dbc.DBSet;

@RunWith(MockitoJUnitRunner.class)
public class DBSetWhiteboxTest {

	@Mock
	DBMap<String, String> mockBackend;
	DBSet<String> toTest;
	
	@Before
	public void before(){
		toTest = new DBSet<String>(mockBackend);
		toTest = spy(toTest);
	}
	
	@After
	public void clean(){
		reset(toTest);
		reset(mockBackend);
	}
	
	@Test
	public void size(){
		toTest.size();
		
		verify(mockBackend).size();
		verifyNoMoreInteractions(mockBackend);
	}
	
	@Test
	public void isEmpty(){
		toTest.isEmpty();
		
		verify(mockBackend).isEmpty();
		verifyNoMoreInteractions(mockBackend);
	}
	
	@Test
	public void contains(){
		final String test = "Test";
		
		toTest.contains(test);
		
		verify(mockBackend).containsKey(same(test));
		verifyNoMoreInteractions(mockBackend);
	}
		
	@Test
	public void add(){
		final String test = "test";
		
		doReturn(true).when(mockBackend).containsKey(anyObject());
		
		assertFalse(toTest.add(test));
		
		verify(mockBackend).containsKey(same(test));
		verify(mockBackend, never()).put(anyString(), anyString());
		
		reset(mockBackend);
		doReturn(false).when(mockBackend).containsKey(anyObject());
		
		assertTrue(toTest.add(test));
		
		verify(mockBackend).containsKey(same(test));
		verify(mockBackend).put(same(test), eq((String)null));
	}
	
	@Test
	public void remove(){
		final String test = "test";
		
		doReturn(true).when(mockBackend).containsKey(anyObject());
		
		assertTrue(toTest.remove(test));
		
		verify(mockBackend).containsKey(same(test));
		verify(mockBackend).remove(same(test));
		
		reset(mockBackend);
		doReturn(false).when(mockBackend).containsKey(anyObject());
		
		assertFalse(toTest.remove(test));
		
		verify(mockBackend).containsKey(same(test));
		verify(mockBackend, never()).remove(anyString());
	}
	
	@Test
	public void clear(){
		toTest.clear();
		
		verify(mockBackend).clear();
		verifyNoMoreInteractions(mockBackend);
	}
	
	@Test
	public void containsAll(){
		assertTrue(toTest.containsAll(toTest));
		try{
			toTest.containsAll(null);
			fail("It should be thrown an exception!");
		}catch(NullPointerException e){}
		
		doReturn(false).when(toTest).contains(any());
		assertFalse(toTest.containsAll(Collections.singleton("Test")));
		
		doReturn(true).when(toTest).contains(any());
		assertTrue(toTest.containsAll(Collections.singleton("Test")));
	}
	
	@Test
	public void addAll(){
		try{
			toTest.addAll(null);
			fail("It should be thrown an exception.");
		}catch(NullPointerException e){}
		
		doReturn(true).when(toTest).add(anyString());
		assertTrue(toTest.addAll(Collections.singleton("Test")));
		
		doReturn(false).when(toTest).add(anyString());
		assertFalse(toTest.addAll(Collections.singleton("Test")));
	}
	
	@Test
	public void retainAll(){
		assertFalse(toTest.retainAll(toTest));
	}
}
