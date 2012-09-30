package coffeedb.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coffeedb.Catalog;
import coffeedb.CoffeeDB;
import coffeedb.Schema;
import coffeedb.Table;

public class DropTests {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		TestUtil.reset();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testDropTable() {
		CoffeeDB database = CoffeeDB.getInstance();
		database.runQuery("create table test (a int, b int);");
		database.runQuery("insert into test values (10, 20);");
		database.runQuery("insert into test values (30, 20);");
		database.runQuery("drop table test;");
		assertFalse(database.getCatalog().tableExists("test"));
	}

}
