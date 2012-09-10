package coffeedb;

import java.util.ArrayList;
import java.util.Iterator;

public class Table {
	private String _tableName;
	private Schema _schema;
	
	// Really is this it?
	// Might get a lot harder once we have transactions
	private ArrayList<Tuple> _data;
	
	public Table(String tableName, Schema schema) {
		_tableName = tableName;
		_schema = schema;
		_data = new ArrayList<Tuple>();
	}
	
	public String getTableName() {
		return _tableName;
	}
	
	public Schema getSchema() {
		return _schema;
	}

	public synchronized void insertTuple(Tuple tuple) {
		_data.add(tuple);
	}

	public Iterator<Tuple> getIterator() {
		// TODO: Ensure consistency, e.g. makes a copy or is actual 
		// reference. Or somehow make read only
		return _data.iterator();
	}

	public synchronized boolean hasTuple(Tuple testTuple) {
		return _data.contains(testTuple);
	}
	
	public void clear() {
		_data.clear();
	}
}
