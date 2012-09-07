package coffeedb;

import java.util.ArrayList;
import coffeedb.operators.*;

public class QueryPlan {
	public ArrayList<Operator> _operators;
	private Transaction _transaction;
	
	public QueryPlan() {
		_operators = new ArrayList<Operator>();
	}
	
	public ArrayList<Operator> getOperators() {
		return _operators;
	}
	
	public void addOperator(Operator op) {
		_operators.add(0, op);
	}

	public ScanOperator addSelect(String tableName, ArrayList<String> columns) {
		ScanOperator scan = new ScanOperator(tableName);
		addOperator(scan);
		return scan;
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
	
	public FilterOperator addWhere(ScanOperator scan, Comparison compare) {
		FilterOperator filter = new FilterOperator(compare, scan);
		addOperator(filter);
		return filter;
	}

	public Transaction getTransaction() {
		return _transaction;
	}

	public void setTransaction(Transaction transaction) {
		this._transaction = transaction;
	}
}
