package coffeedb.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coffeedb.*;

public class CatalogTests {

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
	public void test() {
		Catalog catalog = CoffeeDB.getInstance().getCatalog();
		Table table = new Table("TestTable");
		catalog.addTable(table);
		
		assertTrue(catalog.tableExists(table));
	}
}
