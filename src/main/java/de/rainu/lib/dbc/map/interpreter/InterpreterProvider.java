package de.rainu.lib.dbc.map.interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * Dies ist ein Provider, der die bestehenden Interpreter
 * beinhaltet und zur Verfügung stellt.
 * 
 * @author rainu
 *
 */
public class InterpreterProvider {
	private static InterpreterProvider instance;
	
	public static InterpreterProvider getInstance(){
		if(instance == null){
			instance = new InterpreterProvider();
		}
		
		return instance;
	}
	
	private Map<Class<?>, Interpreter<?>> interpreter = new HashMap<Class<?>, Interpreter<?>>();
	private Interpreter<?> objectInterpreter;
	
	private InterpreterProvider() {
		objectInterpreter = new ObjectInterpreter();
		interpreter.put(Integer.class, new IntegerInterpreter());
		interpreter.put(Long.class, new LongInterpreter());
		interpreter.put(Float.class, new FloatInterpreter());
		interpreter.put(Double.class, new DoubleInterpreter());
		interpreter.put(Byte.class, new ByteInterpreter());
		interpreter.put(Character.class, new CharacterInterpreter());
		interpreter.put(Boolean.class, new BooleanInterpreter());
		interpreter.put(String.class, new StringInterpreter());
	}

	public Interpreter<?> getInterpreter(Class<?> clazz){
		if(interpreter.containsKey(clazz)){
			return interpreter.get(clazz);
		}
		
		return objectInterpreter;
	}
}
