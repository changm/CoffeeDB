package coffeedb;

import coffeedb.executionengine.SqlInterpreter;

public class ExecutionEngine {
	public ExecutionEngine() {
	}
	
	public void runPlan(QueryPlan plan) {
		assert (plan != null);
		SqlInterpreter interpreter = new SqlInterpreter();
		Tuple[] results = interpreter.runPlan(plan);
		printResults(results);
	}
	
	public void printResults(Tuple[] results) {
		for (Tuple tuple : results) {
			System.out.println(tuple);
		}
	}
}
