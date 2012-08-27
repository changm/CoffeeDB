package coffeedb;

public class Table {
	private String _tableName;
	
	public Table(String tableName) {
		_tableName = tableName;
	}
	
	public String getTableName() {
		return _tableName;
	}
}
