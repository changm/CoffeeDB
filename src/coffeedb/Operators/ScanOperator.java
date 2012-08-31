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
	}

	public boolean hasNext() {
		return _iterator.hasNext();
	}

	public void close() {
	}

	public Tuple next() {
		assert (_iterator.hasNext());
		return _iterator.next();
	}
}
