package coffeedb;

import coffeedb.types.Type;
import coffeedb.values.Value;

/***
 * Represents one tuple that is stored in the database.
 * A tuple is a collection of Values
 * Each value has a tpye
 * @author masonchang
 */
public class Tuple {
	private Type[] _types;
	private Value[] _values;
	
	public Tuple(Type[] type) {
		assert (type != null);
		_types = type;
		_values = new Value[type.length];
	}
	
	public Tuple(Type[] types, Value[] values) {
		_types = types;
		_values = values;
		consistencyCheck();
	}
	
	private void consistencyCheck() {
		assert (_types.length == _values.length);
		for (int i = 0; i < _types.length; i++) {
			Type type = _types[i];
			Type valueType = _values[i].getType();
			assert (type.equals(valueType));
		}
	}
	
	public void setValue(int field, Value value) {
		assert (field < _types.length);
		_values[field] = value;
	}
	
	public Value getValue(int field) {
		assert (field < _types.length);
		return _values[field];
	}
	
	public Type[] getType() {
		return _types;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Value v : _values) {
			sb.append(v.toString());
			sb.append(" ");
		}
		
		return sb.toString();
	}
}
