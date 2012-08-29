package coffeedb.types;

/***
 * Gives a type descriptor to a given tuple
 * @author masonchang
 *
 */
public abstract class Type {
	public boolean isInt() { return false; }
	public boolean isBlob() { return false; }
	public boolean isString() { return false; }
	
	private static IntType _intType = new IntType();
	private static StringType _stringType = new StringType();
	
	public static IntType getIntType() { return _intType; }
	public static StringType getStringType() { return _stringType; }
	
	public static Type getType(String typeName) {
		if (typeName.equalsIgnoreCase("int")) {
			return _intType;
		} else if (typeName.equalsIgnoreCase("blob")) {
			return new BlobType();
		} else if (typeName.equalsIgnoreCase("string")) {
			return _stringType;
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
}
