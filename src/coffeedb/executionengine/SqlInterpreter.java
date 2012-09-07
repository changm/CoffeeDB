package coffeedb.executionengine;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;

import coffeedb.QueryPlan;
import coffeedb.Transaction;
import coffeedb.Tuple;
import coffeedb.operators.Operator;

public class SqlInterpreter extends Thread {
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
		while (!_finish) {
			try {
				QueryPlan plan = _transactionManager.getNextPlan();
				Tuple[] result = runPlan(plan);
				
				Transaction transaction = plan.getTransaction();
				transaction.commitTransaction(result);
			} catch (NoSuchElementException e) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} // end for
	}
}
