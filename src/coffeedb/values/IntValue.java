package coffeedb.values;

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
}
