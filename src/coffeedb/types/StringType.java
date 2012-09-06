package coffeedb.types;

public class StringType extends Type {
	private int _size;
	
	public StringType(int size) {
		_size = size;
	}
	
	public boolean isString() { return true; }
	
	public int getSize() {
		return _size;
	}
	
	public TypeEnum getEnum() { return TypeEnum.STRING; }

}
