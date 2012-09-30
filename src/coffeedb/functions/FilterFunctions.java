package coffeedb.functions;

import java.util.ArrayList;
import java.util.List;

import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.operators.Predicate;
import coffeedb.types.Type;

public class FilterFunctions {
	private static Predicate toPredicate(String predicate) {
		for (Predicate p : Predicate.values()) {
			if (p.toString().equalsIgnoreCase(predicate)) {
				return p;
			}
		}
		
		assert (false);
		return null;
	}
	
	public static List<Tuple > where(List<Tuple> data, String[] arguments) {
		assert (arguments.length == 3);
		String column = arguments[0];
		String value = arguments[1];
		Predicate predicate = toPredicate(arguments[2]);
		
		assert (data != null);
		ArrayList<Tuple> results = new ArrayList<Tuple>();
		
		for (Tuple t : data) {
			Value tupleValue = t.getValue(column);
			Type type = tupleValue.getType();
			Value comparison = new Value(type, value);
			if (tupleValue.compare(predicate, comparison)) {
				results.add(t);
			}
		}
		
		return results;
	}

}
