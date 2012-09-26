package coffeedb.functions;

import java.util.List;

import coffeedb.Tuple;
import coffeedb.Value;

public class Comparison extends Function {
	private Compare _op;

	public Comparison(String name, String[] arguments, Compare op) {
		super(name, arguments);
		_op = op;
	}
	
	public List<Tuple> execute(List<Tuple> data) {
		return null;
	}

}
