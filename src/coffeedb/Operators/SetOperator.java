package coffeedb.operators;

import java.util.List;

import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.types.Type;

public class SetOperator extends Operator {
	private String[] _columns;
	private Value[] _values;
	
	public SetOperator(String[] columns, Value[] values) {
		assert (columns.length == values.length);
		_columns = columns;
		_values = values;
	}
	
	private void updateTuple(Tuple t) {
		for (int i = 0; i < _columns.length; i++) {
			String column = _columns[i];
			Value newValue = _values[i];
			t.setValue(column, newValue);
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
