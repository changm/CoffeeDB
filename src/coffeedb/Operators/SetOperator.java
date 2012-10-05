package coffeedb.operators;

import java.util.List;

import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.types.Type;

public class SetOperator extends Operator {
	private Value[] _columns;
	private Value[] _values;
	
	public SetOperator(Value[] left, Value[] right) {
		assert (left.length == right.length);
		_columns = left;
		_values = right;
	}
	
	private void updateTuple(Tuple t) {
		for (int i = 0; i < _columns.length; i++) {
			Value column = _columns[i];
			assert (column.isSymbol());
			
			String columnName = column.toString();
			Value newValue = _values[i];
			t.setValue(columnName, newValue);
		}
	}
	
	private List<Tuple> getResultTuple(List<Tuple> result) {
		Schema schema = new Schema();
		schema.addColumn("Updated", Type.getStringType());
		
		String message = "Updated " + result.size() + " rows";
		Value[] values = Value.createValueArray(message);
		Tuple resultTuple = new Tuple(schema, values);
		return Tuple.createList(resultTuple);
	}

	public List<Tuple> getData() {
		List<Tuple> data = _child.getData();
		for (Tuple t : data) {
			updateTuple(t);
		}
		
		return getResultTuple(data);
	}
}
