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
	
	private void init() {
		_columnNames = new ArrayList<String>();
		_columnTypes = new ArrayList<Type>();
	}
	
	public void addColumn(String columnName, Type type) {
		assert (columnName.length() > 0);
		_columnNames.add(columnName);
		_columnTypes.add(type);
	}
	
	private byte[] getInt(int value) {
		return ByteBuffer.allocate(4).putInt(value).array();
	}

	public byte[] serialize() throws IOException {
		assert (_columnNames.size() == _columnTypes.size());
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write(getInt(_columnNames.size()));
		
		for (String column : _columnNames) {
			buffer.write(getInt(column.length()));
			buffer.write(column.getBytes());
		}
		
		for (Type type : _columnTypes) {
			buffer.write(getInt(type.getEnum().ordinal()));
		}
		
		return buffer.toByteArray();
	}
	
	public void recover(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int columnCount = buffer.getInt();
				
		for (int i = 0; i < columnCount; i++) {
			int columnNameLength = buffer.getInt();
			// Have to allocate a new byte buffer 
			// Otherwise creating a new string from a byte buffer
			// Includes the empty space
			byte[] column = new byte[columnNameLength];
			assert (columnNameLength < 255);
			
			buffer.get(column, 0, columnNameLength);
			String columnName = new String(column);
			_columnNames.add(columnName);
		}
		
		for (int i = 0; i < columnCount; i++) {
			int ordinal = buffer.getInt();
			_columnTypes.add(Type.getType(ordinal));
		}
	}
	
	public int numberOfColumns() {
		assert (_columnNames.size() == _columnTypes.size());
		return _columnNames.size();
	}
	
	public boolean equals(Object object) {
		if (object instanceof Schema) {
			return equalSchema((Schema) object);
		}
		
		return false;
	}
	
	public int getIndex(String columnName) {
		assert (_columnNames.contains(columnName));
		return _columnNames.indexOf(columnName);
	}
	
	public Type getColumnType(String columnName) {
		return _columnTypes.get(getIndex(columnName));
	}

	private boolean equalSchema(Schema other) {
		if (_columnNames.size() != other._columnNames.size()) return false;
		
		for (int i = 0; i < this._columnNames.size(); i++) {
			String thisName = _columnNames.get(i);
			String otherName = other._columnNames.get(i);
			if (!thisName.equals(otherName)) return false;
			
			Type thisType = _columnTypes.get(i);
			Type otherType = other._columnTypes.get(i);
			if (!thisType.equals(otherType)) return false;
		}
		
		return true;
	}
	
	public int columnCount() {
		assert (_columnNames.size() == _columnTypes.size());
		return _columnNames.size();
	}
	
	public boolean hasColumn(String columnName) {
		return _columnNames.contains(columnName);
	}
}
