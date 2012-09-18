package coffeedb;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import coffeedb.operators.Predicate;
import coffeedb.types.Type;

/***
 * Represents a wrapped raw value
 * @author masonchang
 */
public class Value {
	private byte[] _data;
	private Type _type;
	private ByteBuffer _buffer;
	
	public Value(Type type) {
		assert (type != null);
		_type = type;
		_buffer = ByteBuffer.allocate(_type.getSize());
	}
	
	public Type getType() {
		return _type;
	}
	
	public Value(Type type, String value) {
		_type = type;
		_buffer = ByteBuffer.allocate(_type.getSize());
		toType(value);
	}
	
	private void toType(String value) {
		switch (_type.getEnum()) {
		case BLOB:
			assert (false);
			break;
		case FUNCTION:
			assert (false);
			break;
		case INTEGER:
			setInt(Integer.parseInt(value));
			break;
		case LONG:
			assert (false);
			break;
		case STRING:
			setString(value);
			break;
		default:
			assert (false);
			break;
		}
	}

	public void setInt(int value) {
		assert (_type.isInt());
		_buffer.putInt(value);
		_data = _buffer.array();
	}
	
	public void setString(String value) {
		assert (_type.isString());
		_data = value.getBytes();
	}
	
	public void setLong(long value) {
		_buffer.putLong(value);
		_data = _buffer.array();
	}
	
	public boolean compare(Predicate predicate, Value other) {
		switch (predicate) {
		case LESS:
			return !greaterThan(this, other);
		default:
			assert (false);
			return false;
		}
	}
	
	public boolean equals(Value other) {
		if (!this._type.equals(other._type)) return false;
		switch (_type.getEnum()) {
		case BLOB:
			assert (false);
			break;
		case INTEGER:
			return this.toInt() == other.toInt();
		case LONG:
			assert (false);
		case STRING:
			return this.dataToString().equals(other.dataToString());
		default:
			assert (false);
			break;
		}
		
		return false;
	}

	private boolean greaterThan(Value value, Value other) {
		assert (value.getType().equals(other.getType()));
		return value.toInt() < other.toInt();
	}

	public static Value[] createValueArray(Object...objects) {
		Value[] values = new Value[objects.length];
		for (int i = 0; i < objects.length; i++) {
			Object value = objects[i];
			values[i] = convertObject(value);
		}
		
		return values;
	}

	private static Value convertObject(Object value) {
		if (value instanceof Integer) {
			return createInt((Integer) value);
		} else if (value instanceof String) {
			return createString((String) value);
		}
		
		assert (false);
		return null;
	}
	
	public static Value createInt(int intVal) {
		Value value = new Value(Type.getIntType());
		value.setInt(intVal);
		return value;
	}
	
	public static Value createString(String stringVal) {
		Value value = new Value(Type.getStringType());
		value.setString(stringVal);
		return value;
	}
	
	public String toString() {
		switch (_type.getEnum()) {
		case INTEGER:
			return Integer.toString(toInt());
		case STRING:
			return dataToString();
		case FUNCTION:
			return ((Function) this)._functionName;
		default:
			assert (false);
			break;
		}
		
		return "Unknown type";
	}
	
	private String dataToString() {
		return new String(_data);
	}

	public int toInt() {
		assert (_type.isInt());
		ByteBuffer wrapper = ByteBuffer.wrap(_data);
		return wrapper.getInt();
	}

	public byte[] getData() {
		return _data;
	}
}
