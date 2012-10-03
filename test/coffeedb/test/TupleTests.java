package coffeedb.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.types.Type;

public class TupleTests {

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
	public void createTuple() {
		Schema schema = new Schema();
		String columnName = "test";
		schema.addColumn(columnName, Type.getIntType());
		
		Tuple testTuple = new Tuple(schema);
		Value ten = new Value(Type.getIntType(), 10);
		testTuple.setValue(columnName, ten);
		
		assertTrue(testTuple.getValue(columnName).toInt() == 10);
	}
	
	@Test
	public void mergeTuple() {
		Tuple leftTuple = new Tuple(10, 20);
		Tuple rightTuple = new Tuple(30, 40);
		
		Tuple mergedTuple = Tuple.merge(leftTuple, rightTuple);
		for (int i = 0; i < 2; i++) {
			assertTrue(mergedTuple.getValue(i).equals(leftTuple.getValue(i)));
		}
		
		for (int i = 2; i < 4; i++) {
			assertTrue(mergedTuple.getValue(i).equals(rightTuple.getValue(i - 2)));
		}
	}

}
