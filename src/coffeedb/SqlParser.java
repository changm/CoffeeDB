package coffeedb;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import coffeedb.operators.CreateOperator;
import coffeedb.types.Type;

import net.sf.jsqlparser.*;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

public class SqlParser implements StatementVisitor {
	private String _query;
	private QueryPlan _queryPlan;
	
	public SqlParser(String query) {
		_query = query;
		_queryPlan = new QueryPlan();
	}
	
	public QueryPlan generateQueryPlan() {
		CCJSqlParserManager parseManager = new CCJSqlParserManager();
		StringReader reader = new StringReader(_query);
		
		try {
			Statement statement = parseManager.parse(reader);
			statement.accept(this);
		} catch (JSQLParserException e) {
			System.out.println("Parser error: " + e.toString());
			e.printStackTrace();
		}
		
		return _queryPlan;
	}

	public void visit(Select select) {
		SelectQueryPlan selectData = new SelectQueryPlan();
		SelectBody body = select.getSelectBody();
		body.accept(selectData);
		_queryPlan.addSelect(selectData.getTableName(), selectData.getColumns());
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
		Table table = create.getTable();
		String tableName = table.getName();
		ArrayList<ColumnDefinition> columns = (ArrayList<ColumnDefinition>) create.getColumnDefinitions();
		
		String[] columnNames = new String[columns.size()];
		Type[] columnTypes = new Type[columns.size()];
		
		for (int i = 0; i < columns.size(); i++) {
			ColumnDefinition column = columns.get(i);
			ColDataType type = column.getColDataType();
			
			String columnName = column.getColumnName();
			String typeName = type.getDataType();
			Type columnType = Type.getType(typeName);
			
			columnNames[i] = columnName;
			columnTypes[i] = columnType;
			
			System.out.println(columnName + " = " + typeName);
		}
		
		Schema tableSchema = new Schema(columnNames, columnTypes);
		_queryPlan.addCreate(tableName, tableSchema);
	}
	
	public void visit(Drop drop) {
		assert (false);
	}
	
	public void visit(Replace arg0) {
	}

	public void visit(Truncate arg0) {
	}
		
	/***
	 * Visitor dedicated to creating query plans for SELECT statements
	 * @author masonchang
	 *
	 */
	class SelectQueryPlan implements SelectVisitor, SelectItemVisitor, FromItemVisitor {
		private String _tableName;
		private ArrayList<String> _columns;
		public SelectQueryPlan() {
			_columns = new ArrayList<String>();
		}
		
		public void visit(AllColumns allColumns) {
			_columns.add(allColumns.toString());
		}

		public void visit(AllTableColumns arg0) {
			assert (false);
		}

		public void visit(SelectExpressionItem arg0) {
			assert (false);
		}

		public void visit(PlainSelect plainSelect) {
			plainSelect.getFromItem().accept(this);
			for (SelectItem item : plainSelect.getSelectItems()) {
				item.accept(this);
			}
		}

		public void visit(Union arg0) {
			assert (false);
		}

		public void visit(Table table) {
			_tableName = table.getName();
		}

		public void visit(SubSelect arg0) {
			assert (false);
		}

		public void visit(SubJoin arg0) {
			assert (false);
		}
		
		public String getTableName() {
			return _tableName;
		}
		
		public ArrayList<String> getColumns() {
			return _columns;
		}
	}
	/*** END SELECT QUERY PLAN ***/
	

	}
