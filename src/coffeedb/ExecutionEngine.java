package coffeedb;

import coffeedb.executionengine.SqlInterpreter;
import coffeedb.executionengine.TransactionManager;

public class ExecutionEngine {
	private SqlInterpreter _interpreter;
	private TransactionManager _transactionManager;
	
	public ExecutionEngine() {
		init();
	}
	
	private void init() {
		_transactionManager = new TransactionManager();
		_interpreter = new SqlInterpreter(_transactionManager);
		//_interpreter.start();
	}

	public Transaction executeQueryPlan(QueryPlan plan) {
		Transaction transaction = _transactionManager.addQueryPlan(plan);
		_interpreter.run();
		return transaction;
	}

	public void shutdown() {
		_interpreter._finish = true;
	}
}
