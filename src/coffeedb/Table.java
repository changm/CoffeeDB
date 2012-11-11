package coffeedb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class Table implements Serializable {
	private String _tableName;
	private Schema _schema;
	
	// Really is this it?
	// Might get a lot harder once we have transactions
	private List<Tuple> _data;
	
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
	
	public List<Tuple> getData() {
		return _data;
	}
	
	public void insertTuples(List<Tuple> tuples) {
		_data.addAll(tuples);
	}
	
	public int getColumnIndex(String columnName) {
		assert (_schema.hasColumn(columnName));
		return _schema.getIndex(columnName);
	}
}
