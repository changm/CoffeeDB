package coffeedb.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import coffeedb.Tuple;
import coffeedb.Value;

/***
 * Implementation of a BTree
 * @author masonchang
 */
public class BtreeNode {
	public static int BRANCH_FACTOR = 3;
	private static int KEY_INDEX = 0;
	
	/***
	 * Btree internal nodes have a slightly different
	 * semantic compared to leaf nodes. The number of children
	 * is always _keys.size() + 1 because there is an implicit
	 * node value that is less than the first key's value.
	 * When you split a node, there is only 1 key but 2 children.
	 * Thus we split when the number of children get too big, 
	 * not when the number of keys.
	 */
	protected BtreeNode _parent;
	private LinkedList<BtreeNode> _children;
	protected LinkedList<Value> _keys;
	
	public BtreeNode() {
		_children = new LinkedList<BtreeNode>();
		_keys = new LinkedList<Value>();
	}
	
	private boolean isConsistent() {
		return _keys.size() == _children.size();
	}
	
	private boolean shouldSplit() {
		return _children.size() >= BRANCH_FACTOR;
	}
	
	private boolean shouldMerge() {
		return _children.size() <= (BRANCH_FACTOR / 2);
	}
	
	protected void mergeNode() {
		assert false : "Haven't implemented merge node";
	}
	
	
	protected boolean isRoot() {
		return _parent == null;
	}
	
	protected int getSplitIndex() {
		return _keys.size() / 2;
	}
	
	protected void splitNode(BtreeNode right) {
		int splitIndex = getSplitIndex();
		Value splitKey = _keys.get(splitIndex);
		_parent.addNode(splitKey, this, right);
		right.setParent(_parent);
		
		for (int i = splitIndex; i < _children.size(); i++) {
			Value key = _keys.get(i);
			BtreeNode child = _children.get(i);
			right.addKey(key, child);
		}
		
		for (int i = 0; i < _children.size(); i++) {
			_keys.removeLast();
			_children.removeLast();
		}
	}
	
	/***
	 * B+Trees are always split at the root.
	 * @return
	 */
	protected BtreeNode splitRootNode() {
		assert (isRoot());
		BtreeNode newRoot = new BtreeNode();
		BtreeNode right = new BtreeNode();
		
		this.setParent(newRoot);
		right.setParent(newRoot);
		
		splitNode(right);
		return newRoot;
	}
	
	public void deleteKey(Value key) {
	}
	
	public void addKey(Value key, BtreeNode child) {
		_keys.add(key);
		_children.add(child);
		
		if (shouldSplit()) {
			if (isRoot()) {
				splitRootNode();
			} else {
				splitNode(new BtreeNode());
			}
		}
	}
	
	public void addNode(Value key, BtreeNode left, BtreeNode right) {
		// If we're a new node, we have to start with a left/right children.
		// But we actually only have one key. If an incoming value is less than the key, we go left
		if (_children.isEmpty()) {
			_children.add(left);
		}
		
		_children.add(right);
		_keys.add(key);
	}
	
	public void searchKey(Value key) {
	}
	
	public void setParent(BtreeNode parent) {
		assert (parent != null);
		_parent = parent;
	}
	
	public boolean isLeaf() {
		return false;
	}

	public BtreeNode getChild(Value key) {
		assert (!isLeaf());
		assert (isConsistent());
		
		for (BtreeNode child : _children) {
			if (child.getKey().equals(key)) return child;
		}
		
		assert false : key.toString() + " is not in btreenode";
		return null;
	}
	
	/***
	 * We choose the left most key to represent the values in
	 * the btree. We assume that the keys and pointers are in sorted order
	 * based on the keys
	 * @return
	 */
	public Value getKey() {
		return _keys.get(KEY_INDEX);
	}

	public BtreeNode getParent() {
		return _parent;
	}

	public List<BtreeNode> getChildren() {
		return _children;
	}
	
	public String toString() {
		StringBuffer keys = new StringBuffer();
		for (Value key : _keys) {
			keys.append(key.toString() + ", ");
		}
		
		keys.append(" | ");
		return keys.toString();
	}
}
