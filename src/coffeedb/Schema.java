package coffeedb;

import java.util.ArrayList;

import coffeedb.types.Type;

/***
 * We're assuming row based schemas here.
 * Maybe we'll try a column store another day
 * @author masonchang
 */
public class Schema {
	public ArrayList<String> _columnNames;
	public ArrayList<Type> _columnTypes;
	
	public Schema() {
		init();
	}
	
	public Schema(String[] columns, Type[] types) {
		assert (columns != null);
		assert (types != null);
		init();
		
		_columnNames.addAll(_columnNames);
		// For some reason can't do addAll with non bulitin types
		for (Type t : types) {
			_columnTypes.add(t);
		}
	}
	
	private void init() {
		_columnNames = new ArrayList<String>();
		_columnTypes = new ArrayList<Type>();
	}
	
	public void addColumn(String columnName, Type type) {
		_columnNames.add(columnName);
		_columnTypes.add(type);
	}
}
