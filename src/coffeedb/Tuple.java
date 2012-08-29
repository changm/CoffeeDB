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
	private Type[] _type;
	private Value[] _values;
	
	public Tuple(Type[] type) {
		assert (type != null);
		_type = type;
		_values = new Value[type.length];
	}
	
	public void setValue(int field, Value value) {
		assert (field < _type.length);
		_values[field] = value;
	}
	
	public Value getValue(int field) {
		assert (field < _type.length);
		return _values[field];
	}
	
	public Type[] getType() {
		return _type;
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
