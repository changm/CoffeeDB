package coffeedb.operators;

import coffeedb.Tuple;

public interface Operator {
	public void open();
	public boolean hasNext();
	public void close();
	public Tuple next();
}
