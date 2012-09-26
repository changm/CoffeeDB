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
public class AggregateFunctions extends Function {
	public static Aggregate _op;
	
	public AggregateFunctions(String name, String[] arguments, Aggregate op) {
		super(name, arguments);
		_op = op;
	}
	
	public static List<Tuple> sum(List<Tuple> data) {
		assert false : "Not yet implemented";
		return null;
	}

	public static List<Tuple> min(List<Tuple> data) {
		assert false : "Not yet implemented";
		return null;
	}

	public static List<Tuple> max(List<Tuple> data) {
		assert false : "Not yet implemented";
		return null;
	}

	public static List<Tuple> count(List<Tuple> data) {
		Value[] values = Value.createValueArray(data.size());
		Schema schema = new Schema();
		schema.addColumn("count", Type.getIntType());
		return Tuple.createList(new Tuple(schema, values));
	}

	public static List<Tuple> average(List<Tuple> data) {
		assert false : "Not yet implemented";
		return null;
	}
}
