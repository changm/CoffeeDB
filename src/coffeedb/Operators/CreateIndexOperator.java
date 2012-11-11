package coffeedb.operators;

import java.util.LinkedList;
import java.util.List;

import coffeedb.CoffeeDB;
import coffeedb.Index;
import coffeedb.Table;
import coffeedb.Tuple;
import coffeedb.types.Type;

public class CreateIndexOperator extends Operator {
	private String _indexName; 
	private String _tableName;
	private String _columnName;
	private boolean _didCreate;
	private LinkedList<Tuple> _results;
	
	public CreateIndexOperator(String indexName, String tableName, String columnName) {
		_indexName = indexName;
		_tableName = tableName;
		_columnName = columnName;
		_didCreate = false;
		_results = new LinkedList<Tuple>();
	}
	
	private Tuple getResultTuple() {
		String resultString = "Created index " + _indexName + " on " + _tableName;
		return Tuple.createTupleAndSchema(resultString, "Result");
	}

	@Override
	public List<Tuple> getData() {
		if (!_didCreate) {
			_results.add(createIndex());
		}
		
		return _results;
	}

	private Tuple createIndex() {
		Table table = CoffeeDB.catalog().getTable(_tableName);
		Index index = new Index(_indexName, table, _columnName);
		index.scanTable();
		return getResultTuple();
	}
}
