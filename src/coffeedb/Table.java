package coffeedb;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class Table implements Serializable {
	private String _tableName;
	private Schema _schema;
	
	// Really is this it?
	// Might get a lot harder once we have transactions
	private LinkedHashSet<Tuple> _data;
	
	public Table(String tableName, Schema schema) {
		_tableName = tableName;
		_schema = schema;
		_data = new LinkedHashSet<Tuple>();
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
		return _data.iterator();
	}

	public synchronized boolean hasTuple(Tuple testTuple) {
		return _data.contains(testTuple);
	}
	
	public void clear() {
		_data.clear();
	}
	
	public int getTupleCount() {
		return _data.size();
	}
}
