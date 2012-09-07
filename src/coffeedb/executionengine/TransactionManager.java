package coffeedb.executionengine;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

import coffeedb.QueryPlan;
import coffeedb.Transaction;

/***
 * A much simpler version of a transaction system compared to a SystemR type implementation.
 * Since we're an in memory database, we'll just serialize transactions with a traditional consumer / 
 * producer model. Incoming requests create a query plan and throw it in the queue
 * A separate thread just pops items off the queue and modifies data as needed. 
 * 
 * We may not even need this, could probably just use the ExecutionEngine for now
 * @author masonchang
 *
 */
public class TransactionManager {
	private ConcurrentLinkedQueue<QueryPlan> _queryPlans;
	
	public TransactionManager() {
		_queryPlans = new ConcurrentLinkedQueue<QueryPlan>();
	}
	
	public Transaction addQueryPlan(QueryPlan plan) {
		Transaction planTransaction = new Transaction();
		plan.setTransaction(planTransaction);
		_queryPlans.add(plan);
		return planTransaction;
	}
	
	public QueryPlan getNextPlan() throws NoSuchElementException {
		return _queryPlans.remove();
	}
}
