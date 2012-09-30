package coffeedb.operators;

import java.util.List;

import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.functions.Function;
import coffeedb.types.Type;

public class FilterOperator extends Operator {
	private Function _filter;
	private Predicate _predicate; 
	
	public FilterOperator(Operator child, Function filter, Predicate p) {
		_child = child;
		_filter = filter;
		_predicate = p;
	}
	
	public List<Tuple> getData() {
		return _filter.execute(_child.getData());
	}
}
