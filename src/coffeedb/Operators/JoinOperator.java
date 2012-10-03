package coffeedb.operators;

import java.util.ArrayList;
import java.util.List;

import coffeedb.Schema;
import coffeedb.Tuple;

public class JoinOperator extends Operator {
	private Operator _left;
	private Operator _right;
	
	public JoinOperator(Operator left, Operator right) {
		_left = left;
		_right = right;
	}
	
	private List<Tuple> nestedLoopJoin(List<Tuple> leftData,
			List<Tuple> rightData) {
		List<Tuple> results = new ArrayList<Tuple>();
		for (Tuple left : leftData) {
			for (Tuple right : rightData) {
				Tuple merged = Tuple.merge(left, right);
				results.add(merged);
			}
		}
		
		return results;
	}

	
	public List<Tuple> getData() {
		List<Tuple> leftData = _left.getData();
		List<Tuple> rightData = _right.getData();
		
		return nestedLoopJoin(leftData, rightData);
	}
}
