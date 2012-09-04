package coffeedb;

import java.util.ArrayList;

import coffeedb.types.Type;
import coffeedb.values.Value;

/***
 * Represents one tuple that is stored in the database.
 * A tuple is a collection of Values
 * Each value has a tpye
 * @author masonchang
 */
public class Tuple {
	private Value[] _values;
	private Schema _schema;
	
	public Tuple(Schema schema, Object...objects) {
		_values = Value.createValueArray(objects);
		_schema = schema;
		consistencyCheck();
	}
	
	public Tuple(Schema schema, Value[] values) {
		_values = values;
		_schema = schema;
		consistencyCheck();
	}
	
	private void consistencyCheck() {
		for (int i = 0; i < _values.length; i++) {
			Value value = _values[i];
			Type schemaType = _schema._columnTypes.get(i);
			assert (schemaType.equals(value.getType()));
		}
		
	}
	
	public Tuple(Iterable<Value> values) {
		ArrayList<Value> list = new ArrayList<Value>();
		for (Value v : values) {
			assert (v != null);
			list.add(v);
		}
		
		_values = new Value[list.size()];
		list.toArray(_values);
	}

	public void setValue(int field, Value value) {
		assert (field < _values.length);
		_values[field] = value;
	}
	
	public Value getValue(int field) {
		assert (field < _values.length);
		return _values[field];
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Value v : _values) {
			sb.append(v.toString());
			sb.append(" ");
		}
		
		return sb.toString();
	}
	
	public boolean equals(Tuple tuple) {
		for (int i = 0; i < _values.length; i++) {
			Value current = _values[i];
			Value other = tuple._values[i];
			if (!current.equals(other)) return false;
		}
		
		return true;
	}
	
	public int getIndex(String columnName) {
		for (int i = 0; i < _schema._columnNames.size(); i++) {
			String column = _schema._columnNames.get(i);
			if (column.equalsIgnoreCase(columnName)) return i;
		}
		
		assert false : "Unknown column";
		return -1;
	}
}
