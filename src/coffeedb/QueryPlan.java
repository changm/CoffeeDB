package coffeedb;

import java.util.ArrayList;

import java.util.List;

import net.sf.jsqlparser.statement.select.SelectItem;

import coffeedb.operators.*;
import coffeedb.values.Value;

public class QueryPlan {
	public ArrayList<Operator> _operators;
	
	public QueryPlan() {
		_operators = new ArrayList<Operator>();
	}
	
	public ArrayList<Operator> getOperators() {
		assert (_operators.size() == 1);
		return _operators;
	}
	
	private void addOperator(Operator op) {
		_operators.add(op);
	}

	public void addSelect(String tableName, ArrayList<String> columns) {
		ScanOperator scan = new ScanOperator(tableName);
		addOperator(scan);
		
		//FilterOperator filter = new FilterOperator(scan);
	}
	
	public void addCreate(String tableName, Schema tableSchema) {
		CreateOperator createOp = new CreateOperator(tableName, tableSchema);
		addOperator(createOp);
	}

	public void createInsertOperator(String tableName, ArrayList<Value> _values) {
		Tuple tuple = new Tuple(_values);
		InsertOperator insert = new InsertOperator(tableName, tuple);
		addOperator(insert);
	}
}
