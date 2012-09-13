package coffeedb.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import coffeedb.Schema;
import coffeedb.Tuple;

public class TupleOperator extends Operator {
	private Iterator<Tuple> _iterator;
	private List<Tuple> _data;
	
	public TupleOperator(Object...tuples) {
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		result.ensureCapacity(tuples.length);
		
		for (Object t : tuples) {
			Tuple tuple = (Tuple) t;
			result.add(tuple);
		}
		
		_data = result;
	}
	
	public TupleOperator(List<Tuple> tuples) {
		_data = tuples;
	}

	public void open() {
		reset();
	}

	public void reset() {
		_iterator = _data.iterator();
	}

	public void close() {
	}

	protected Tuple getNext() {
		if (_iterator.hasNext()) {
			return _iterator.next();
		}
		
		return null;
	}

	protected Schema getSchema() {
		assert (_data.size() > 0);
		return _data.get(0).getSchema();
	}
}
