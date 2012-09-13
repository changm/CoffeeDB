package coffeedb.operators;

import coffeedb.Tuple;

public abstract class Aggregator {
	public abstract void addTuple(Tuple tuple);
	public abstract Operator getOperator();
}
