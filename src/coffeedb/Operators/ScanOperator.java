package coffeedb.operators;

import java.util.Iterator;

import coffeedb.Catalog;
import coffeedb.CoffeeDB;
import coffeedb.Table;
import coffeedb.Tuple;

/***
 * SeqScan operator
 * 
 * @author masonchang
 * 
 */
public class ScanOperator extends Operator {
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

	public void close() {
	}

	public Tuple getNext() {
		if (!_iterator.hasNext())
			return null;
		Tuple tuple = _iterator.next();
		assert (tuple != null);
		return tuple;
	}

	public void reset() {
		assert false : "Not yet implemented";
	}
}
