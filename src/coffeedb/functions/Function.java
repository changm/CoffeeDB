package coffeedb.functions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.types.Type;

/***
 * Represents either a builtin function or a stored procedure
 * 
 * Why a Function: Because functions take as input Values and output Values
 * If a Function is also a Value, that implies a function can take input functions
 * and output another function. This enables SQL to query other data sets
 * and thus create views (I hope!)
 * 
 * The fundamental question is whether or not a Function is a Value
 * Which depends on the language. Is function a first class primitive?
 * And I think in SQL, it should be. Unsure if this is true though. YAY for research!
 * 
 * Another fundamental change. Tuple operators don't really exist anymore
 * There is no good reason to sequentailly call tuple operators to get the next tuple
 * Instead, we should take the big data approach. We'll use relational algebra tuple operators
 * 
 * Then, functions, take in huge data sets and operate purely on the data.
 * Hopefully this means we can super parallelize everything!
 * 
 * @author masonchang
 *
 */
public class Function extends Value {
	protected String _functionName;
	protected String[] _arguments;
	
	// Didn't know Java has this problem, but 
	// Builtin classes has to be defined prior to builtin functions
	// Static initializers are in the order they are declared
	private static Class[] _builtinClasses = {
			AggregateFunctions.class, Comparison.class, FilterFunctions.class
	};
	private static HashMap<String, Method> _builtinFunctions = initFunctions();
	
	private static HashMap<String, Method> initFunctions() {
		HashMap<String, Method> functions = new HashMap<String, Method>();
		assert _builtinClasses != null : "Builtin classes defined AFTER functions. Incorrect";
		
		for (Class builtinClass : _builtinClasses) {
			addMethods(functions, builtinClass);
		}
		
		return functions;
	}
	
	private static void addMethods(HashMap<String, Method> functions,
			Class builtinClass) {
		for (Method builtinMethod : builtinClass.getDeclaredMethods()) {
			String methodName = builtinMethod.getName();
			//System.out.println("Adding method: " + methodName);
			assert (!functions.containsKey(methodName));
			functions.put(methodName, builtinMethod);
		}
	}

	public Function(String name, String[] arguments) {
		super(Type.getFunctionType());
		_functionName = name;
		_arguments = arguments;
	}

	public List<Tuple> execute(List<Tuple> data) {
		assert _builtinFunctions.containsKey(_functionName) : _functionName + " is not a builtin function";
		Method m = _builtinFunctions.get(_functionName);
		try {
			assert (m != null);
			Object instance = null;
			return (List<Tuple>) m.invoke(instance, data, _arguments);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			System.err.println("Illegal argument exception: " + e.toString());
			assert (false);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			System.err.println("Illegal access exception " + e.toString());
			e.printStackTrace();
			assert (false);
		} catch (InvocationTargetException e) {
			System.err.println("Invocatoin Target Exception " + e.toString());
			e.printStackTrace();
			assert (false);
		}
		assert (false);
		return null;
	}
	
	public boolean isFunction() { return true; }
	public String getName() { return _functionName; }
	
}
