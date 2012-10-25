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
		if (_keys.isEmpty()) {
			return _children.isEmpty();
		}
		
		boolean childRatio = _keys.size() == _children.size() - 1;
		return childRatio && areKeysSorted();
	}
	
	private boolean areKeysSorted() {
		return true;
	}

	private boolean shouldSplit() {
		return _keys.size() > Btree.BRANCH_FACTOR;
	}
	
	private boolean shouldMerge() {
		return _keys.size() <= (Btree.BRANCH_FACTOR / 2);
	}
	
	protected void mergeNode() {
		assert false : "Haven't implemented merge node";
	}
	
	
	protected boolean isRoot() {
		return _parent == null;
	}
	
	protected int getSplitIndex() {
		return (int) Math.ceil((double)_keys.size() / 2.0);
	}
	
	// We actually always have key.size() == child.size() + 1
	// Because of the artificial left index
	// so a key that isn't the 0 key maps to a child of keyIndex + 1
	private int getChildIndex(int keyIndex) {
		return keyIndex + 1;
	}
	
	protected void splitNode(BtreeNode right) {
		int splitIndex = getSplitIndex();
		Value splitKey = _keys.get(splitIndex);
		right.setParent(_parent);
		
		transferValuesToRight(right, splitIndex);
		removeTransferedValues(splitIndex);
		addKeyToParent(right, splitKey);
	}

	private void addKeyToParent(BtreeNode right, Value splitKey) {
		if (!_parent.isEmptyNode()) {
			_parent.addKey(splitKey, right);
		}
	}

	private void removeTransferedValues(int splitIndex) {
		int keySize = _keys.size();
		for (int i = splitIndex; i < keySize; i++) {
			_keys.removeLast();
			_children.removeLast();
		}
	}

	private void transferValuesToRight(BtreeNode right, int splitIndex) {
		for (int i = splitIndex; i < _keys.size(); i++) {
			Value key = _keys.get(i);
			int childIndex = getChildIndex(i);
			BtreeNode child = _children.get(childIndex);
			right.addKey(key, child);
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
		BtreeNode left = this;
		
		left.setParent(newRoot);
		right.setParent(newRoot);
		splitNode(right);
		newRoot.addNodes(right.getKey(), left, right);
		
		assert (isConsistent());
		assert (right.isConsistent());
		assert (newRoot.isConsistent());
		return newRoot;
	}
	
	public void deleteKey(Value key) {
	}
	
	private int findKeyIndex(Value key) {
		if (_keys.isEmpty()) return 0;
		
		if (_keys.size() == 1) {
			Value firstKey = _keys.getFirst();
			if (key.lessThan(firstKey)) return 0;
		}
		
		for (int i = 0; i < _keys.size(); i++) {
			Value currentKey =  _keys.get(i);
			if (key.lessThan(currentKey)) return i;
		}
		
		return _keys.size();
	}
	
	public void addKey(Value key, BtreeNode child) {
		int index = findKeyIndex(key);
		if (_children.isEmpty()) {
			_children.add(child);
		} else {
			_keys.add(index, key);
			_children.add(getChildIndex(index), child);
		}
		
		if (shouldSplit()) {
			if (isRoot()) {
				splitRootNode();
			} else {
				splitNode(new BtreeNode());
			}
		}
	}
	
	public boolean isEmptyNode() {
		return (_keys.size() == 0) && (_children.size() == 0);
	}
	
	public void addNodes(Value key, BtreeNode left, BtreeNode right) {
		// We're a new node without any children
		// We should add both the left and right child
		assert (_keys.isEmpty());
		assert (_children.isEmpty());
		
		_keys.add(key);
		_children.add(left);
		_children.add(right);
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
		
		for (int i = 0; i < _children.size(); i++) {
			BtreeNode child = _children.get(i);
			Value childKey = child.getKey();
			
			if (key.lessThan(childKey)) {
				if (i == 0) {
					return child;
				} else {
					// Because we have to go left
					return _children.get(i - 1);
				}
			}
		}
		
		return _children.getLast();
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
	
	public LinkedList<Value> getKeys() {
		return _keys;
	}
}
