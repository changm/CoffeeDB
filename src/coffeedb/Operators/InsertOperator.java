package coffeedb.operators;

import java.util.ArrayList;


import java.util.Iterator;

import coffeedb.Catalog;
import coffeedb.CoffeeDB;
import coffeedb.Schema;
import coffeedb.Table;
import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.types.Type;

public class InsertOperator extends Operator {
	private String _tableName;
	private Iterable<Tuple> _tuples;
	private Iterator<Tuple> _tupleIter;
	private boolean _didInsert;
	
	public InsertOperator(String tableName, Object...objects) {
		_tableName = tableName;
		_didInsert = false;
		
		_tuples = new ArrayList<Tuple>();
		for (Object o : objects) {
			((ArrayList<Tuple>) _tuples).add((Tuple) o);
		}
		
	}
	public InsertOperator(String tableName, Iterable<Tuple> tuples) {
		_tableName = tableName;
		assert (tuples != null);
		_tuples = tuples;
	}
	
	public void open() {
		_tupleIter = _tuples.iterator();
	}

	public void close() {
	}
	
	private Tuple createResultTuple(int insertCount) {
		String resultString = "Inserted " + insertCount + " rows";
		Value[] results = Value.createValueArray(resultString); 
		return new Tuple(getSchema(), results);
	}

	public Tuple getNext() {
		if (_didInsert) return null;
		
		int insertCount = 0;
		Catalog catalog = CoffeeDB.catalog();
		Table table = catalog.getTable(_tableName);
		
		while (_tupleIter.hasNext()) {
			Tuple tuple = _tupleIter.next();
			table.insertTuple(tuple);
			insertCount++;
		}
		
		_didInsert = true;
		return createResultTuple(insertCount);
	}
	
	public void reset() {
		
	}
	
	protected Schema getSchema() {
		Schema schema = new Schema();
		schema.addColumn("result", Type.getStringType());
		return schema;
	}
}
