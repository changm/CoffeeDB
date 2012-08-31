package coffeedb.test;

import coffeedb.*;
import coffeedb.operators.ScanOperator;
import coffeedb.types.*;
import coffeedb.values.IntValue;
import coffeedb.values.Value;

public class TestUtil {
	public static Schema getSimpleSchema() {
		String[] names = new String[1];
		names[0] = "test";
		
		Type[] types = new Type[1];
		types[0] = new IntType();
		
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
		Value intValue = new IntValue(10);
		Value[] values = Value.toValueArray(intValue);
		return new Tuple(values);
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
