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
import coffeedb.core.BtreeInternalNode;
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
		_instance = new Btree(3);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private void leavesHaveTuplesInOrder(List<Tuple> tuples) {
		assert (!tuples.isEmpty());
		Tuple first = tuples.get(0);
		Value firstKey = first.getValue(0);
		
		BtreeLeafNode bucket = _instance.findLeaf(firstKey);
		for (int i = 0; i < tuples.size(); ) {
			List<Tuple> bucketTuples = bucket.getTuples();
			
			for (int j = 0; j < bucketTuples.size(); j++) {
				Tuple bucketTuple = bucketTuples.get(j);
				Tuple listTuple = tuples.get(i);
				assertTrue(bucketTuple.equals(listTuple));
				i++;
			}
			
			if (bucket.hasNext()) {
				bucket = bucket.getNext();
			}
		}
	}
	
	private boolean tuplesExist(List<Tuple> tuples) {
		for (Tuple t : tuples) {
			Value key = t.getValue(0);
			BtreeLeafNode bucket = _instance.findLeaf(key);
			if (!bucket.containsTuple(t)) return false;
		}
		
		return true;
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
		for (int i = 0; i <= _instance.getBranchFactor(); i++) {
			Tuple tuple = Tuple.createTupleAndSchema(i, "key");
			Value key = tuple.getValue(0);
			_instance.addKey(key, tuple);
			insertions.put(key,  tuple);
		}
		
		BtreeInternalNode root = (BtreeInternalNode) _instance.getRoot();
		assertTrue(_instance.getNumberOfNodes() == 3); 
		
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
	
	private void deleteTuples(List<Tuple> tuples) {
		for (Tuple tuple : tuples) {
			Value key = tuple.getValue(0);
			_instance.deleteKey(key);
		}
	}
	
	@Test
	public void variedValueTest() {
		int records[] = {44, 53, 86, 4, 53, 23};
		int sortedRecords[] = {4, 23, 44, 53, 53, 86};
		
		List<Tuple> insertedTuples = insertTuples(records);
		assertTrue(tuplesExist(insertedTuples));
		
		List<Tuple> sortedTuples = createTuples(sortedRecords);
		leavesHaveTuplesInOrder(sortedTuples);
	}
	
	@Test
	public void duplicateInsertTest() {
		int records[] = {39, 39};
		List<Tuple> insertedTuples = insertTuples(records);
		assertTrue(tuplesExist(insertedTuples));
	}
	
	@Test
	public void testSingleDelete() {
		int records[] = {44};
		List<Tuple> insertedTuples = insertTuples(records);
		assertTrue(tuplesExist(insertedTuples));
		
		deleteTuples(insertedTuples);
		assertFalse(tuplesExist(insertedTuples));
	}
	
	@Test
	public void deleteAllTuples() {
		int records[] = { 1, 4, 47, 49 };
		List<Tuple> insertedTuples = insertTuples(records);
		assertTrue(tuplesExist(insertedTuples));
		
		deleteTuples(insertedTuples);
		assertFalse(tuplesExist(insertedTuples));
		
		int numberOfNodes = _instance.getNumberOfNodes();
		assertTrue(numberOfNodes == 1);
	}
}
