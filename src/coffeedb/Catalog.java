package coffeedb;

import java.util.HashMap;

/***
 * Contains information about all the tables in the database
 * @author masonchang
 */
public class Catalog {
	private HashMap<String, Table> _tables;
	
	public Catalog() {
		_tables = new HashMap<String, Table>();
	}
	
	public void addTable(Table table) {
		assert (!tableExists(table));
		String tableName = table.getTableName();
		_tables.put(tableName, table);
	}
	
	public boolean tableExists(Table table) {
		String tableName = table.getTableName();
		return tableExists(tableName);
	}
	
	public boolean tableExists(String tableName) {
		return _tables.containsKey(tableName);
	}
	
	public Table getTable(String tableName) {
		assert (tableExists(tableName));
		return _tables.get(tableName);
	}
	
	public void clean() {
		for (Table table : _tables.values()) {
			table.clear();
		}
		
		_tables.clear();
	}
	
	public boolean deleteTable(String tableName) {
		Object delete = _tables.remove(tableName);
		return delete != null;
	}
}
