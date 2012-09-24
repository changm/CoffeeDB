package coffeedb.functions;

import java.util.ArrayList;
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
	protected List<Value> _arguments;
	
	public Function(String name, List<Value> arguments) {
		super(Type.getFunctionType());
		_functionName = name;
		_arguments = arguments;
	}
	
	public Function(String name, Value...values) {
		super (Type.getFunctionType());
		_functionName = name;
		_arguments = new ArrayList<Value>();
		for (Value v : values) {
			_arguments.add(v);
		}
	}
	
	public boolean isFunction() { return true; }
	public String getName() { return _functionName; }
	
	public List<Tuple> execute(List<Tuple> data) {
		assert (false);
		return null;
	}
}
