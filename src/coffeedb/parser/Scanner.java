package coffeedb.parser;

import java.util.HashMap;

import coffeedb.ConstantValue;
import coffeedb.types.Type;

public class Scanner {
	private int _currentLoc;
	private String _string;
	private String _currentIdent;
	private char[] _queryString;
	private ConstantValue _numberValue;
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
		_tokenNames.put("<", Token.LESS);
		_tokenNames.put(">", Token.GREATER);
		_tokenNames.put("'", Token.SINGLE_QUOTE);
		_tokenNames.put("\"", Token.DOUBLE_QUOTE);
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
				if ((_currentLoc + 1) == _queryString.length) break;
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
	
	boolean isInt(String number) {
		try {
			Integer.parseInt(number);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	boolean isDouble(String number) {
		try {
			Double.parseDouble(number);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean isNumeric(String number) {
		return isInt(number) || isDouble(number);
	}

	private Token getToken(String result) {
		result = result.toLowerCase();
		if (_tokenNames.containsKey(result)) {
			return _tokenNames.get(result);
		}
		
		if (isNumeric(result)) {
			setNumber(result);
			return Token.NUMERIC;
		}
		
		_currentIdent = result;
		return Token.IDENT;
	}
	
	private void setNumber(String result) {
		try {
			int value = Integer.parseInt(result);
			_numberValue = new ConstantValue(Type.getIntType(), value);
		} catch (Exception e) {
			try {
				double value = Double.parseDouble(result);
				_numberValue = new ConstantValue(Type.getDoubleType(), value);
			} catch (Exception f) {
				assert (false);
			}
		}
	}

	public String getIdent() {
		return _currentIdent;
	}
	
	public ConstantValue getNumber() {
		return _numberValue;
	}
}
