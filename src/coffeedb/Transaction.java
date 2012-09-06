package coffeedb;

public class Transaction {
	private static long NEXT_ID = 0;
	private long _id;
	
	public Transaction() {
		_id = NEXT_ID++;
	}
	
	public long getId() {
		return _id;
	}
	
	public void commitTransaction() {
		
	}
	
	public void abortTransaction() {
		
	}

}
