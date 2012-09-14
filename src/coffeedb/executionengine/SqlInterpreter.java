package coffeedb.executionengine;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;

import coffeedb.QueryPlan;
import coffeedb.Transaction;
import coffeedb.Tuple;
import coffeedb.operators.Operator;

public class SqlInterpreter {
	private TransactionManager _transactionManager;
	public boolean _finish;
	
	public SqlInterpreter(TransactionManager transactionManager) {
		_finish = false;
		_transactionManager = transactionManager;
	}
	
	private Tuple[] runPlan(QueryPlan plan) {
		ArrayList<Operator> operators = plan.getOperators();
		ArrayList<Tuple> results = new ArrayList<Tuple>();
	
		Operator operator = operators.get(0);
		operator.open();
		while (operator.hasNext()) {
			results.add(operator.next());
		}
		operator.close();
		return results.toArray(new Tuple[results.size()]);
	}
	
	public void run() {
		try {
			QueryPlan plan = _transactionManager.getNextPlan();
			Tuple[] result = runPlan(plan);
			
			Transaction transaction = plan.getTransaction();
			transaction.commitTransaction(result);
		} catch (NoSuchElementException e) {
			System.err.println("Could not run plan");;
			e.printStackTrace();
			System.exit(1);
		}
	} // end for
}
