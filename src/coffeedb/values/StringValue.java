package coffeedb.values;

import coffeedb.operators.Predicate;
import coffeedb.types.Type;

public class StringValue extends Value {
	private String _value;
	
	public StringValue(String value) {
		_value = value;
	}
	
	public String getValue() {
		return _value;
	}
	
	public Type getType() {
		return Type.getStringType();
	}
	
	public String toString() {
		return _value;
	}

	public boolean equals(Value other) {
		if (other instanceof StringValue) {
			return _value.equals(((StringValue)other)._value);
		}
		
		return false;
	}

	public boolean compare(Predicate filter, IntValue value) {
		assert false : "Not yet implemented";
		return false;
	}
}
