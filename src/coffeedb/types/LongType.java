package coffeedb.types;

public class LongType extends Type {
	public boolean isLong() { return true; }

	public int getSize() {
		return 8;
	}
	
	public TypeEnum getEnum() { return TypeEnum.LONG; }
}
