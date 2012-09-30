package coffeedb.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coffeedb.CoffeeDB;
import coffeedb.Table;
import coffeedb.Value;
import coffeedb.types.Type;

public class UpdateTests {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testInsert() {
		CoffeeDB database = CoffeeDB.getInstance();
		database.runQuery("create table test (a int, b int);");
		database.runQuery("insert into test values (10, 20);");
		database.runQuery("insert into test values (30, 20);");
		database.runQuery("select * from test;");
		database.runQuery("update test SET a = 50 where b = 20;");
		
		Value setValue = new Value(Type.getIntType(), "50");
		String tableName = "test";
		TestUtil.assertColumnIsValue(tableName, "a", setValue);
	}
}
