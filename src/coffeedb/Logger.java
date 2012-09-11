package coffeedb;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

/***
 * Implements the log
 * Essentially takes snapshots and records active SQL queries
 * 
 * We have one logfile that is metadata for all the tables
 * Logfile has number of tables and their location on disk
 * 
 * Each table is backed by a real file on disk
 * Each table is constant size of tuple * number of tuples
 * 
 * The REDO log is sequence of SQL queries since last snapshot
 * Each SQL query is deliminated by a ;
 * @author masonchang
 */
public class Logger {
	private static String LOG_DIR = "/Users/masonchang/Documents/workspace/CoffeeDB/log";
	private static int END_TABLE_NAME = 0xAAAAAAAA;
	private static int END_TUPLE_COUNT = 0xBBBBBBBB;
	private static int END_SCHEMA_MARKER = 0xCCCCCCCC;
	private static int END_CATALOG_MARKER = 0xDDDDDDDD;
	private static int END_TABLE_LOG = 0xEEEEEEEE;
	
	private String _logFile;
	private String _catalogFile;
	
	public Logger() {
		_logFile = LOG_DIR + "/log.txt";
		_catalogFile = LOG_DIR + "/catalog.dat";
	}
	
	/***
	 * @param coffeeDB
	 */
	public ArrayList<String> snapshot(CoffeeDB coffeeDB) {
		Catalog catalog = coffeeDB.getCatalog();
				
		ArrayList<String> logFiles = new ArrayList<String>();
		buildCatalogHeader(catalog, logFiles);
		snapshotTables(catalog, logFiles);
		return logFiles;
	}

	/***
	 * Catalog log structure
	 * 4 byte int - number of tables
	 * [ while number of tables ]
	 * 4 byte int - length of table name
	 * String - table name
	 * End Catalog Struct marker
	 * @param catalog
	 * @param logFiles
	 */
	private void buildCatalogHeader(Catalog catalog, ArrayList<String> logFiles) {
		int tableCount = catalog.getTableCount();
		Iterator<Table> tableIter = catalog.getTables();
				
		try {
			FileOutputStream catalogWriter = new FileOutputStream(_catalogFile);
			writeInt(tableCount, catalogWriter);
			
			while (tableIter.hasNext()) {
				String tableName = tableIter.next().getTableName();
				int stringLength = tableName.length();
				
				writeInt(stringLength, catalogWriter);
				writeString(tableName, catalogWriter);
			}
			
			writeInt(END_CATALOG_MARKER, catalogWriter);
			catalogWriter.close();
		} catch (IOException e) {
			System.err.println("Error dumping catalog header: " + e.toString());
			e.printStackTrace();
		}
		
		logFiles.add(_catalogFile);
	}

	private void writeString(String string, FileOutputStream writer) throws IOException {
		writer.write(string.getBytes());
	}

	private void snapshotTables(Catalog catalog, ArrayList<String> logFiles) {
		Iterator<Table> tableIter = catalog.getTables();
		while (tableIter.hasNext()) {
			Table table = tableIter.next();
			String tableFile = getTableLogFile(table.getTableName());
			snapshotTable(table, tableFile);
			logFiles.add(tableFile);
		}
	}
	
	public void replayRedoLog(CoffeeDB coffeeDB, String redoLog) {
		try {
			
		} catch (Exception e) {
			
		}
	}
	
