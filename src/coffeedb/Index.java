package coffeedb;

import coffeedb.core.Btree;

public class Index {
	private Btree _index;
	private Table _table;
	private String _columnName;
	private int _columnIndex;
	private String _indexName;
	
	public Index(String indexName, Table table, String columnName) {
		int branchSize = 10;
		_index = new Btree(branchSize);
		_table = table;
		_columnIndex = table.getColumnIndex(columnName);
		_columnName = columnName;
		_indexName = indexName;
	}
	
	public void addKey(Tuple tuple) {
		Value key = tuple.getValue(_columnIndex);
		_index.addKey(key, tuple);
	}

	public void scanTable() {
		for (Tuple tuple : _table.getData()) {
			addKey(tuple);
		}
	}
}
