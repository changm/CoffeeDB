package coffeedb.types;

public class DoubleType extends Type {
	public boolean isDouble() { return true; }
	public int getSize() { return 8; }
	public TypeEnum getEnum() { return TypeEnum.DOUBLE; }
}
