package coffeedb.operators;

import java.util.ArrayList;
import java.util.List;

import coffeedb.CoffeeDB;
import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.types.Type;

/***
 * Implements the relational algebra projection operator
 * @author masonchang
 *
 */
public class Projection extends Operator {
	private String[] _columns;
	private Schema _resultSchema;
	private boolean _all;
	
	public Projection(Operator child, String[] columns) {
		_child = child;
		_columns = columns;
	}
	
	public Projection() {
		_all = true;
	}

	private Schema createResultSchema(Tuple result) {
		Schema schema = new Schema();
		Schema tupleSchema = result.getSchema();
		
		for (String column : _columns) {
			Type columnType = tupleSchema.getColumnType(column);
			assert (columnType != null);
			schema.addColumn(column, columnType);
		}
		
		assert (schema.columnCount() == _columns.length);
		return schema;
	}

	private Tuple extractColumns(Tuple next) {
		Tuple tuple = new Tuple(getSchema(next));
		for (int i = 0; i < _columns.length; i++) {
			String column =  _columns[i];
			Value value = next.getValue(column);
			tuple.setValue(i, value);
		}
		
		return tuple;
	}

	protected Schema getSchema(Tuple result) {
		if (_resultSchema == null) {
			_resultSchema = createResultSchema(result);
		}
		
		return _resultSchema;
	}

	@Override
	public List<Tuple> getData() {
		assert (_child != null);
		List<Tuple> data = _child.getData();
		if (_all) return data;
		
		List<Tuple> results = new ArrayList<Tuple>(data.size());
		for (Tuple t : data) {
			results.add(extractColumns(t));
		}
		
		return results;
	}
}
