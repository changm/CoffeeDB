package coffeedb;

import java.util.ArrayList;

import coffeedb.operators.Operator;

public class QueryPlan {
	public ArrayList<Operator> _operators;
	
	public QueryPlan() {
		_operators = new ArrayList<Operator>();
	}
	
	public ArrayList<Operator> getOperators() {
		assert (_operators.size() == 1);
		return _operators;
	}
	
	public void addOperator(Operator op) {
		_operators.add(op);
	}
}
