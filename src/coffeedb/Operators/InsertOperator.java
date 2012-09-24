package coffeedb.operators;

import java.util.ArrayList;
import java.util.List;


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
	private List<Tuple> _tuples;
	private boolean _didInsert;
	
	public InsertOperator(String tableName, Object...objects) {
		_tableName = tableName;
		_didInsert = false;
		
		_tuples = new ArrayList<Tuple>();
		for (Object o : objects) {
			((ArrayList<Tuple>) _tuples).add((Tuple) o);
		}
	}
	
	public InsertOperator(String tableName, List<Tuple> tuples) {
		_tableName = tableName;
		assert (tuples != null);
		_tuples = tuples;
	}
	
	
	private List<Tuple> createResultTuple(int insertCount) {
		String resultString = "Inserted " + insertCount + " rows";
		Value[] results = Value.createValueArray(resultString); 
		
		ArrayList<Tuple> resultList = new ArrayList<Tuple>();
		resultList.add(new Tuple(getSchema(), results));
		return resultList;
	}

	public List<Tuple> getData() {
		if (_didInsert) return null;
		_didInsert = true;
		
		int insertCount = 0;
		Catalog catalog = CoffeeDB.catalog();
		
		Table table = catalog.getTable(_tableName);
		table.insertTuples(_tuples);
		return createResultTuple(insertCount);
	}
	
	
	protected Schema getSchema() {
		Schema schema = new Schema();
		schema.addColumn("result", Type.getStringType());
		return schema;
	}
}
