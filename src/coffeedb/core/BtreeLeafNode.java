package coffeedb.core;

import java.util.LinkedList;
import java.util.List;

import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.operators.Predicate;

public class BtreeLeafNode extends BtreeNode {
	private LinkedList<Tuple> _pointers;
	private BtreeLeafNode _next;
		
	public BtreeLeafNode() {
		_pointers = new LinkedList<Tuple>();
	}
	
	public boolean isConsistent() {
		boolean constraints = _pointers.size() == _keys.size();
		constraints = constraints && (_pointers.size() <= Btree.BRANCH_FACTOR);
		return constraints && pointersSorted() && keysMatchTuples();
	}
	
	public boolean keysMatchTuples() {
		for (int i = 0; i < _keys.size(); i++) {
			Value key = _keys.get(i);
			Tuple tuple = _pointers.get(i);
			if (!key.equals(tuple.getValue(0))) return false;
		}
		
		return true;
	}
	
	public boolean pointersSorted() {
		if (_keys.isEmpty()) return true;
		Value prev = _keys.get(0);
		
		for (int i = 1; i < _pointers.size(); i++) {
			Value key = _keys.get(i);
			if (key.lessThan(prev)) return false;
			prev = key;
		}
		
		return true;
	}

	private boolean shouldSplit() {
		return _keys.size() > Btree.BRANCH_FACTOR;
	}
	
	private boolean shouldMerge() {
		assert (isConsistent());
		return _keys.size() < getSplitIndex();
	}
	
	
	protected void splitNode(BtreeLeafNode right) {
		int split = getSplitIndex();
		right.setParent(_parent);
		
		addLinkToNextLeaf(right);
		transferValuesToRightNode(right, split);
		removeTransferedValues(split);
		addKeyToParent(right);
	}

	private void addKeyToParent(BtreeLeafNode right) {
		if (!_parent.isEmptyNode()) {
			_parent.addKey(right.getKey(), right);
		}
	}

	private void removeTransferedValues(int split) {
		while (_pointers.size() > split) {
			_pointers.removeLast();
			_keys.removeLast();
		}
	}

	private void transferValuesToRightNode(BtreeLeafNode right, int split) {
		for (int i = split; i < _pointers.size(); i++) {
			Value key = _keys.get(i);
			Tuple pointer = _pointers.get(i);
			right.addKey(key, pointer);
		}
	}
	
	private void addLinkToNextLeaf(BtreeLeafNode next) {
		this._next = next;
	}
	
	protected BtreeNode splitRootNode() {
		assert (isRoot());
		BtreeNode root =  new BtreeNode();
		BtreeLeafNode right = new BtreeLeafNode();
		this.setParent(root);
		
		splitNode(right);
		root.addNodes(right.getKey(), this, right);
		return root;
	}

	protected void mergeNode() {
		assert false : "Haven't implemented split node";
	}
	
	private int getInsertIndex(Value key) {
		assert (isConsistent());
		if (_keys.isEmpty()) return 0;
		
		Value first = _keys.get(0);
		if (key.compare(Predicate.LESS, first)) {
			return 0;
		}
		
		for (int i = 0; i < _pointers.size(); i++) {
			Value compare = _keys.get(i);
			if (key.compare(Predicate.LESS, compare)) {
				return i;
			}
		}
		
		int nextIndex = _pointers.size();
		Value greatest = _keys.get(nextIndex - 1);
		assert (key.compare(Predicate.GREATER, greatest));
		return nextIndex;
	}
	
	private int findIndex(Value key) {
		return _keys.indexOf(key);
	}
	
	public void addKey(Value key, Tuple tuple) {
		int index = getInsertIndex(key);
		_keys.add(index, key);
		_pointers.add(index, tuple);
		
		/***
		 * We don't split until we already add the key. 
		 * Simplifies the logic when we split so that we don't
		 * have to find which leaf node we should have inserted the new value
		 * into
		 */
		if (shouldSplit()) {
			if (isRoot()) {
				splitRootNode();
			} else {
				splitNode(new BtreeLeafNode());
			}
		}
	}
	
	public Tuple getTuple(Value key) {
		int index = findIndex(key);
		return _pointers.get(index);
	}
	
	public void deleteKey(Value key) {
		assert (isConsistent());
		assert (_keys.contains(key));
		
		int index = _keys.indexOf(key);
		_keys.remove(index);
		_pointers.remove(index);
		
		if (shouldMerge()) {
			mergeNode();
		}
	}
	
	public boolean isLeaf() {
		return true;
	}
	
	public String toString() {
		assert (isConsistent());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < _keys.size(); i++) {
			Value key = _keys.get(i);
			Tuple tuple = _pointers.get(i);
			sb.append(key.toString() + " = [" + tuple.toString() + "] ");
		}
		
		sb.append(" | ");
		return sb.toString();
	}
	
	public LinkedList<Tuple> getTuples() {
		return _pointers;
	}
}
