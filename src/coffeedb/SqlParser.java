package coffeedb;

import java.io.StringReader;

import net.sf.jsqlparser.*;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.Union;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

public class SqlParser implements SelectVisitor, StatementVisitor {
	private String _query;
	
	public SqlParser(String query) {
		_query = query;
	}
	
	public void parseQuery() {
		CCJSqlParserManager parseManager = new CCJSqlParserManager();
		StringReader reader = new StringReader(_query);
		
		try {
			Statement statement = parseManager.parse(reader);
			statement.accept(this);
		} catch (JSQLParserException e) {
			System.out.println("Parser error: " + e.toString());
			e.printStackTrace();
		}
	}

	public void visit(PlainSelect select) {
		assert (false);
	}

	public void visit(Select select) {
		assert (false);
	}

	public void visit(Delete delete) {
		assert (false);
	}

	public void visit(Update update) {
		assert (false);
	}

	public void visit(Insert insert) {
		assert (false);
	}

	public void visit(CreateTable create) {
		assert (false);
	}
	
	public void visit(Drop drop) {
		assert (false);
	}
		
	public void visit(Replace replace) {
		assert (false);
	}

	public void visit(Truncate truncate) {
		assert (false);
	}
	
	public void visit(Union union) {
		assert (false);
	}
}
