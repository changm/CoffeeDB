package coffeedb.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coffeedb.Catalog;
import coffeedb.CoffeeDB;
import coffeedb.Table;
import coffeedb.Tuple;

public class SystemTest {
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
	public void createAndInsert() {
		CoffeeDB database = CoffeeDB.getInstance();
		Catalog catalog = database.getCatalog();
		
		String tableName = "test";
		Table table = catalog.getTable(tableName);
		
		assertFalse(catalog.tableExists(tableName));
		database.runQuery("create table test (a int, b int)");
		assertTrue(catalog.tableExists(tableName));
		
		Tuple inserted = new Tuple(table.getSchema(), 10, 20);
		database.runQuery("insert into test values (10, 20);");
		assertTrue(TestUtil.tupleExists(tableName, inserted));
		
		database.runQuery("select * from test;");
	}

}
