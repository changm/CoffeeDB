package coffeedb.operators;

import java.util.List;

import coffeedb.CoffeeDB;
import coffeedb.Table;
import coffeedb.Tuple;

public class DeleteOperator extends Operator {
	private Table _table;
	public DeleteOperator(Operator child, Table table) {
		super(child);
		_table = table;
	}
	
	private List<Tuple> createResult(int rowCount) {
		String message = "Deleted " + rowCount + " rows";
		Tuple result = new Tuple(message);
		return Tuple.createList(result);
	}

	public List<Tuple> getData() {
		List<Tuple> data = _child.getData();
		int deleteCount = data.size();
		
		List<Tuple> tableData = _table.getData();
		for (Tuple delete : data) {
			tableData.remove(delete);
		}
		
		return createResult(deleteCount);
	}

}
