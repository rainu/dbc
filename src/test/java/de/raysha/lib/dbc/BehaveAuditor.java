package de.raysha.lib.dbc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;

public class BehaveAuditor implements InvocationHandler{
	private Object original;
	private Object toVerify;
	private Collection<String> blacklistedMethods;
	
	private BehaveAuditor(Object original, Object toVerify,
			Collection<String> blacklistedMethods){
		this.original = original;
		this.toVerify = toVerify;
		this.blacklistedMethods = blacklistedMethods;
	}
	
	public static <T> T initBehaveAuditor(Class<T> targetClass, T original, T toCHeck,
			String...blacklistedMethods){
		return initBehaveAuditor(targetClass, original, toCHeck, 
				Arrays.asList(blacklistedMethods));
	}
	
	public static <T> T initBehaveAuditor(Class<T> targetClass, T original, T toCHeck,
			Collection<String> blacklistedMethods){
		return (T)Proxy.newProxyInstance(
				BehaveAuditor.class.getClassLoader(), 
				new Class<?>[]{targetClass},
				new BehaveAuditor(original, toCHeck, blacklistedMethods));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		if(blacklistedMethods.contains(method.getName())){
			return invokeVerify(method, args);
		}
		
		Object orgResult = null;
		Throwable orgException = null;
		
		Object verifyResult = null;
		Throwable verifyException = null;
		
		try{
			orgResult = method.invoke(original, args);
		}catch(InvocationTargetException e){
			orgException = e.getTargetException();
		}
		
		try{
			verifyResult = method.invoke(toVerify, args);
		}catch(InvocationTargetException e){
			verifyException = e.getTargetException();
		}
		
		compareThrowable(method, args, orgException, verifyException);
		compareResults(method, args, orgResult, verifyResult);
		
		return verifyResult;
	}

	private void compareResults(Method method, Object[] args, Object orgResult,
			Object verifyResult) {
		
		final String errorMessage = "\nThe original (" + original.getClass().getName() + ") object returns another value " +
				"than the verify object! (" + toVerify.getClass().getName() + ") .\n" +
				method.getName() + "(" + (args != null ? Arrays.toString(args) : "") + ")";
		
		if(orgResult != null && orgResult.getClass().isArray()){
			assertArrayEquals(errorMessage,
					(Object[])orgResult, (Object[])verifyResult);
		}else{
			assertEquals(
					errorMessage,
					orgResult, verifyResult);
		}
	}

	private void compareThrowable(Method method, Object[] args, 
			Throwable orgException, Throwable verifyException) throws Throwable {
		if(orgException == null){
			assertNull(
					"\nThe original throw no exception but the verify does!\n" +
					"This exception was thrown: " + (verifyException != null ? verifyException.getClass().getName() : "" ),
					verifyException);
		}else{
			assertNotNull(
					"\nThe original throws an exception but the verify object doesnt!\n" +
					"Method: " + method.getName() + "(" + (args != null ? Arrays.toString(args) : "") + ") \n" +
					"This exception should be thrown: " + orgException.getClass().getName(),
					verifyException);
			assertEquals(
					"\nThere was thrown difference exceptions!",
					orgException.getClass(),
					verifyException.getClass());
			throw verifyException;
		}
	}
	
	private Object invokeVerify(Method method, Object[] args) throws Throwable{
		try{
			return method.invoke(toVerify, args);
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
	}
}
