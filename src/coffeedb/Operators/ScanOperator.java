package coffeedb.operators;

import java.util.Iterator;
import java.util.List;

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
	private Table _table;

	// Assumes all columns
	public ScanOperator(String tableName) {
		_tableName = tableName;
		_table = CoffeeDB.catalog().getTable(_tableName);
	}

	public List<Tuple> getData() {
		return _table.getData();
	}
}
