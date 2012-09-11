package coffeedb.test;

import coffeedb.*;
import coffeedb.operators.ScanOperator;
import coffeedb.types.*;

public class TestUtil {
	public static Schema getSimpleSchema() {
		String[] names = new String[1];
		names[0] = "test";
		
		Type[] types = new Type[1];
		types[0] = Type.getIntType();
		
		return new Schema(names, types);
	}
	
	public static Table getSimpleTable(String tableName) {
		return new Table(tableName, getSimpleSchema());
	}
	
	public static Table createSimpleTable(String tableName) {
		Catalog catalog = CoffeeDB.catalog();
		Table table = getSimpleTable(tableName);
		catalog.addTable(table);
		return table;
	}

	public static Tuple createSimpleTuple() {
		Value[] values = Value.createValueArray(10);
		Schema schema = new Schema();
		schema.addColumn("intVal", Type.getIntType());
		return new Tuple(schema, values);
	}
	
	public static boolean tupleExists(String tableName, Tuple tuple) {
		ScanOperator scan = new ScanOperator(tableName);
		scan.open();
		while (scan.hasNext()) {
			Tuple t = scan.next();
			if (t.equals(tuple)) return true;
		}
		scan.close();
		return false;
	}
}
