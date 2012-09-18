package coffeedb.parser;

import java.util.ArrayList;

import coffeedb.CoffeeDB;
import coffeedb.QueryPlan;
import coffeedb.Schema;
import coffeedb.Tuple;
import coffeedb.Value;
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
			default:
				assert (false);
		}
		
		assert (operator != null);
		eat (Token.SEMI_COLON);
		
		QueryPlan plan = new QueryPlan();
		plan.addOperator(operator);
		return plan;
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

	private Operator parseSelect() {
		eat(Token.SELECT);
		
		boolean allColumns = isToken(Token.ASTERIK);
		if (!allColumns) {
			String[] columns = parseStrings();
		} else {
			eat(Token.ASTERIK);
		}
		
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
		return new ScanOperator(tables[0]);
	}

	private void parseGroupBy() {
		assert false : "Not yet implemented";
	}

	private void parseWhere() {
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

	private Tuple convertIntoTuple(String tableName, String[] stringValues) {
		Schema tableSchema = CoffeeDB.catalog().getTable(tableName).getSchema();
		Value[] values = new Value[stringValues.length];
		
		for (int i = 0; i < values.length; i++) {
			Type type = tableSchema._columnTypes.get(i);
			String string = stringValues[i];
			Value value = new Value(type, string);
			values[i] = value;
		}
		
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
			eat(Token.IDENT);
		}
		
		return strings.toArray(new String[strings.size()]);
	}
}
