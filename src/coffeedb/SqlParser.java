package coffeedb;

import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

import coffeedb.types.Type;
import coffeedb.values.*;

import net.sf.jsqlparser.*;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.*;
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
		Table table = insert.getTable();
		insert.getItemsList().accept(new InsertDataPlan(table));
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
	
	class InsertDataPlan implements ItemsListVisitor, ExpressionVisitor {
		private Table _table;
		private ArrayList<Value> _values;
		public InsertDataPlan(Table table) {
			_table = table;
			_values = new ArrayList<Value>();
		}

		public void visit(SubSelect subSelect) {
			assert (false);
		}

		public void visit(ExpressionList expressions) {
			for (Expression expression : expressions.getExpressions()) {
				expression.accept(this);
			}
			
			_queryPlan.createInsertOperator(_table.getName(), _values);
		}

		public void visit(NullValue nullVal) {
			assert (false);
		}

		public void visit(Function function) {
			assert (false);
		}

		public void visit(InverseExpression arg0) {
			assert (false);
		}

		public void visit(JdbcParameter arg0) {
			assert (false);
		}

		public void visit(DoubleValue arg0) {
			assert (false);
		}

		public void visit(LongValue longValue) {
			long value = longValue.getValue();
			if (value == (int) value) {
				_values.add(new IntValue((int) value));
			} else {
				_values.add(new coffeedb.values.LongValue(value));
			}
		}

		public void visit(DateValue arg0) {
			assert false;
			
		}

		public void visit(TimeValue arg0) {
			assert false;
			
		}

		public void visit(TimestampValue arg0) {
			assert false;
			
		}

		@Override
		public void visit(Parenthesis arg0) {
			assert false;
			
		}

		@Override
		public void visit(StringValue arg0) {
			assert false;
		}

		@Override
		public void visit(Addition arg0) {
			assert false;
			
		}

		@Override
		public void visit(Division arg0) {
			assert false;
			
		}

		@Override
		public void visit(Multiplication arg0) {
			assert false;
			
		}

		@Override
		public void visit(Subtraction arg0) {
			assert false;
			
		}

		@Override
		public void visit(AndExpression arg0) {
			assert false;
			
		}

		@Override
		public void visit(OrExpression arg0) {
			assert false;
			
		}

		@Override
		public void visit(Between arg0) {
			assert false;
			
		}

		@Override
		public void visit(EqualsTo arg0) {
			assert false;
			
		}

		@Override
		public void visit(GreaterThan arg0) {
			assert false;
			
		}

		@Override
		public void visit(GreaterThanEquals arg0) {
			assert false;
			
		}

		@Override
		public void visit(InExpression arg0) {
			assert false;
			
		}

		@Override
		public void visit(IsNullExpression arg0) {
			assert false;
			
		}

		@Override
		public void visit(LikeExpression arg0) {
			assert false;
			
		}

		@Override
		public void visit(MinorThan arg0) {
			assert false;
			
		}

		@Override
		public void visit(MinorThanEquals arg0) {
			assert false;
			
		}

		@Override
		public void visit(NotEqualsTo arg0) {
			assert false;
			
		}

		@Override
		public void visit(Column arg0) {
			assert false;
			
		}

		@Override
		public void visit(CaseExpression arg0) {
			assert false;
			
		}

		@Override
		public void visit(WhenClause arg0) {
			assert false;
			
		}

		@Override
		public void visit(ExistsExpression arg0) {
			assert false;
			
		}

		@Override
		public void visit(AllComparisonExpression arg0) {
			assert false;
			
		}

		@Override
		public void visit(AnyComparisonExpression arg0) {
			assert false;
			
		}

		@Override
		public void visit(Concat arg0) {
			assert false;
			
		}

		@Override
		public void visit(Matches arg0) {
			assert false;
			
		}

		@Override
		public void visit(BitwiseAnd arg0) {
			assert false;
			
		}

		@Override
		public void visit(BitwiseOr arg0) {
			assert false;
			
		}

		@Override
		public void visit(BitwiseXor arg0) {
			assert false;
			
		}
	}
	

	} // end SqlParser file
