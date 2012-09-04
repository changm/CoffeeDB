package coffeedb.operators;

import coffeedb.values.*;
import coffeedb.*;
import coffeedb.types.Type;

public class CreateOperator extends Operator {
	private String _tableName;
	private Schema _schema;
	private boolean _didCreateTable;
	
	public CreateOperator(String tableName, Schema schema) {
		_schema = schema;
		_tableName = tableName;
		_didCreateTable = false;
	}
	
	public void open() {
	}

	public void close() {
	}

	public Tuple getNext() {
		if (_didCreateTable) return null;
		
		CoffeeDB db = CoffeeDB.getInstance();
		Catalog catalog = db.getCatalog();
		
		Table table = new Table(_tableName, _schema);
		catalog.addTable(table);
		_didCreateTable = true;
		return createSuccessTuple();
	}

	private Tuple createSuccessTuple() {
		Value[] result = Value.toValueArray(new StringValue("Created table " + _tableName));
		return new Tuple(result);
	}

	public void reset() {
	}
}
