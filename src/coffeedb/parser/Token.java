package coffeedb.parser;

/***
 * SQL Keywords
 * @author masonchang
 */
public enum Token {
	// Keywords
	SELECT, 
	INSERT,
	UPDATE,
	FROM,
	WHERE,
	GROUP,
	BY,
	CREATE,
	INTO,
	VALUES,
	SET,
	DROP,
	TABLE,
	INDEX,
	JOIN, 
	DELETE,
	ON,
	
	// Symbols
	LEFT_PAREN,
	RIGHT_PAREN,
	IDENT,
	ASTERIK, 
	COMMA, 
	SEMI_COLON,
	EQUALS,
	LESS,
	GREATER,
	SINGLE_QUOTE,
	DOUBLE_QUOTE,
	
	// Types
	INT,
	STRING,
	VARCHAR,
	BLOB, 
	LONG, 
	DOUBLE,
	EOF, 
	NUMERIC, 
};
