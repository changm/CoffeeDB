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
		Tuple[] result = new Tuple[1];
	
		Operator operator = operators.get(0);
		operator.open();
		result[0] = operator.getNext();
		operator.close();
		
		return result;
	}
}
