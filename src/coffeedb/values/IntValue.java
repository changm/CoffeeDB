package coffeedb.values;

import coffeedb.operators.Predicate;
import coffeedb.types.Type;

public class IntValue extends Value {
	private int _value;
	
	public IntValue(int value) {
		_value = value;
	}
	
	public int getValue() {
		return _value;
	}
	
	public String toString() {
		return Integer.toString(_value);
	}
	
	public Type getType() {
		return Type.getIntType();
	}

	@Override
	public boolean equals(Value other) {
		if (other instanceof IntValue) {
			return _value == ((IntValue) other)._value;
		}
		
		return false;
	}
	
	public boolean compare(Predicate predicate, Value other) {
		switch (predicate) {
		case LESS:
			return other.compare(predicate, this);
		default:
			assert (false);
			break;
		}
		
		assert (false);
		return false;
	}
	
	public boolean compare(Predicate predicate, IntValue other) {
		switch (predicate) {
		case LESS:
			return this._value < other._value;
		default:
			assert (false);
			break;
		}
		
		assert (false);
		return false;
	}
}
