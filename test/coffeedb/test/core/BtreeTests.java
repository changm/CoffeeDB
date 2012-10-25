package coffeedb.test.core;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.core.Btree;
import coffeedb.core.BtreeLeafNode;
import coffeedb.core.BtreeNode;

public class BtreeTests {
	private Btree _instance;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Before
	public void setUp() throws Exception {
		_instance = new Btree();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private boolean leavesHaveTuplesInOrder(List<Tuple> tuples) {
		assert (false);
		return false;
	}
	
	private void tuplesExist(List<Tuple> tuples) {
		for (Tuple t : tuples) {
			Value key = t.getValue(0);
			BtreeLeafNode bucket = _instance.findLeaf(key);
			assertTrue(bucket.containsTuple(t));
		}
	}

	@Test
	public void insertTest() {
		Tuple tuple = Tuple.createTupleAndSchema(3, "key", 10, "second");
		Value key = tuple.getValue(0);
		_instance.addKey(key, tuple);
		
		BtreeLeafNode leaf =  _instance.findLeaf(key);
		LinkedList<Value> keys = leaf.getKeys();
		assertTrue(keys.contains(key));
		assertTrue(leaf.getTuples().contains(tuple));
	}
	
	@Test
	public void splitLeafTest() {
		HashMap<Value, Tuple> insertions = new HashMap<Value, Tuple>();
		// Force one split
		for (int i = 0; i <= Btree.BRANCH_FACTOR; i++) {
			Tuple tuple = Tuple.createTupleAndSchema(i, "key");
			Value key = tuple.getValue(0);
			_instance.addKey(key, tuple);
			insertions.put(key,  tuple);
		}
		
		BtreeNode root = _instance.getRoot();
		assertTrue(_instance.getTreeSize() == 3);
		assertTrue(root.getChildren().size() == 2);
		
		for (BtreeNode child : root.getChildren()) {
			BtreeLeafNode leaf = (BtreeLeafNode) child;
			
			for (Value leafKey : child.getKeys()) {
				Tuple tuple = leaf.getTuple(leafKey);
				assertTrue(insertions.containsKey(leafKey));
				assertTrue(insertions.get(leafKey).equals(tuple));
			}
						
		} // end outer for
	}
	
	private List<Tuple> createTuples(int[] records) {
		LinkedList<Tuple> tuples = new LinkedList<Tuple>();
		for (int i = 0; i < records.length; i++) {
			int intColumn = records[i];
			Tuple tuple = Tuple.createTupleAndSchema(intColumn, "test" + intColumn);
			tuples.add(tuple);
		}
		
		return tuples;
	}
	
	private void insertTuples(List<Tuple> tuples) {
		for (Tuple t : tuples) {
			Value key = t.getValue(0);
			_instance.addKey(key, t);
		}
	}
	
	private List<Tuple> insertTuples(int[] keys) {
		List<Tuple> tuples = createTuples(keys);
		insertTuples(tuples);
		return tuples;
	}
	
	@Test
	public void variedValueTest() {
		int records[] = {44, 53, 86, 4, 53, 23};
		List<Tuple> insertedTuples = insertTuples(records);
		tuplesExist(insertedTuples);
	}
	
	@Test
	public void duplicateInsertTest() {
		int records[] = {39, 39};
		List<Tuple> insertedTuples = insertTuples(records);
		tuplesExist(insertedTuples);
	}

}
