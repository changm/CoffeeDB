package coffeedb.operators;

import java.util.List;

import coffeedb.Catalog;
import coffeedb.CoffeeDB;
import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.types.Type;

public class DropOperator extends Operator {
	private String _tableName;
	
	public DropOperator(String tableName) {
		_tableName = tableName;
	}
	
	private List<Tuple> getResult() {
		String message = "Dropped table " + _tableName;
		Schema resultSchema = new Schema();
		resultSchema.addColumn("Drop", Type.getStringType());
		
		Tuple resultTuple =  new Tuple(resultSchema, Value.createValueArray(message));
		return Tuple.createList(resultTuple);
	}

	@Override
	public List<Tuple> getData() {
		Catalog catalog = CoffeeDB.getInstance().getCatalog();
		assert (catalog.tableExists(_tableName));
		catalog.deleteTable(_tableName);
		return getResult();
	}

}
