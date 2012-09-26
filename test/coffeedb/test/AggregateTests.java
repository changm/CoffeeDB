package coffeedb.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coffeedb.CoffeeDB;
import coffeedb.Tuple;

public class AggregateTests {

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
	public void count() {
		CoffeeDB database = CoffeeDB.getInstance();
		
		database.runQuery("create table test (a int, b int);");
		database.runQuery("insert into test values (10, 20);");
		
		List<Tuple> result = database.runQuery("select count(a) from test;");
		assertTrue (result.size() == 1);
		assertTrue (result.get(0).getValue(0).toInt() == 1);
	}

}
