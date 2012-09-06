package coffeedb.types;

public class IntType extends Type {
	public boolean isInt() { return true; }
	public int getSize() {
		return 4;
	}
	public TypeEnum getEnum() { return TypeEnum.INTEGER; }
}
