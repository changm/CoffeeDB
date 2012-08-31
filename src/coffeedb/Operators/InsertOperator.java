package coffeedb.operators;

import java.util.Iterator;

import coffeedb.Catalog;
import coffeedb.CoffeeDB;
import coffeedb.Table;
import coffeedb.Tuple;
import coffeedb.types.Type;
import coffeedb.values.IntValue;
import coffeedb.values.Value;

public class InsertOperator implements Operator {
	private String _tableName;
	private Iterable<Tuple> _tuples;
	private Iterator<Tuple> _tupleIter;
	
	public InsertOperator(String tableName, Iterable<Tuple> tuples) {
		_tableName = tableName;
		assert (tuples != null);
		_tuples = tuples;
		_tupleIter = _tuples.iterator();
	}
	
	public void open() {
	}

	public boolean hasNext() {
		return _tupleIter.hasNext();
	}

	public void close() {
	}
	
	private Tuple createResultTuple(int insertCount) {
		Value[] results = Value.toValueArray(new IntValue(insertCount));
		return new Tuple(results);
	}

	public Tuple next() {
		assert (hasNext());
		int insertCount = 0;
		Catalog catalog = CoffeeDB.catalog();
		Table table = catalog.getTable(_tableName);
		
		while (_tupleIter.hasNext()) {
			Tuple tuple = _tupleIter.next();
			table.insertTuple(tuple);
			insertCount++;
		}
		
		return createResultTuple(insertCount);
	}
}
