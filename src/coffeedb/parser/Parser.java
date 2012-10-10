package coffeedb.parser;

import java.util.ArrayList;
import java.util.List;

import coffeedb.CoffeeDB;
import coffeedb.ConstantValue;
import coffeedb.QueryPlan;
import coffeedb.Schema;
import coffeedb.SymbolValue;
import coffeedb.Table;
import coffeedb.Tuple;
import coffeedb.Value;
import coffeedb.functions.AggregateFunction;
import coffeedb.functions.FilterFunction;
import coffeedb.functions.Function;
import coffeedb.operators.*;
import coffeedb.types.Type;

public class Parser {
	private String _query;
	private Scanner _scanner;
	private Token _current;
	
	public Parser() {
	}
	
	public QueryPlan parseQuery(String query) {
		_query = query;
		_scanner = new Scanner(query);
		
		Operator operator = null;
		
		switch (getNext()) {
		case INSERT:
			operator = parseInsert();
			break;
		case SELECT:
			operator = parseSelect();
			break;
		case CREATE:
			operator = parseCreate();
			break;
		case UPDATE:
			operator = parseUpdate();
			break;
		case DROP:
			operator = parseDrop();
			break;
		case DELETE:
			operator = parseDelete();
			break;
		default:
			assert (false);
		}
		
		assert (operator != null);
		eat (Token.SEMI_COLON);
		
		QueryPlan plan = new QueryPlan();
		plan.addOperator(operator);
		return plan;
	}
	
	private Operator parseDelete() {
		eat(Token.DELETE);
		Operator tableScan = parseFrom();
		assert (tableScan instanceof ScanOperator);
		
		
		Table scanTable = ((ScanOperator) tableScan).getTable();
		Operator where = parseWhere(tableScan);
		return new DeleteOperator(where, scanTable);
	}

	private Operator parseDrop() {
		eat(Token.DROP);
		eat(Token.TABLE);
		String tableName = getIdent();
		eat(Token.IDENT);
		return new DropOperator(tableName);
	}

	private Operator parseUpdate() {
		eat(Token.UPDATE);
		assert (isToken(Token.IDENT));
		String tableName = getIdent();
		eat(Token.IDENT);
			
		Operator set = parseSet(tableName);
		
		ScanOperator scan = new ScanOperator(tableName);
		Operator whereOp = parseWhere(scan);
		
		set.setChild(whereOp);
		return set;
	}
	
	private boolean isQuote(Token token) {
		return (token == Token.DOUBLE_QUOTE) ||
				(token == Token.SINGLE_QUOTE);
	}
	
	private Value parseLiteralString() {
		assert isQuote(peek());
		Token end = peek();
		eat (peek());
		StringBuffer buffer = new StringBuffer();
		
		while (!isToken(end)) {
			buffer.append(getIdent());
			eat(Token.IDENT);
		}
		
		return new ConstantValue(Type.getStringType(), buffer.toString());
	}
	
	private Value parseValue() {
		Value result = null;
		switch (peek()) {
		case IDENT:
			result = new SymbolValue(getIdent());
			eat (Token.IDENT);
			break;
		case SINGLE_QUOTE:
		case DOUBLE_QUOTE:
			result = parseLiteralString();
			break;
		case NUMERIC:
			result = getConstantNumber();
			eat (Token.NUMERIC);
			break;
		default:
			assert false : "Unknown token: " + peek();
			break;
		}
		
		assert (result != null);
		return result;
	}
	
	private Operator parseSet(String tableName) {
		eat(Token.SET);
		ArrayList<Value> columns = new ArrayList<Value>();
		ArrayList<Value> values = new ArrayList<Value>();
		
		Value left = parseValue();
		eat(Token.EQUALS);
		Value right = parseValue();
		
		columns.add(left);
		values.add(right);
		
		while (isToken(Token.COMMA)) {
			eat (Token.COMMA);
			left = parseValue();
			eat(Token.EQUALS);
			right = parseValue();
		
			columns.add(left);
			values.add(right);
		}
		
		Value[] columnsArray = new Value[columns.size()];
		Value[] valuesArray = new Value[columns.size()];
		
		columns.toArray(columnsArray);
		values.toArray(valuesArray);
		return new SetOperator(columnsArray, valuesArray);
	}

	private Operator parseWhere(Operator source) {
		eat (Token.WHERE);
		
		Value left = parseValue();
		Predicate op = parsePredicate();
		Value right = parseValue();
		
		String functionName = "where";
		Function whereFunction = new FilterFunction(functionName, left,right, op);
		Operator top = new FunctionOperator(whereFunction);
		top.setChild(source);
		
		while (isToken(Token.COMMA)) {
			eat (Token.COMMA);
			left = parseValue();
			op = parsePredicate(); 
			right = parseValue();
			
			whereFunction = new FilterFunction(functionName, left,right, op);
			top = new FunctionOperator(whereFunction);
		}
		
		return top;
	}

	private Predicate parsePredicate() {
		Predicate op = null;
		switch (peek()) {
		case EQUALS:
			op = Predicate.EQUALS;
			break;
		case LESS:
			op = Predicate.LESS;
			break;
		case GREATER:
			op = Predicate.GREATER;
			break;
		default:
			assert false : "Unknown predicate";
		}
		
		eat (peek());
		return op;
	}

	private Operator parseCreate() {
		eat(Token.CREATE);
		eat(Token.TABLE);
		
		String tableName = getIdent();
		eat(Token.IDENT);
		eat(Token.LEFT_PAREN);
		Schema schema = parseParams();
		eat (Token.RIGHT_PAREN);
		
		return new CreateOperator(tableName, schema);
	}

