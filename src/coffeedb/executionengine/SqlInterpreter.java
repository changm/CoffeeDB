package coffeedb.executionengine;

import java.util.ArrayList;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;

import coffeedb.QueryPlan;
import coffeedb.Tuple;
import coffeedb.operators.Operator;

public class SqlInterpreter {
	public SqlInterpreter() {
	}
	
	public Tuple[] runPlan(QueryPlan plan) {
		ArrayList<Operator> operators = plan.getOperators();
		assert (operators.size() == 1);
		ArrayList<Tuple> results = new ArrayList<Tuple>();
	
		Operator operator = operators.get(0);
		operator.open();
		while (operator.hasNext()) {
			results.add(operator.next());
		}
		operator.close();
		
		return results.toArray(new Tuple[results.size()]);
	}
}
