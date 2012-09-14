package coffeedb.types;

public abstract class Type {
	public boolean isInt() { return false; }
	public boolean isBlob() { return false; }
	public boolean isString() { return false; }
	public boolean isFunction() { return false; }
	public boolean isLong() { return false; }
	public abstract int getSize();  // Implement by knowing JVM specification grr :/ No sizeof operator
	public boolean isVarSize() { return false; }
	
	private static IntType _intType = new IntType();
	private static StringType _stringType = new StringType(50);
	private static LongType _longType = new LongType();
	private static FunctionType _functionType = new FunctionType();
	
	public static IntType getIntType() { return _intType; }
	public static StringType getStringType() { return _stringType; }
	public static LongType getLongType() { return _longType; }
	public static Type getFunctionType() { return _functionType; }
	
	public static Type getType(String typeName) {
		if (typeName.equalsIgnoreCase("int")) {
			return _intType;
		} else if (typeName.equalsIgnoreCase("blob")) {
			return new BlobType();
		} else if (typeName.equalsIgnoreCase("string")) {
			return _stringType;
		} else if (typeName.equalsIgnoreCase("long")) {
			return _longType;
		}
		
		assert false : "Unknown type : " + typeName;
		return null;
	}
	
	public static Type[] toTypeArray(Object...objects) {
		int length = objects.length;
		Type[] typeArray = new Type[length];
		for (int i = 0; i < length; i++) {
			Type type = (Type) objects[i];
			assert (type != null);
			typeArray[i] = type;
		}
		
		return typeArray;
	}
	
	public boolean equals(Type type) {
		return this == type;
	}
	
	// To make it easier to switch
	public abstract TypeEnum getEnum();
	
	public static Type getType(int ordinal) {
		TypeEnum enumType = TypeEnum.values()[ordinal];
		switch (enumType) { 
		case INTEGER: return getIntType();
		case LONG: return getLongType();
		case STRING: return getStringType();
		case FUNCTION: return getFunctionType();
		case BLOB: {
			assert (false);
			return null;
		}
		default:
			break;
		}
		
		assert (false);
		return getIntType();
	}
}
