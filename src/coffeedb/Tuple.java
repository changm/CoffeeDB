package coffeedb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import coffeedb.types.Type;

/***
 * Represents one tuple that is stored in the database.
 * A tuple is a collection of Values
 * Each value has a tpye
 * @author masonchang
 */
public class Tuple {
	private Value[] _values;
	private Schema _schema;
	
	public static List<Tuple> createList(Object...objects) {
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		for (Object o : objects) {
			Tuple t = (Tuple) o;
			result.add(t);
		}
		
		return result;
	}
	
	public Tuple(Schema schema) {
		assert (schema != null);
		_schema = schema;
		_values = new Value[_schema.numberOfColumns()];
	}
	
	public Tuple(Schema schema, Object...objects) {
		_values = Value.createValueArray(objects);
		_schema = schema;
		assert (schema != null);
	}
	
	public Tuple(Schema schema, Value[] values) {
		assert (schema != null);
		_values = values;
		_schema = schema;
	}
	
	public Tuple (Value[] values) {
		_values = values;
		createSchema(values);
	}
	
	public Tuple (Object...objects){
		_values = Value.createValueArray(objects);
		createSchema(_values);
	}
	
	private void createSchema(Value[] values) {
		_schema = new Schema();
		for (Value v : values) {
			_schema.addColumn("", v.getType());
		}
	}

	public Tuple(Schema schema, Iterable<Value> values) {
		_schema = schema;
		setValues(values);
	}
	
	public Tuple(Iterable<Value> values) {
		setValues(values);
	}
	
	public Schema getSchema() {
		return _schema;
	}

	private void setValues(Iterable<Value> values) {
		ArrayList<Value> list = new ArrayList<Value>();
		for (Value v : values) {
			assert (v != null);
			list.add(v);
		}
		
		_values = new Value[list.size()];
		list.toArray(_values);
	}
	
	public void setValue(int column, Value value) {
		assert (column < _values.length);
		_values[column] = value;
	}
	
	public void setValue(String column, Value value) {
		int columnIndex = getIndex(column);
		_values[columnIndex] = value;
	}
	
	public Value getValue(int column) {
		assert (column < _values.length);
		return _values[column];
	}
	
	public Value getValue(String columnName) {
		int index = getIndex(columnName);
		return getValue(index);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < _values.length; i++) {
			sb.append(_values[i].toString());
			if (i != _values.length - 1) {
				sb.append(" | ");
			}
		}
		
		return sb.toString();
	}
	
	public boolean equals(Tuple tuple) {
		for (int i = 0; i < _values.length; i++) {
			Value current = _values[i];
			Value other = tuple._values[i];
			if (!current.equals(other)) return false;
		}
		
		return true;
	}
	
	public int getIndex(String columnName) {
		for (int i = 0; i < _schema._columnNames.size(); i++) {
			String column = _schema._columnNames.get(i);
			if (column.equalsIgnoreCase(columnName)) return i;
		}
		
		assert false : "Unknown column " + columnName;
		return -1;
	}
	
	private byte[] getInt(int value) {
		return ByteBuffer.allocate(4).putInt(value).array();
	}
	
	public byte[] serialize() {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		assert (_schema.numberOfColumns() == _values.length);
		
		for (int i = 0; i < _values.length; i++) {
			Value value = _values[i];
			byte[] valueOutput = value.getData();
			
			try {
				if (value.getType().isString()) {
					byteArray.write(getInt(value.toString().length()));
				}
				
				byteArray.write(valueOutput);
			} catch (IOException e) {
				System.err.println("Error serializing tuple: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		return byteArray.toByteArray();
	}

	public void recover(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		for (int i = 0; i < _schema.numberOfColumns(); i++) {
			Type columnType = _schema._columnTypes.get(i);
			Value columnValue = new Value(columnType);
			
			switch (columnType.getEnum()) {
			case INTEGER:
			{
				columnValue.setInt(buffer.getInt());
				break;
			}
			default:
				assert (false);
				break;
			}
			
			_values[i] = columnValue;
		}
	}

	public static Tuple merge(Tuple left, Tuple right) {
		Schema leftSchema = left.getSchema();
		Schema mergedSchema = Schema.mergeSchemas(left._schema, right._schema);
		Tuple result = new Tuple(mergedSchema);
		for (int i = 0; i < leftSchema.columnCount(); i++) {
			result.setValue(i, left.getValue(i));
		}
		
		for (int i = leftSchema.columnCount(); i < mergedSchema.columnCount(); i++) {
			result.setValue(i, right.getValue(i - leftSchema.columnCount()));
		}
		
		return result;
	}
}
