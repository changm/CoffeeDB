package coffeedb.core;

import java.util.LinkedList;
import java.util.Queue;

import coffeedb.Tuple;
import coffeedb.Value;

public class Btree {
	private BtreeNode _root;
	public static int BRANCH_FACTOR = 3;
	
	public Btree() {
		_root = new BtreeLeafNode();
	}
	
	private BtreeLeafNode findBucket(BtreeNode current, Value key) {
		if (current.isLeaf()) {
			return (BtreeLeafNode) current;
		}
		
		return findBucket(current.getChild(key), key);
	}
	
	public void addKey(Value key, Tuple value) {
		BtreeLeafNode leafNode = findBucket(_root, key);
		assert (leafNode.isLeaf());
		leafNode.addKey(key, value);
		
		if (!_root.isRoot()) {
			_root = _root.getParent();
			assert (_root.isRoot());
		}
	}
	
	private String printBFS() {
		StringBuffer buffer = new StringBuffer();
		LinkedList<BtreeNode> level = new LinkedList<BtreeNode>();
		LinkedList<BtreeNode> alt = new LinkedList<BtreeNode>();
		level.add(_root);
		
		while (!level.isEmpty()) {
			BtreeNode node = level.removeFirst();
			buffer.append(node.toString());
			
			alt.addAll(node.getChildren());
			
			if (level.isEmpty()) {
				buffer.append("\n");
				level.addAll(alt); 
				alt.clear();
			}
		}
		
		return buffer.toString();
	}
	
	public BtreeLeafNode findLeaf(Value key) {
		return findBucket(_root, key);
	}
	
	public String toString() {
		return printBFS();
	}
	
	public int getTreeSize() {
		int count = 0;
		LinkedList<BtreeNode> queue = new LinkedList<BtreeNode>();
		queue.add(_root);
		
		while (!queue.isEmpty()) {
			count++;
			BtreeNode node = queue.removeFirst();
			queue.addAll(node.getChildren());
		}
		
		return count;
	}
	
	public BtreeNode getRoot() {
		return _root;
	}
}
