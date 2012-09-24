package coffeedb.operators;

import java.util.List;

import coffeedb.Schema;
import coffeedb.Tuple;

/***
 * Our definition of operator changes drastically compared to system R
 * Instead of reading things tuple at a time, we throw the data to a function
 * and the function processes all the data at once. Thus we don't actually have
 * to keep calling next, we just say getData!
 * @author masonchang
 *
 */
public abstract class Operator {
	protected Operator _child;
	
	public Operator() {
		
	}
	
	public Operator(Operator child) {
		_child = child;
	}
	
	public Operator getChild() { 
		return _child; 
	}
	
	public abstract List<Tuple> getData(); 
}