	private Schema parseParams() {
		Schema schema = new Schema();
		
		String columnName = getIdent();
		eat(Token.IDENT);
		
		Token typeToken = peek();
		Type columnType = Type.getType(typeToken);
		eat (typeToken);
		
		schema.addColumn(columnName, columnType);
		
		while (isToken(Token.COMMA)) {
			eat(Token.COMMA);
			columnName = getIdent();
			eat (Token.IDENT);
			
			typeToken = peek();
			columnType = Type.getType(typeToken);
			eat(typeToken);
			
			schema.addColumn(columnName, columnType);
		}
		
		return schema;
	}

	private Token getNext() {
		_current = _scanner.next();
		return _current;
	}
	
	private Operator parseSelectExpression() {
		if (isToken(Token.ASTERIK)) {
			eat(Token.ASTERIK);
			return new Projection();
		}
		
		assert (isToken(Token.IDENT));
		String ident = getIdent();
		eat(Token.IDENT);
		
		if (isToken(Token.LEFT_PAREN)) {
			return parseFunction(ident);
		}
		
		ArrayList<String> columns = new ArrayList<String>();
		columns.add(ident);
		
		while (isToken(Token.COMMA)) {
			eat(Token.COMMA);
			columns.add(getIdent());
			eat(Token.IDENT);
		}
		
		return new Projection(null, columns.toArray(new String[columns.size()]));
	}

	private Operator parseFunction(String functionName) {
		eat(Token.LEFT_PAREN);
		
		assert (isToken(Token.IDENT));
		String column = getIdent();
		eat(Token.IDENT);
		eat(Token.RIGHT_PAREN);
		
		String groupBy = null;
		Function function = new AggregateFunction(functionName, column, groupBy);
		return new FunctionOperator(function);
	}

	private Operator parseSelect() {
		eat(Token.SELECT);
		
		Operator select = parseSelectExpression();
		Operator dataSource = parseFrom();
		select.setChild(dataSource);
		
		if (isToken(Token.WHERE)) {
			Operator where = parseWhere(select);
			select = where;
		}
		
		if (isToken(Token.GROUP)) {
			eat(Token.GROUP);
			eat(Token.BY);
			parseGroupBy(select);
		}
		
		return select;
	}
	
	private boolean isJoin() {
		Token current = peek();
		return (current == Token.COMMA) ||
				(current == Token.JOIN);
	}
	
	private void eatJoin() {
		assert (isJoin());
		eat(peek());
	}

	private Operator parseFrom() {
		eat (Token.FROM);
		assert (isToken(Token.IDENT));
		String table = getIdent();
		eat(Token.IDENT);
		Operator result = new ScanOperator(table);
		
		while (isJoin()) {
			eatJoin();
			String joinTable = getIdent();
			eat (Token.IDENT);
			Operator joinScan = new ScanOperator(joinTable);
			result = new JoinOperator(result, joinScan);
		}
		
		return result;
	}

	private void parseGroupBy(Operator select) {
		String groupBy= getIdent();
		eat(Token.IDENT);
		
		// Only aggregates can have group by selectors
		assert (select instanceof FunctionOperator);
		FunctionOperator functionOp = (FunctionOperator) (select);
		Function aggregateFunction = functionOp.getFunction();
		AggregateFunction agg = (AggregateFunction) aggregateFunction;
		agg.setGroupBy(groupBy);
	}

	private String getIdent() {
		assert (isToken(Token.IDENT));
		return _scanner.getIdent();
	}
	
	private Value getConstantNumber() {
		assert (isToken(Token.NUMERIC));
		return _scanner.getNumber();
	}
	
	private boolean isToken(Token token) {
		return peek() == token;
	}
	
	private void eat(Token token) {
		assert (_current == token) : "Wrong token. Expected " + token.name() + " got: " + _current.name();
		_current = _scanner.next();
	}
	
	private Token peek() {
		return _current;
	}

	private Operator parseInsert() {
		eat(Token.INSERT);
		eat(Token.INTO);
		String tableName = getIdent();
		
		eat(Token.IDENT);
		eat(Token.VALUES);
		eat(Token.LEFT_PAREN);
		
		Value[] values = parseValues();
		
		eat(Token.RIGHT_PAREN);
		Schema tableSchema = CoffeeDB.catalog().getTable(tableName).getSchema();
		Tuple tuple = new Tuple(tableSchema, values);
		return new InsertOperator(tableName, tuple);
	}
	
	private Value[] convertIntoValues(String tableName, List<String> stringValues) {
		String[] store = new String[stringValues.size()];
		stringValues.toArray(store);
		return convertIntoValues(tableName, store);
	}
	
	private Value[] convertIntoValues(String tableName, String[] stringValues) {
		Schema tableSchema = CoffeeDB.catalog().getTable(tableName).getSchema();
		Value[] values = new Value[stringValues.length];
		
		for (int i = 0; i < values.length; i++) {
			Type type = tableSchema._columnTypes.get(i);
			String string = stringValues[i];
			Value value = new Value(type, string);
			values[i] = value;
		}
		
		return values;
	}

	private Tuple convertIntoTuple(String tableName, String[] stringValues) {
		Value[] values = convertIntoValues(tableName, stringValues);
		Schema tableSchema = CoffeeDB.catalog().getTable(tableName).getSchema();
		return new Tuple(tableSchema, values);
	}

	private Value[] parseValues() {
		ArrayList<Value> values =  new ArrayList<Value>();
		Value value = parseValue();
		values.add(value);
		
		while (isToken(Token.COMMA)) {
			eat(Token.COMMA);
			value = parseValue();
			values.add(value);
		}
		
		return values.toArray(new Value[values.size()]);
	}
}
