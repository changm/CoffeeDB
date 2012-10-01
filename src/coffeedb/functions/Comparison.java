package coffeedb.functions;

import java.util.List;

import coffeedb.Tuple;
import coffeedb.Value;

public class Comparison extends Function {
	private Compare _op;

	public Comparison(String name) {
		super(name);
	}
	
	public List<Tuple> execute(List<Tuple> data) {
		assert (false);
		return null;
	}

}
