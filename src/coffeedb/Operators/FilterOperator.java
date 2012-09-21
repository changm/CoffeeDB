package coffeedb.operators;

import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.functions.Function;


public class FilterOperator extends Operator {
	private Function _filter;
	private Operator _operator;
	private int _columnIndex;
	
	public FilterOperator(Function filter, Operator child) {
		assert (filter!= null);
		assert (child != null);
		_filter = filter;
		_operator = child;
	}

	public void open() {
		_operator.open();
	}

	public void close() {
		_operator.close();
	}

	public Tuple getNext() {
		while (_operator.hasNext()) {
			Tuple tuple = _operator.next();
			Value column = tuple.getValue(_columnIndex);
			if (_filter.filter(tuple)) {
				return tuple;
			}
		}
		
		return null;
	}

	public void reset() {
		assert false : "Not yet implemented";
	}

	protected Schema getSchema() {
		return _operator.getSchema();
	}

}
