package coffeedb.operators;

import java.util.Iterator;

import coffeedb.Catalog;
import coffeedb.CoffeeDB;
import coffeedb.Schema;
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
	private Table _table;

	public ScanOperator(String tableName) {
		_tableName = tableName;
		_table = CoffeeDB.catalog().getTable(_tableName);
	}

	public void open() {
		_iterator = _table.getIterator();
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

	protected Schema getSchema() {
		return _table.getSchema();
	}
	
}
