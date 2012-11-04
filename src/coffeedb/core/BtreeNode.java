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
public abstract class BtreeNode {
	private BtreeInternalNode _parent;
	public abstract boolean needsSplit();
	public abstract boolean needsMerge();
	public abstract BtreeNode getLeftSibling();
	public abstract BtreeNode getRightSibling();
	public abstract Value getKey();
	public abstract boolean isConsistent();
	public abstract boolean isEmpty();
	public abstract void clean();
	public abstract LinkedList<Value> getKeys();
	
	public boolean isLeaf() {
		return false;
	}
	
	public BtreeInternalNode getParent() {
		return _parent;
	}
	public void setParent(BtreeInternalNode parent) {
		this._parent = parent;
	}
}