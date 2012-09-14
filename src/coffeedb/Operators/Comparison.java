package coffeedb.operators;

import coffeedb.Function;
import coffeedb.Value;

public class Comparison extends Function {
	public Predicate predicate;
	public Value left;
	public Value right;
	
	public Comparison(Predicate pred, Value leftVal, Value rightVal) {
		super(pred.toString(), leftVal, rightVal);
		predicate = pred;
		left = leftVal;
		right = rightVal;
	}

}
