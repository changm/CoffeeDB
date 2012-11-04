package coffeedb.core;

import java.util.LinkedList;
import java.util.List;

import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.operators.Predicate;

public class BtreeLeafNode extends BtreeNode {
	private LinkedList<Value> _keys;
	private LinkedList<Tuple> _tuples;
	private int _branchFactor;
	private static int KEY_INDEX = 0;
	private BtreeLeafNode _next;
	
	public BtreeLeafNode (BtreeInternalNode parent, int branchFactor) {
		setParent(parent);
		_keys = new LinkedList<Value>();
		_tuples = new LinkedList<Tuple>();
		_branchFactor = branchFactor;
	}
	
	private int findNewSlot(Value key) {
		if (isEmpty()) return 0;
		if (key.lessThan(_keys.getFirst())) return 0;
		
		for (int i = 1; i < _keys.size(); i++) {
			Value slotKey = _keys.get(i);
			if (key.lessThan(slotKey)) {
				return i; 
			}
		}
		
		return _keys.size();
	}
	
	private int findKeySlot(Value key) {
		for (int i = 0; i < _keys.size(); i++) {
			Value slotKey = _keys.get(i);
			if (slotKey.equals(key)) return i;
		}
		
		assert false : "Unknown key in leaf node " + key;
		return -1;
	}
	
	public void addKey(Value key, Tuple tuple) {
		int slot = findNewSlot(key);
		_keys.add(slot, key);
		_tuples.add(slot, tuple);
		assert (isConsistent());
	}
	
	public void deleteKey(Value key) {
		int slot = findKeySlot(key);
		_keys.remove(slot);
		_tuples.remove(slot);
		assert (isConsistent());
	}
	
	public Tuple getTuple(Value key) {
		int slot = findKeySlot(key);
		return _tuples.get(slot);
	}
	
	@Override
	public boolean needsSplit() {
		return _keys.size() > _branchFactor;
	}

	@Override
	public boolean needsMerge() {
		int half = (int) Math.ceil(_branchFactor / 2.0);
		return _keys.size() < half;
	}

	@Override
	public BtreeNode getLeftSibling() {
		return getParent().getLeftSibling(this);
	}

	@Override
	public BtreeNode getRightSibling() {
		return getParent().getRightSibling(this);
	}

	@Override
	public Value getKey() {
		return _keys.get(KEY_INDEX);
	}
	
	private boolean isSorted() {
		if (isEmpty()) return true;
		Value key = _keys.getFirst();
		for (int i = 1; i < _keys.size(); i++) {
			Value currentKey = _keys.get(i);
			if (currentKey.lessThan(key)) return false;
			key = currentKey;
		}
		
		return true;
	}

	@Override
	public boolean isConsistent() {
		return (_keys.size() == _tuples.size()) && isSorted();
	}

	@Override
	public boolean isEmpty() {
		if (_keys.isEmpty()) {
			return _tuples.isEmpty();
		}
		
		return false;
	}

	public BtreeLeafNode getNext() {
		return _next;
	}

	public void setNext(BtreeLeafNode next) {
		this._next = next;
	}
	
	public boolean isLeaf() {
		return true;
	}
	
	public LinkedList<Value> getKeys() {
		return _keys;
	}
	
	public LinkedList<Tuple> getTuples() {
		return _tuples;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Tuple tuple : _tuples) {
			buffer.append(tuple);
			buffer.append(",");
		}
		
		return buffer.toString();
	}
	
	public void clean() {
		_keys.clear();
		_tuples.clear();
	}
	
	public Value getTupleKey(Tuple tuple) {
		return tuple.getValue(KEY_INDEX);
	}
}