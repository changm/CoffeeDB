package coffeedb.values;

import java.util.ArrayList;

import coffeedb.types.Type;

public abstract class Value {
	public Value() {}
	public abstract Type getType(); 
	public abstract boolean equals(Value other);
	
	/***
	 * Converts an itertable list of values into a value aray
	 * @param objects
	 * @return
	 */
	public static Value[] toValueArray(Object...objects) {
		Value[] values = new Value[objects.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = (Value) objects[i];
		}
		
		return values;
	}
	
	/***
	 * Creates raw values from primitive types
	 * @param objects
	 * @return
	 */
	public static Value[] createValueArray(Object...objects) {
		ArrayList<Value> values = new ArrayList<Value>();
		for (Object object : objects) {
			if (object instanceof Integer) {
				Integer value = (Integer) object;
				values.add(new IntValue(value.intValue()));
			} else if (object instanceof String) {
				values.add(new StringValue((String) object));
			} else{
				assert false : "Unknown object type";
			}
		}
		
		return toValueArray(values.toArray());
	}
}
