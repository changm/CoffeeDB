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
	public void testCreateTable() {
		Catalog catalog = CoffeeDB.getInstance().getCatalog();
		Schema schema = TestUtil.getSimpleSchema();
		Table table = new Table("TestTable", schema);
		catalog.addTable(table);
		
		assertTrue(catalog.tableExists(table));
	}
	
	@Test
	public void testDeleteTable() {
		Catalog catalog = CoffeeDB.catalog();
		String tableName = "test";
		catalog.addTable(TestUtil.getSimpleTable(tableName));
		assertTrue(catalog.tableExists(tableName));
		
		assertTrue(catalog.deleteTable(tableName));
		assertFalse(catalog.tableExists(tableName));
	}
	
	@Test
	public void cannotDeleteNonCreatedTableTest() {
		Catalog catalog = CoffeeDB.catalog();
		
		String tableName = "test";
		assertFalse(catalog.deleteTable(tableName));
	}
}
