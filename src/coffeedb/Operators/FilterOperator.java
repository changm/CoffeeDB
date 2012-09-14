package coffeedb.operators;

import coffeedb.Function;
import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;


public class FilterOperator extends Operator {
	private Function _comparison;
	private Operator _operator;
	private int _columnIndex;
	
	public FilterOperator(Function compare, Operator child) {
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
		Comparison compare = (Comparison) _comparison;
		Predicate op = compare.predicate;
		
		while (_operator.hasNext()) {
			Tuple tuple = _operator.next();
			Value column = tuple.getValue(_columnIndex);
			if (column.compare(op, compare.right)) {
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
