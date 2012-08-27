package coffeedb.types;

public abstract class Type {
	public abstract void parse(byte[] data);
	public abstract byte[] serialize();
	public abstract String toString();
}
