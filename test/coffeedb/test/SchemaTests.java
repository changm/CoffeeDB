package coffeedb.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coffeedb.Schema;
import coffeedb.types.Type;

public class SchemaTests {

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
	public void createSchema() {
		String columnName = "test";
		
		Schema testSchema = new Schema();
		testSchema.addColumn(columnName, Type.getIntType());
		
		assertTrue(testSchema.hasColumn(columnName));
		assertTrue(testSchema.getColumnType(columnName).isInt());
	}
	
	@Test
	public void mergeSchema() {
		Schema leftSchema = new Schema();
		Schema rightSchema = new Schema();
		
		leftSchema.addColumn("left", Type.getIntType());
		rightSchema.addColumn("right", Type.getIntType());
		
		Schema merged = Schema.mergeSchemas(leftSchema, rightSchema);
		assertTrue(merged.hasColumn("left"));
		assertTrue(merged.hasColumn("right"));
	}

}
