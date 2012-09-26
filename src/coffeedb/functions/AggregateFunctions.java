package coffeedb.functions;

import java.util.List;

import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.types.Type;

/***
 * Aggregate functions here
 * RESEARCH IDEA: 
 * Test the idea of instead of having aggregates, we have functions
 * that take in data sets and work purely on the data sets
 * In a way, it's limiting operators to pure relational algebra and
 * Passing pointers to data and doing operations there. Much like how
 * applications pull data from the database and do logic there.
 * 
 * Why?
 * 
 * Before, this was impossible with on-disk databases. You didn't have enough
 * memory to hold all the data in memory so you aggregated things
 * while you read from disk. Now we can separate the two. Pull data from memory
 * operate on data in memory. Might still be really bad though because we technically
 * have two copies of the data - the data in storage and the result set.
 * Oh well, let's try it! Makes a cleaner architecture.
 * @author masonchang
 *
 */
public class AggregateFunctions {
	private static Schema createIntSchema(String aggregateName) {
		Schema schema = new Schema();
		schema.addColumn(aggregateName, Type.getIntType());
		return schema;
	}
	
	private static Schema createDoubleSchema(String aggregateName) {
		Schema schema = new Schema();
		schema.addColumn(aggregateName, Type.getDoubleType());
		return schema;
	}
	
	public static List<Tuple> sum(List<Tuple> data, String[] arguments) {
		assert (arguments.length == 1);
		String column = arguments[0];
		//String groupBy = arguments[1];
		
		int sum = 0;
		for (Tuple t : data) {
			assert (t.getSchema().hasColumn(column));
			int value = t.getValue(column).toInt();
			sum += value;
		}
		
		Schema schema = createIntSchema("sum");
		Tuple result = new Tuple(schema, sum);
		return Tuple.createList(result);
	}

	public static List<Tuple> min(List<Tuple> data, String[] arguments) {
		assert (arguments.length == 1);
		String column = arguments[0];
		
		int min = Integer.MAX_VALUE;
		for (Tuple t : data) {
			int value = t.getValue(column).toInt();
			if (value < min) {
				min = value;
			}
		}
		
		Schema schema = createIntSchema("min");
		Tuple result = new Tuple(schema, min);
		return Tuple.createList(result);
	}

	public static List<Tuple> max(List<Tuple> data, String[] arguments) {
		assert (arguments.length == 1);
		String column = arguments[0];
		
		int max = Integer.MIN_VALUE;
		for (Tuple t : data) {
			int value = t.getValue(column).toInt();
			if (value > max) {
				max = value;
			}
		}
		
		Schema schema = createIntSchema("max");
		Tuple result = new Tuple(schema, max);
		return Tuple.createList(result);
	}

	public static List<Tuple> count(List<Tuple> data, String[] arguments) {
		Value[] values = Value.createValueArray(data.size());
		Schema schema = new Schema();
		schema.addColumn("count", Type.getIntType());
		return Tuple.createList(new Tuple(schema, values));
	}

	public static List<Tuple> avg(List<Tuple> data, String[] arguments) {
		assert (arguments.length == 1);
		String column = arguments[0];
		
		double sum = 0;
		for (Tuple t : data) {
			int value = t.getValue(column).toInt();
			sum += value;
		}
		
		double average = sum / data.size();
		
		Schema schema = createDoubleSchema("average");
		Tuple result = new Tuple(schema, average);
		return Tuple.createList(result);
	}
}
