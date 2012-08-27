package coffeedb.test;

import coffeedb.*;
import coffeedb.types.*;

public class TestUtil {
	public static Schema getSimpleSchema() {
		String[] names = new String[1];
		names[0] = "test";
		
		Type[] types = new Type[1];
		types[0] = new IntType();
		
		return new Schema(names, types);
	}
}
