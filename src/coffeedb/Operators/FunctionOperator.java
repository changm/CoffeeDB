package coffeedb.operators;

import java.util.List;

import coffeedb.Tuple;
import coffeedb.functions.Function;

public class FunctionOperator extends Operator {
	Function _function;
	
	public FunctionOperator(Function function) {
		_function = function;
	}
	
	public FunctionOperator(Operator child, Function function) {
		_child = child;
		_function = function;
	}

	public List<Tuple> getData() {
		assert (_child != null);
		List<Tuple> data = _child.getData();
		return _function.execute(data);
	}
}
