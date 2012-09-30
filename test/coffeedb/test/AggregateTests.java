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
		TestUtil.reset();
		CoffeeDB database = CoffeeDB.getInstance();
		database.runQuery("create table test (a int, b int);");
		database.runQuery("insert into test values (10, 20);");
		database.runQuery("insert into test values (30, 50);");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private void assertAggregate(List<Tuple> result, int value) {
		assertTrue (result.size() == 1);
		assertTrue (result.get(0).getValue(0).toInt() == value);
	}
	
	private void assertDoubleAggregate(List<Tuple> result, double value) {
		assertTrue(result.size() == 1);
		assertTrue (result.get(0).getValue(0).toDouble() == value);
	}
	
	@Test
	public void sum() {
		CoffeeDB database = CoffeeDB.getInstance();
		List<Tuple> result = database.runQuery("select sum(a) from test;");
		assertAggregate(result, 40);
	}

	@Test
	public void count() {
		CoffeeDB database = CoffeeDB.getInstance();
		List<Tuple> result = database.runQuery("select count(a) from test;");
		assertAggregate(result, 2);
	}
	
	@Test
	public void min() {
		CoffeeDB database = CoffeeDB.getInstance();
		List<Tuple> result = database.runQuery("select min(a) from test;");
		assertAggregate(result, 10);
	}
	
	@Test
	public void max() {
		CoffeeDB database = CoffeeDB.getInstance();
		List<Tuple> result = database.runQuery("select max(a) from test;");
		assertAggregate(result, 30);
	}
	
	@Test
	public void avg() {
		CoffeeDB database = CoffeeDB.getInstance();
		List<Tuple> result = database.runQuery("select avg(a) from test;");
		assertDoubleAggregate(result, 20);
	}
}
