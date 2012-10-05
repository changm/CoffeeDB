package coffeedb;

import coffeedb.types.Type;

public class ConstantValue extends Value {
	public ConstantValue(Type type, Object object) {
		super(type, object);
	}
	
	public boolean isConstant() {
		return true;
	}
}
