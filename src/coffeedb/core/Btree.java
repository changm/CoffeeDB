package coffeedb.core;

import java.util.LinkedList;
import java.util.Queue;

import coffeedb.Tuple;
import coffeedb.Value;

public class Btree {
	private BtreeNode _root;
	private int _branchFactor;
	
	public Btree(int branchFactor) {
		_branchFactor = branchFactor;
		_root = new BtreeLeafNode(null, _branchFactor);
	}
	
	public BtreeNode findBucket(BtreeNode node, Value key) {
		if (node.isLeaf()) {
			return node;
		}
		
		BtreeInternalNode internalNode = (BtreeInternalNode) node;
		return internalNode.getChild(key);
	}
	
	public BtreeLeafNode findLeaf(Value key) {
		return (BtreeLeafNode) findBucket(_root, key);
	}
	
	private boolean isRoot(BtreeNode node) {
		return _root == node;
	}
	
	private void setRoot(BtreeNode node) {
		assert (node != null);
		assert (node.isConsistent());
		_root = node;
	}
	
	public Tuple getTuple(Value key) {
		BtreeLeafNode leaf = (BtreeLeafNode) findBucket(_root, key);
		return leaf.getTuple(key);
	}
	
	public void addKey(Value key, Tuple tuple) {
		BtreeLeafNode leaf = (BtreeLeafNode) findBucket(_root, key);
		leaf.addKey(key, tuple);
		
		if (leaf.needsSplit()) {
			if (isRoot(leaf)) {
				BtreeInternalNode newRoot = new BtreeInternalNode(null, _branchFactor);
				leaf.setParent(newRoot);
				setRoot(newRoot);
				newRoot.addKey(leaf.getKey(), leaf);
			}
			
			splitNode(leaf);
		}
	}
	
	public boolean isEmpty() {
		return _root.isEmpty();
	}
	
	public void deleteKey(Value key) {
		BtreeLeafNode leaf = (BtreeLeafNode) findBucket(_root, key);
		leaf.deleteKey(key);
		
		if (!isRoot(leaf) && leaf.needsMerge()) {
			mergeNode(leaf);
		}
	}
	
	/***
	 * Begin insertion methods
	 */
	private void splitNode(BtreeInternalNode node) {
		BtreeInternalNode parent = node.getParent();
		Value oldNodeKey = node.getKey();
		BtreeInternalNode newNode = new BtreeInternalNode(parent, _branchFactor);
		distributeChildren(node, newNode);
		Value newNodeKey = newNode.getKey();
		
		parent.addKey(newNodeKey, newNode);
		parent.updateKey(oldNodeKey, node.getKey());
		
		if (parent.needsSplit()) {
			splitNode(parent);
		}
	}
	
	private void distributeChildren(BtreeInternalNode node,
			BtreeInternalNode newNode) {
		assert false : "Not yet implemented";
	}

	private void splitNode(BtreeLeafNode leaf) {
		BtreeLeafNode newLeaf = new BtreeLeafNode(leaf.getParent(), _branchFactor);
		newLeaf.setNext(leaf.getNext());
		leaf.setNext(newLeaf);
		
		Value leafKey = leaf.getKey();
		distributeTuples(leaf, newLeaf);
		Value newLeafKey = newLeaf.getKey();
		
		assert (leaf.getParent() == newLeaf.getParent());
		BtreeInternalNode parent = leaf.getParent();
		
		assert (leafKey.equals(leaf.getKey()));
		parent.updateKey(leafKey, leaf.getKey());
		parent.addKey(newLeafKey, newLeaf);
		
		if (parent.needsSplit()) {
			splitNode(parent);
		}
	}
	
	private void distributeTuples(BtreeLeafNode oldLeaf, BtreeLeafNode newLeaf) {
		LinkedList<Value> keys = oldLeaf.getKeys();
		LinkedList<Tuple> tuples = oldLeaf.getTuples();
		int splitIndex = getSplitIndex();
		int keyCount = keys.size();
		
		for (int i = splitIndex; i < keyCount; i++) {
			Value key = keys.get(i);
			Tuple tuple = tuples.get(i);
			newLeaf.addKey(key, tuple);
		}
		
		for (int i = splitIndex; i < keyCount; i++) {
			// Since we're actually modifying this list
			// We have to do a get last
			Value key = keys.getLast();
			oldLeaf.deleteKey(key);
		}
	}
	
	private int getSplitIndex() {
		return (int) Math.ceil(_branchFactor / 2.0);
	}

	/***
	 * Begin deletion methods
	 */
	private void mergeNode(BtreeInternalNode node) {
		BtreeInternalNode left = (BtreeInternalNode) node.getLeftSibling();
		BtreeInternalNode right = (BtreeInternalNode) node.getRightSibling();
		BtreeInternalNode parent = node.getParent();
		
		assert (left.getParent() == parent);
		assert (right.getParent() == parent);
				
		if (isRoot(node)) {
			assert (left == right);
			assert (node == left);
			forceRootToLeaf(node);
		} else {
			consolidateChildren(left, node, right);
			parent.deleteChild(node);
			
			if (parent.needsMerge()) {
				mergeNode(parent);
			}
		}
	}
	
	private void consolidateChildren(BtreeInternalNode left,
			BtreeInternalNode middle, BtreeInternalNode right) {
		assert (middle.needsMerge());
		if (isRoot(left)) {
			assert (left == middle);
			assert (middle == right);
		} else if (left == middle) {
			mergeInto(middle, right);
		} else if (middle == right) {
			mergeInto(middle, left);
		} else {
			consolidateAllChildren(left, middle, right);
		}
		
		assert (left.isConsistent());
		assert (middle.isConsistent());
		assert (right.isConsistent());
	}
	
