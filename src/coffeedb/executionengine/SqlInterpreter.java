package coffeedb.executionengine;

import java.util.ArrayList;
import java.util.List;
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
	
	private List<Tuple> runPlan(QueryPlan plan) {
		ArrayList<Operator> operators = plan.getOperators();
		ArrayList<Tuple> results = new ArrayList<Tuple>();
	
		Operator operator = operators.get(0);
		return operator.getData();
	}
	
	public void run() {
		try {
			QueryPlan plan = _transactionManager.getNextPlan();
			List<Tuple> result = runPlan(plan);
			
			Transaction transaction = plan.getTransaction();
			transaction.commitTransaction(result);
		} catch (NoSuchElementException e) {
			System.err.println("Could not run plan");;
			e.printStackTrace();
			System.exit(1);
		}
	} // end for
}
