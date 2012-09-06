package coffeedb.operators;

import coffeedb.Tuple;
import coffeedb.Value;


public class FilterOperator extends Operator {
	private Comparison _comparison;
	private Operator _operator;
	private int _columnIndex;
	
	public FilterOperator(Comparison compare, Operator child) {
		assert (compare != null);
		assert (child != null);
		
		_comparison = compare;
		_operator = child;
	}

	public void open() {
		_operator.open();
	}

	public void close() {
		_operator.close();
	}
	

	public Tuple getNext() {
		Predicate op = _comparison.predicate;
		
		while (_operator.hasNext()) {
			Tuple tuple = _operator.next();
			Value column = tuple.getValue(_columnIndex);
			if (column.compare(op, _comparison.right)) {
				return tuple;
			}
		}
		
		return null;
	}

	public void reset() {
		assert false : "Not yet implemented";
	}

}
