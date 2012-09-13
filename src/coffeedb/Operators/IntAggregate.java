package coffeedb.operators;

import java.util.HashMap;

import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.types.Type;

public class IntAggregate extends Aggregator {
	private HashMap<Integer, Integer> _values;
	private int _aggregateColumn;
	private int _groupColumn;
	private AggregateOp _op;
	
	public IntAggregate(int aggregateColumn, int groupByColumn, AggregateOp op) {
		_values = new HashMap<Integer, Integer>();
		_groupColumn = groupByColumn;
		_aggregateColumn = aggregateColumn;
		_op = op;
		
		_values.put(_aggregateColumn, 0);
	}
	
	public void addTuple(Tuple tuple) {
		Value columnVal = tuple.getValue(_aggregateColumn);
		assert (columnVal.getType().isInt());
		assert (!this.hasGroupBy());
		
		int columnInt = columnVal.toInt();
		int aggregateValue = _values.get(_aggregateColumn);
		
		switch (this._op) {
		case AVG:
			assert (false);
			break;
		case COUNT:
			aggregateValue++;
			break;
		case SUM:
			aggregateValue += columnInt;
			break;
		default:
			assert (false);
			break;
		}
		
		_values.put(_aggregateColumn, aggregateValue);
	}
	
	private Schema createSchema() {
		Schema newSchema = new Schema();
		newSchema.addColumn(_op.name(), Type.getIntType());
		return newSchema;
	}
	
	public Operator getOperator() {
		assert (!hasGroupBy());
		assert (_values.size() == 1);
		
		Tuple result = new Tuple(createSchema(), _values.get(_aggregateColumn));
		TupleOperator tupleOp = new TupleOperator(result);
		return tupleOp;
	}

	private boolean hasGroupBy() {
		return _groupColumn == Aggregate.NO_GROUPING;
	}
}
