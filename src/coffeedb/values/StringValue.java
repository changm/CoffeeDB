package coffeedb.values;

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
}
