package coffeedb.test.core;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

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

}
