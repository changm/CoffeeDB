package coffeedb.parser;

import java.util.ArrayList;
import java.util.List;

import coffeedb.CoffeeDB;
import coffeedb.QueryPlan;
import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;
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
		default:
			assert (false);
		}
		
		assert (operator != null);
		eat (Token.SEMI_COLON);
		
		QueryPlan plan = new QueryPlan();
		plan.addOperator(operator);
		return plan;
	}
	
	private Operator parseUpdate() {
		eat(Token.UPDATE);
		assert (isToken(Token.IDENT));
		String tableName = getIdent();
		eat(Token.IDENT);
		
		Operator set = parseSet(tableName);
		
		Operator whereOp = parseWhere();
		ScanOperator scan = new ScanOperator(tableName);
		whereOp.setChild(scan);
		
		set.setChild(whereOp);
		return set;
	}
	
	private Operator parseSet(String tableName) {
		eat(Token.SET);
		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		
		String column = getIdent();
		eat(Token.IDENT);
		eat (Token.EQUALS);
		String value = getIdent();
		eat(Token.IDENT);
		
		columns.add(column);
		values.add(value);
		
		while (isToken(Token.COMMA)) {
			eat (Token.COMMA);
			column = getIdent();
			eat(Token.IDENT);
			eat(Token.EQUALS);
			value = getIdent();
			eat(Token.IDENT);
		
			columns.add(column);
			values.add(value);
		}
		
		Value[] valuesArray = convertIntoValues(tableName, values);
		String[] columnsArray = new String[columns.size()];
		columns.toArray(columnsArray);
		return new SetOperator(columnsArray, valuesArray);
	}

	private Operator parseWhere() {
		eat (Token.WHERE);
		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		
		String column = getIdent();
		eat(Token.IDENT);
		eat (Token.EQUALS);
		String value = getIdent();
		eat(Token.IDENT);
		
		columns.add(column);
		values.add(value);
		
		while (isToken(Token.COMMA)) {
			eat (Token.COMMA);
			column = getIdent();
			eat(Token.IDENT);
			eat (Token.EQUALS);
			value = getIdent();
			eat(Token.IDENT);
		
			columns.add(column);
			values.add(value);
		}
		
		String functionName = "where";
		assert (columns.size() == values.size());
		String[] arguments = new String[(columns.size() * 2) + 1];
		arguments[arguments.length -1] = Predicate.EQUALS.toString();
		
		for (int i = 0; i < columns.size(); i++) {
			column = columns.get(i);
			value = values.get(i);
		
			arguments[i] = column;
			arguments[i+1] = value;
		}
		
		Function whereFunction = new Function(functionName, arguments);
		return new FilterOperator(null, whereFunction, Predicate.EQUALS);
	}


	private Operator parseCreate() {
		eat(Token.CREATE);
		assert (getIdent().equalsIgnoreCase("table"));
		eat(Token.IDENT);
		
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
		String[] arguments = parseStrings();
		eat(Token.RIGHT_PAREN);
		
		Function function = new Function(functionName, arguments);
		return new FunctionOperator(function);
	}

	private Operator parseSelect() {
		eat(Token.SELECT);
		
		Operator select = parseSelectExpression();
		eat(Token.FROM);
		String[] tables = parseStrings();
		
		if (isToken(Token.WHERE)) {
			parseWhere();
		}
		
		if (isToken(Token.GROUP)) {
			eat(Token.GROUP);
			eat(Token.BY);
			parseGroupBy();
		}
		
		assert (tables.length == 1);
		ScanOperator scan = new ScanOperator(tables[0]);
		select.setChild(scan);
		return select;
	}

	private void parseGroupBy() {
		assert false : "Not yet implemented";
	}

		// Parse String, Deliminated, By, Comma
	private String[] parseStrings() {
		ArrayList<String> strings = new ArrayList<String>();
		String ident = getIdent();
		strings.add(ident);
		eat(Token.IDENT);
		
		while (isToken(Token.COMMA)) {
			eat(Token.COMMA);
			ident = getIdent();
			strings.add(ident);
		}
		
		return strings.toArray(new String[strings.size()]);
	}
	
	private String getIdent() {
		assert (isToken(Token.IDENT));
		return _scanner.getIdent();
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
		
		String[] values = parseValues();
		
		eat(Token.RIGHT_PAREN);
		Tuple tuple = convertIntoTuple(tableName, values);
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

	private String[] parseValues() {
		ArrayList<String> strings =  new ArrayList<String>();
		String value = getIdent();
		strings.add(value);
		eat(Token.IDENT);
		
		while (isToken(Token.COMMA)) {
			eat(Token.COMMA);
			value = getIdent();
			strings.add(value);
			eat(Token.IDENT);
		}
		
		return strings.toArray(new String[strings.size()]);
	}
}
