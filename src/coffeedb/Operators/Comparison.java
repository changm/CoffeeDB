package coffeedb.operators;

import coffeedb.Value;

public class Comparison {
	public Predicate predicate;
	public Value left;
	public Value right;
	
	public Comparison(Predicate pred, Value leftVal, Value rightVal) {
		predicate = pred;
		left = leftVal;
		right = rightVal;
	}

}
