package coffeedb.values;

import coffeedb.types.Type;

public abstract class Value {
	public Value() {}
	public abstract Type getType(); 
	
	public static Value[] toValueArray(Object...objects) {
		Value[] values = new Value[objects.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = (Value) objects[i];
		}
		
		return values;
	}
}
