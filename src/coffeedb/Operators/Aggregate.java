package coffeedb.operators;

import coffeedb.Schema;
import coffeedb.Tuple;


/***
 * Aggregate operator to support fields such as 
 * COUNT, SUM, etc
 * @author masonchang
 *
 */
public class Aggregate extends Operator {
	public static final int NO_GROUPING = -1;
	protected Operator _source;
	protected AggregateOp _op;
	protected String _aggregate;
	protected int _groupByColumn;
	private Aggregator _aggregator;
	private Operator _results;
	
	public Aggregate(Operator source, String aggregateColumn, String groupBy, AggregateOp op) {
		init(source, aggregateColumn, op);
		_groupByColumn = getSchema()._columnNames.indexOf(groupBy);
	}

	private void init(Operator source, String aggregateColumn, AggregateOp op) {
		_source = source;
		_aggregate = aggregateColumn;
		_op = op;
	}
	
	public Aggregate(Operator source, String aggregateColumn, AggregateOp op) {
		init(source, aggregateColumn, op);
		_groupByColumn = NO_GROUPING;
	}
	
	protected Aggregate() {
	}

	public void open() {
		_source.open();
		_results = runAggregate();
	}

	private Operator runAggregate() {
		createAggregate();
		
		_source.open();
		while (_source.hasNext()) {
			Tuple tuple = _source.next();
			_aggregator.addTuple(tuple);
		}
		
		return _aggregator.getOperator();
	}

	private void createAggregate() {
		int aggregateColumn = getSchema()._columnNames.indexOf(_aggregate);
		_aggregator = new IntAggregate(aggregateColumn, _groupByColumn, _op);
	}

	public void reset() {
		_source.reset();
	}

	public void close() {
		_source.close();
	}

	protected Tuple getNext() {
		if (_results.hasNext()) {
			return _results.next();
		}
		
		return null;
	}

	protected Schema getSchema() {
		return _source.getSchema();
	}
	
	protected boolean hasAggregate() {
		return _groupByColumn != NO_GROUPING;
	}
}