	public void recoverFromSnapshot(CoffeeDB coffeeDB) {
		try {
			Catalog catalog = coffeeDB.getCatalog();
			FileInputStream catalogReader = new FileInputStream(_catalogFile);
			String[] tableNames = recoverTableNames(catalog, catalogReader);
			catalogReader.close();
			
			for (String tableName : tableNames) {
				recoverTable(tableName);
			}
		} catch (Exception e) {
			System.err.println("Error recovering from table " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	private String[] recoverTableNames(Catalog catalog, FileInputStream catalogReader) throws IOException {
		int tableCount = readInt(catalogReader);
		String[] tableNames = new String[tableCount];
		
		for (int i = 0; i < tableCount; i++) {
			int tableNameLength = readInt(catalogReader);
			String tableName = readString(tableNameLength, catalogReader);
			tableNames[i] = tableName;
		}
		
		return tableNames;
	}

	/****
	 * Table is a table header
	 * then [number of tuples]
	 * int - tuple size
	 * tuple data
	 * footer
	 * @param table
	 * @param tableFile
	 */
	private void snapshotTable(Table table, String tableFile) {
		try {
			FileOutputStream writer = new FileOutputStream(tableFile);
			writeTableHeader(table, writer);
			
			Iterator<Tuple> tupleIter = table.getIterator();
			while (tupleIter.hasNext()) {
				Tuple tuple = tupleIter.next();
				byte[] tupleData = tuple.serialize();
				int tupleLength = tupleData.length;
				writeInt(tupleLength, writer);
				writer.write(tupleData);
			}
			
			writeFooter(writer);
			writer.close();
		} catch (Exception e) {
			System.err.println("Error snapping table : " + table.getTableName() + " - " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private Tuple readTuple(FileInputStream tableReader, Schema schema) throws IOException {
		int tupleSize = readInt(tableReader);
		Tuple tuple = new Tuple(schema);
		byte[] buffer = new byte[tupleSize];
		tableReader.read(buffer, 0, tupleSize);
		tuple.recover(buffer);
		return tuple;
	}
	
	private void recoverTable(String tableName) throws IOException {
		String tableDataFile = getTableLogFile(tableName);
		FileInputStream tableReader = new FileInputStream(tableDataFile);
		
		Table table = readTableData(tableName, tableReader);
		int tupleCount = readInt(tableReader);
		int endTupleMarker = readInt(tableReader);
		assert (endTupleMarker == END_TUPLE_COUNT);
		Schema schema = table.getSchema();
		
		for (int i = 0; i < tupleCount; i++) {
			Tuple tuple = readTuple(tableReader, schema);
			table.insertTuple(tuple);
		}
		
		CoffeeDB.getInstance().getCatalog().addTable(table);
		readFooter(tableReader);
		tableReader.close();
	}

	private Table readTableData(String tableName, FileInputStream tableReader)
			throws IOException {
		int tableLength = readInt(tableReader);
		String readTableName = readString(tableLength, tableReader);
		assert (tableName.equals(readTableName));
		
		int end = readInt(tableReader);
		assert (end == END_TABLE_NAME);
		
		Schema schema = readSchema(tableReader);
		int endSchemaMarker = readInt(tableReader);
		assert (endSchemaMarker == END_SCHEMA_MARKER);
		
		return new Table(tableName, schema);
	}

	private void writeFooter(FileOutputStream writer) throws IOException {
		writeInt(END_TABLE_LOG, writer);
	}
	
	private void readFooter(FileInputStream tableReader) throws IOException {
		int end = readInt(tableReader);
		assert (end == END_TABLE_LOG);
	}
	
	/***
	 * Table header consists of 
	 * table name length
	 * table name string
	 * END_TABLE_NAME
	 * schema 
	 * END_SCHEMA_MARKER
	 * tuple count
	 * END_TUPLE_COUNT
	 * @param table
	 * @param writer
	 * @throws IOException
	 */
	private void writeTableHeader(Table table, FileOutputStream writer) throws IOException {
		String name = table.getTableName();
		writeInt(name.length(), writer);
		writer.write(name.getBytes());
		writeInt(END_TABLE_NAME, writer);
	
		writeSchema(table, writer);
		writeInt(END_SCHEMA_MARKER, writer);
		
		int tupleCount = table.getTupleCount();
		writeInt(tupleCount, writer);
		writeInt(END_TUPLE_COUNT, writer);
	}

	/***
	 * Schema is length of schema structure on disk
	 * and then that number of bytes of raw data
	 * @param table
	 * @param writer
	 * @throws IOException
	 */
	private void writeSchema(Table table, FileOutputStream writer)
			throws IOException {
		byte[] schemaData = table.getSchema().serialize();
		writeInt(schemaData.length, writer);
		writer.write(schemaData);
	}
	
	private Schema readSchema(FileInputStream tableReader) throws IOException {
		int schemaSize = readInt(tableReader);
		byte[] schemaData = new byte[schemaSize];
		tableReader.read(schemaData, 0, schemaSize);
		
		Schema schema = new Schema();
		schema.recover(schemaData);
		return schema;
	}
	
	// Return LOGDIR/tableName
	private String getTableLogFile(String tableName) {
		return LOG_DIR + "/" + tableName + ".dat";
	}
	
	private String readString(int length, FileInputStream reader) throws IOException {
		byte[] stringData = new byte[length]; 
		int bufferOffset = 0;
		reader.read(stringData, bufferOffset, length);
		return new String(stringData); 
	}
	
	private void writeInt(int value, FileOutputStream writer) throws IOException {
		byte[] byteVal = ByteBuffer.allocate(4).putInt(value).array();
		writer.write(byteVal);
	}
	
	private int readInt(FileInputStream reader) throws IOException {
		byte[] intVal = new byte[4];
		reader.read(intVal);
		return ByteBuffer.wrap(intVal).getInt();
	}

	/***
	 * Redo log just redoes a given SQL query
	 * and reapplies them to the database
	 */
	public void writeRedoLog() {
		
	}
}
