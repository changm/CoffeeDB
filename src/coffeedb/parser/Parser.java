package coffeedb.parser;

import java.util.ArrayList;

import coffeedb.QueryPlan;
import coffeedb.operators.Operator;

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
		
		switch (_scanner.next()) {
		case INSERT:
			operator = parseInsert();
			break;
		case SELECT:
			operator = parseSelect();
			break;
		case UPDATE:
			default:
				assert (false);
		}
		
		assert (operator != null);
		return new QueryPlan();
	}

	private Operator parseSelect() {
		eat(Token.SELECT);
		String[] columns = parseColumns();
		
		eat(Token.FROM);
		parseTables();
		
		if (isToken(Token.WHERE)) {
			parseWhere();
		}
		
		if (isToken(Token.GROUP)) {
			eat(Token.GROUP);
			eat(Token.BY);
			parseGroupBy();
			
		}
		
		assert (false);
		return null;
	}

	private void parseTables() {
		assert false : "Not yet implemented";
	}

	private void parseGroupBy() {
		assert false : "Not yet implemented";
	}

	private void parseWhere() {
		assert false : "Not yet implemented";
	}

	private String[] parseColumns() {
		ArrayList<String> columns = new ArrayList<String>();
		String ident = getIdent();
		columns.add(ident);
		
		while (isToken(Token.COMMA)) {
			eat(Token.COMMA);
			ident = getIdent();
			columns.add(ident);
		}
		
		return (String[]) columns.toArray();
	}
	
	private String getIdent() {
		assert (isToken(Token.IDENT));
		return _scanner.getIdent();
	}
	
	private boolean isToken(Token token) {
		return peek() == token;
	}
	
	private void eat(Token token) {
		assert (_current == token);
		_current = _scanner.next();
	}
	
	private Token peek() {
		return _current;
	}

	private Operator parseInsert() {
		assert false : "Not yet implemented";
		return null;
	}
}
