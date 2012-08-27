package coffeedb.types;

import java.nio.ByteBuffer;

public class IntType extends Type {
	private int _value;

	public void parse(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		_value = buffer.getInt();
	}

	@Override
	public byte[] serialize() {
		int intSize = 4;
		ByteBuffer buffer = ByteBuffer.allocate(intSize);
		buffer.putInt(_value);
		return buffer.array();
	}

	public String toString() {
		return Integer.toString(_value);
	}
	
	public int getValue() {
		return _value;
	}

}
