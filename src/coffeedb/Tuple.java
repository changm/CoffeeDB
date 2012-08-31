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
	
	public Tuple(Object...objects) {
		_values = Value.createValueArray(objects);
	}
	
	public Tuple(Value[] values) {
		_values = values;
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
}
