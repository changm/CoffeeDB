package coffeedb.parser;

import java.util.HashMap;

public class Scanner {
	private int _currentLoc;
	private String _string;
	private String _currentIdent;
	private char[] _queryString;
	private HashMap<String, Token> _tokenNames;
	
	public Scanner(String string) {
		_string = string;
		_currentLoc = 0;
		_queryString = _string.toCharArray();
		initTokenNames();
	}
	
	private void initTokenNames() {
		_tokenNames = new HashMap<String, Token>();
		
		for (Token t : Token.values()) {
			_tokenNames.put(t.name().toLowerCase(), t);
		}
		
		initTokenSymbols();
	}

	private void initTokenSymbols() {
		_tokenNames.put("*", Token.ASTERIK);
		_tokenNames.put("(", Token.LEFT_PAREN);
		_tokenNames.put(")", Token.RIGHT_PAREN);
		_tokenNames.put(",", Token.COMMA);
		_tokenNames.put(";", Token.SEMI_COLON);
		_tokenNames.put("=", Token.EQUALS);
	}

	private boolean isEOF() {
		return _currentLoc == _queryString.length; 
	}
	
	public Token next() {
		if (isEOF()) return Token.EOF;
		
		char current = _queryString[_currentLoc];
		StringBuffer nextToken = new StringBuffer();
		current = eatWhitespace(current);
		
		if (!Character.isLetterOrDigit(current)) {
			nextToken.append(current);
			++_currentLoc;
		} else {
			// Ident parsing
			while (!(Character.isWhitespace(current)) && !isEOF() && Character.isLetterOrDigit(current)) {
				nextToken.append(current);
				current = _queryString[++_currentLoc];
			}
		}
		
		String result = nextToken.toString();
		return getToken(result);
	}

	private char eatWhitespace(char current) {
		while (Character.isWhitespace(current)) {
			current = _queryString[++_currentLoc];
		}
		return current;
	}

	private Token getToken(String result) {
		result = result.toLowerCase();
		if (_tokenNames.containsKey(result)) {
			return _tokenNames.get(result);
		}
		
		_currentIdent = result;
		return Token.IDENT;
	}
	
	public String getIdent() {
		return _currentIdent;
	}
}
