package coffeedb.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coffeedb.Catalog;
import coffeedb.CoffeeDB;
import coffeedb.Logger;
import coffeedb.Schema;

public class LoggerTest {

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
	public void testSnapshotFileExists() {
		CoffeeDB database = CoffeeDB.getInstance();
		TestUtil.createSimpleTable("test"); 
		
		Logger logger = database.getLogger();
		ArrayList<String> logFiles = logger.snapshot(database);
		
		for (String logFile : logFiles) {
			File file = new File(logFile);
			assertTrue(file.exists());
		}
	}
	
	@Test
	public void testRecoverSchema() {
		CoffeeDB database = CoffeeDB.getInstance();
		Catalog catalog = database.getCatalog();
		String tableName = "test";
		TestUtil.createSimpleTable(tableName);
		
		Schema oldSchema = catalog.getTable(tableName).getSchema();
		
		database.snapshot();
		database.reset();
		assertFalse(catalog.tableExists(tableName));
		
		database.recoverFromLog();
		
		Schema recoverSchema = catalog.getTable(tableName).getSchema();
		assertTrue(oldSchema.equals(recoverSchema));
	}

}
