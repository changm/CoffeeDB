package coffeedb.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coffeedb.CoffeeDB;
import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.types.Type;

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
		database.runQuery("insert into test values (20, 20);");
		database.runQuery("insert into test values (30, 50);");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private Tuple createGroupByResult(int aggregateValue, String aggregateFunction) {
		return Tuple.createTupleAndSchema(aggregateValue, aggregateFunction);
	}
	
	private void assertAggregate(List<Tuple> result, int value) {
		assertTrue (result.size() == 1);
		assertTrue (result.get(0).getValue(0).toInt() == value);
	}
	
	private void assertAggregateGroupBy(List<Tuple> result, Object...objects) {
		List<Tuple> expected = Tuple.createList(objects);
		TestUtil.tuplesExist(result, expected);
	}
	
	@Test
	public void sum() {
		CoffeeDB database = CoffeeDB.getInstance();
		List<Tuple> result = database.runQuery("select sum(a) from test;");
		assertAggregate(result, 60);
	}

	@Test
	public void count() {
		CoffeeDB database = CoffeeDB.getInstance();
		List<Tuple> result = database.runQuery("select count(a) from test;");
		assertAggregate(result, 3);
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
		Tuple expected = Tuple.createTupleAndSchema(20.0, "avg");
		assertTrue(TestUtil.tupleExist(result, expected));
	}
	
	@Test
	public void avgGroupBy() {
		CoffeeDB database = CoffeeDB.getInstance();
		List<Tuple> result = database.runQuery("select avg(a) from test group by b;");
		Tuple first = Tuple.createTupleAndSchema(30.0, "avg");
		Tuple second = Tuple.createTupleAndSchema(15.0, "avg");
		assertAggregateGroupBy(result, first, second);
	}
	
	@Test
	public void sumGroupBy() {
		CoffeeDB database = CoffeeDB.getInstance();
		List<Tuple> result = database.runQuery("select sum(a) from test group by b;");
		Tuple first = createGroupByResult(30, "sum");
		Tuple second = createGroupByResult(30, "sum");
		assertAggregateGroupBy(result, first, second);
	}
	
	@Test
	public void countGroupBy() {
		CoffeeDB database = CoffeeDB.getInstance();
		List<Tuple> result = database.runQuery("select count(a) from test group by b;");
		Tuple first = createGroupByResult(2, "count");
		Tuple second = createGroupByResult(1, "count");
		assertAggregateGroupBy(result, first, second);
	}
	
	@Test
	public void minGroupBy() {
		CoffeeDB database = CoffeeDB.getInstance();
		List<Tuple> result = database.runQuery("select min(a) from test group by b;");
		Tuple first = createGroupByResult(30, "min");
		Tuple second = createGroupByResult(10, "min");
		assertAggregateGroupBy(result, first, second);
	}
	
	@Test
	public void maxGroupBy() {
		CoffeeDB database = CoffeeDB.getInstance();
		List<Tuple> result = database.runQuery("select max(a) from test group by b;");
		Tuple first = createGroupByResult(20, "max");
		Tuple second = createGroupByResult(30, "max");
		assertAggregateGroupBy(result, first, second);
	}
}