	private void forceRootToLeaf(BtreeInternalNode middle) {
		assert (isRoot(middle));
		assert (_root.getKeys().isEmpty());
		
		BtreeLeafNode newRoot = new BtreeLeafNode(null, _branchFactor);
		int insertCount = 0;
		for (BtreeNode child : middle.getChildren()) {
			assert (child instanceof BtreeLeafNode);
			BtreeLeafNode childLeaf = (BtreeLeafNode) child;
			for (Tuple tuple : childLeaf.getTuples()) {
				Value key = childLeaf.getTupleKey(tuple);
				newRoot.addKey(key, tuple);
				insertCount++;
			}
		}
		
		assert (insertCount <= _branchFactor);
		setRoot(newRoot);
		middle.clean();
		assert (newRoot.isConsistent());
	}

	private LinkedList<Value> getAllKeys(BtreeNode left, BtreeNode middle, BtreeNode right) {
		LinkedList<Value> keys = new LinkedList<Value>();
		keys.addAll(left.getKeys());
		keys.addAll(middle.getKeys());
		keys.addAll(right.getKeys());
		return keys;
	}

	private void consolidateAllChildren(BtreeInternalNode left,
			BtreeInternalNode middle, BtreeInternalNode right) {
		LinkedList<Value> keys = getAllKeys(left, middle, right);
		LinkedList<BtreeNode> children = new LinkedList<BtreeNode>();
		children.addAll(left.getChildren());
		children.addAll(middle.getChildren());
		children.addAll(right.getChildren());
		
		left.clean();
		middle.clean();
		right.clean();
		assert false : "Not yet implemented";
	}

	private void mergeInto(BtreeInternalNode source, BtreeInternalNode destination) {
		LinkedList<Value> keys = source.getKeys();
		LinkedList<BtreeNode> children = source.getChildren();
		
		for (int i = 0; i < keys.size(); i++) {
			Value key = keys.get(i);
			BtreeNode child = children.get(i + 1);
			if (i == 0) {
				BtreeNode leftChild = children.get(i);
				destination.addKey(key, leftChild);
			}
			
			destination.addKey(key, child);
		}
		
		source.clean();
	}

	private void mergeNode(BtreeLeafNode node) {
		BtreeLeafNode left = (BtreeLeafNode) node.getLeftSibling();
		BtreeLeafNode right = (BtreeLeafNode) node.getRightSibling();
		consolidateTuples(left, node, right);
		
		BtreeInternalNode parent = node.getParent();
		parent.deleteChild(node); 
		
		if (parent.needsMerge()) {
			mergeNode(parent);
		}
	}

	private void consolidateTuples(BtreeLeafNode left, BtreeLeafNode middle,
			BtreeLeafNode right) {
		assert (middle.needsMerge());
		
		if (left == middle) {
			mergeInto(middle, right);
		} else if (middle == right) {
			mergeInto(middle, left);
		} else {
			consolidateAllLeaves(left, middle, right);
		}
	}
	
	private void mergeInto(BtreeLeafNode source, BtreeLeafNode destination) {
		LinkedList<Value> keys = source.getKeys();
		LinkedList<Tuple> tuples = source.getTuples();
		assert (keys.size() == tuples.size());
		
		for (int i = 0; i < keys.size(); i++) {
			Value key = keys.get(i);
			Tuple tuple = tuples.get(i);
			destination.addKey(key, tuple);
		}
		
		source.clean();
		assert (destination.isConsistent());
	}

	private void consolidateAllLeaves(BtreeLeafNode left, BtreeLeafNode middle,
			BtreeLeafNode right) {
		LinkedList<Value> keys = getAllKeys(left, middle, right);
		LinkedList<Tuple> tuples = new LinkedList<Tuple>();
		
		tuples.addAll(left.getTuples());
		tuples.addAll(middle.getTuples());
		tuples.addAll(right.getTuples());
		
		assert (keys.size() == tuples.size());
		assert (tuples.size() <= (_branchFactor * 2));
		int tupleSplit = tuples.size() / 2;
		
		left.clean();
		middle.clean();
		right.clean();
		
		for (int i = 0; i < tupleSplit; i++) {
			Tuple tuple = tuples.get(i);
			Value key = keys.get(i);
			left.addKey(key, tuple);
		}
		
		for (int i = tupleSplit; i < tuples.size(); i++) {
			Tuple tuple = tuples.get(i);
			Value key = keys.get(i);
			right.addKey(key, tuple);
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		LinkedList<BtreeNode> first = new LinkedList<BtreeNode>();
		LinkedList<BtreeNode> second = new LinkedList<BtreeNode>();
		first.push(_root);
		
		while (!first.isEmpty()) {
			BtreeNode node = first.removeFirst();
			buffer.append(node.toString());
			
			if (!node.isLeaf()) {
				BtreeInternalNode internalNode = (BtreeInternalNode) node;
				second.addAll(internalNode.getChildren());
			}
			
			if (first.isEmpty()) {
				first.addAll(second);
				second.clear();
				buffer.append("\n");
			}
		}
		
		return buffer.toString();
	}
	
	public int getBranchFactor() {
		return _branchFactor;
	}
	
	public BtreeNode getRoot() {
		assert (_root != null);
		return _root;
	}
	
	public int getNumberOfNodes() {
		LinkedList<BtreeNode> queue = new LinkedList<BtreeNode>();
		queue.add(_root);
		int count = 0;
		
		while (!queue.isEmpty()) {
			BtreeNode node = queue.removeFirst();
			count++;
			
			if (node.isLeaf()) continue;
			
			BtreeInternalNode internalNode = (BtreeInternalNode) node;
			for (BtreeNode child : internalNode.getChildren()) {
				queue.add(child);
			}
		}
		
		return count;
	}
}