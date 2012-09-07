package coffeedb;

public class Transaction {
	private static long NEXT_ID = 0;
	private long _id;
	private Tuple[] _result;
	
	private boolean _didFinish;
	private boolean _didCommit;
	
	public Transaction() {
		_id = NEXT_ID++;
		_didFinish = false;
		_didCommit = false;
	}
	
	public long getId() {
		return _id;
	}
	
	public void commitTransaction(Tuple[] result) {
		_didCommit = true;
		_result = result;
	}
	
	public void abortTransaction() {
		_didCommit = false;
	}

	public Tuple[] getResult() {
		return _result;
	}
	
	public synchronized boolean didFinish() {
		return _didFinish;
	}
	
	public synchronized boolean didCommit() {
		return _didCommit;
	}
}
