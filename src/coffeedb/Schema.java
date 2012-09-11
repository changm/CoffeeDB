package coffeedb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import coffeedb.types.Type;

/***
 * We're assuming row based schemas here.
 * Maybe we'll try a column store another day
 * @author masonchang
 */
public class Schema {
	public ArrayList<String> _columnNames;
	public ArrayList<Type> _columnTypes;
	
	public Schema() {
		init();
	}
	
	public Schema(String[] columns, Type[] types) {
		assert (columns != null);
		assert (types != null);
		init();
		
		for (String s : columns) {
			_columnNames.add(s);
		}
		
		for (Type t : types) {
			_columnTypes.add(t);
		}
	}
	
	// to reserialize
	public Schema(byte[] data) {
		init();
		recoverFromData(data);
	}
	
	private void init() {
		_columnNames = new ArrayList<String>();
		_columnTypes = new ArrayList<Type>();
	}
	
	public void addColumn(String columnName, Type type) {
		assert (columnName.length() > 0);
		_columnNames.add(columnName);
		_columnTypes.add(type);
	}
	
	public int getSize() {
		int size = 0;
		for (Type type : _columnTypes) {
			size += type.getSize();
		}
		
		return size;
	}

	public byte[] serialize() throws IOException {
		assert (_columnNames.size() == _columnTypes.size());
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write(_columnNames.size());
		
		for (String column : _columnNames) {
			buffer.write(column.length());
			buffer.write(column.getBytes());
		}
		
		for (Type type : _columnTypes) {
			buffer.write(type.getEnum().ordinal());
		}
		
		return buffer.toByteArray();
	}
	
	private void recoverFromData(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int columnCount = buffer.getInt();
		byte[] column = new byte[255];
		
		for (int i = 0; i < columnCount; i++) {
			int columnNameLength = buffer.getInt();
			assert (columnNameLength < 255);
			String columnName = new String(buffer.get(column, 0, columnNameLength).array());
			_columnNames.add(columnName);
		}
		
		for (int i = 0; i < columnCount; i++) {
			int ordinal = buffer.getInt();
			_columnTypes.add(Type.getType(ordinal));
		}
	}

}
