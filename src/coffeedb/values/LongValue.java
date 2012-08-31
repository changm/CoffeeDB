package coffeedb.values;

import coffeedb.types.Type;

public class LongValue extends Value {
	private long _value;
	
	public LongValue(long value) {
		_value = value;
	}
	
	public long getValue() {
		return _value;
	}

	public Type getType() {
		return Type.getLongType();
	}

	public boolean equals(Value other) {
		if (other instanceof LongValue) {
			return _value == ((LongValue)other)._value;
		}
		
		return false;
	}
}
