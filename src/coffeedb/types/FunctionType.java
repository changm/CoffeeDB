package coffeedb.types;

public class FunctionType extends Type {

	public int getSize() {
		return 0;
	}

	@Override
	public TypeEnum getEnum() {
		return TypeEnum.FUNCTION;
	}

}
