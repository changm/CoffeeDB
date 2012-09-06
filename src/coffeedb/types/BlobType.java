package coffeedb.types;

public class BlobType extends Type {
	public boolean isBlob() { return true; }
	public int getSize() { return -1; }
	public boolean isVarSize() { return true; }
	public TypeEnum getEnum() { return TypeEnum.BLOB; }
}
