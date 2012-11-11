package coffeedb.operators;

import java.util.ArrayList;
import java.util.List;

import coffeedb.*;
import coffeedb.types.Type;

public class CreateTableOperator extends Operator {
	private String _tableName;
	private Schema _schema;
	private boolean _didCreateTable;
	
	public CreateTableOperator(String tableName, Schema schema) {
		_schema = schema;
		_tableName = tableName;
	}
	
	public void createTable() {
		CoffeeDB db = CoffeeDB.getInstance();
		Catalog catalog = db.getCatalog();
		
		Table table = new Table(_tableName, _schema);
		catalog.addTable(table);
	}

	private Tuple createSuccessTuple() {
		Value[] result = Value.createValueArray("Created table " + _tableName);
		return new Tuple(getSchema(), result);
	}
	
	public List<Tuple> getData() {
		if (!_didCreateTable) {
			createTable();
		}
		
		ArrayList<Tuple> results = new ArrayList<Tuple>();
		results.add(createSuccessTuple());
		return results;
	}

	protected Schema getSchema() {
		Schema stringSchema = new Schema();
		stringSchema.addColumn("Result", Type.getStringType());
		return stringSchema;
	}
}
