package coffeedb.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coffeedb.Table;
import coffeedb.Tuple;

public class InsertTests {

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
		String tableName = "test";
		Table table = TestUtil.createSimpleTable(tableName);
		
		Tuple testTuple = TestUtil.createSimpleTuple();
		table.insertTuple(testTuple);
		assertTrue(table.hasTuple(testTuple));
	}
}
