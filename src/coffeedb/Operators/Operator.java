package coffeedb.operators;

import coffeedb.Schema;
import coffeedb.Tuple;

public abstract class Operator {
	protected Tuple _next = null;
	
	public Operator() {
	}
	
	public boolean hasNext() {
		if (_next == null) {
			_next = getNext();
		}
		
		return _next != null;
	}
	
	public Tuple next() {
		Tuple tuple = _next;
		assert (tuple != null);
		_next = null;
		return tuple;
	}
		
	public abstract void open();
	public abstract void reset();
	public abstract void close();
	protected abstract Tuple getNext();
	protected abstract Schema getSchema();
}
