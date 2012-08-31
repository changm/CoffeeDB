package coffeedb.operators;

import java.util.Iterator;

import coffeedb.Catalog;
import coffeedb.CoffeeDB;
import coffeedb.Table;
import coffeedb.Tuple;

/***
 * SeqScan operator
 * @author masonchang
 *
 */
public class ScanOperator implements Operator {
	private String _tableName;
	private Iterator<Tuple> _iterator;
	
	public ScanOperator(String tableName) {
		_tableName = tableName;
	}

	public void open() {
		Catalog catalog = CoffeeDB.catalog();
		Table table = catalog.getTable(_tableName);
		_iterator = table.getIterator();
		assert (_iterator != null);
	}

	public boolean hasNext() {
		assert (_iterator != null);
		return _iterator.hasNext();
	}

	public void close() {
	}

	public Tuple next() {
		assert (_iterator.hasNext());
		Tuple tuple = _iterator.next();
		assert (tuple != null);
		return tuple;
	}

	public void reset() {
		assert false : "Not yet implemented";
	}
}
