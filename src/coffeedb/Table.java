package coffeedb;

public class Table {
	private String _tableName;
	private Schema _schema;
	
	public Table(String tableName, Schema schema) {
		_tableName = tableName;
		_schema = schema;
	}
	
	public String getTableName() {
		return _tableName;
	}
	
	public Schema getSchema() {
		return _schema;
	}
}
