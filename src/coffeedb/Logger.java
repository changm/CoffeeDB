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
	private String _logFile;
	
	public Logger() {
		_logFile = LOG_DIR + "/log.txt";
	}
	
	/***
	 * @param coffeeDB
	 */
	public ArrayList<String> snapshot(CoffeeDB coffeeDB) {
		Catalog catalog = coffeeDB.getCatalog();
		Iterator<Table> tableIter = catalog.getTables();
		ArrayList<String> logFiles = new ArrayList<String>();
		
		while (tableIter.hasNext()) {
			Table table = tableIter.next();
			String tableFile = getTableLogFile(table);
			snapshotTable(table, tableFile);
			logFiles.add(tableFile);
		}
		
		return logFiles;
	}
	
	public void recoverFromSnapshot(CoffeeDB coffeeDB) {
		try {
			Catalog catalog = coffeeDB.getCatalog();
			FileInputStream catalogReader = new FileInputStream(_logFile);
			recoverTables(catalog, catalogReader);
			catalogReader.close();
			
			Iterator<Table> tableIter = catalog.getTables();
			while (tableIter.hasNext()) {
				Table table = tableIter.next();
				recoverTable(table, catalogReader);
			}
		} catch (Exception e) {
			System.err.println("Error recovering from table");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void recoverTables(Catalog catalog, FileInputStream catalogReader) {
		assert false : "Not yet implemented";
	}

	private void snapshotTable(Table table, String tableFile) {
		try {
			FileOutputStream writer = new FileOutputStream(tableFile);
			writeTableHeader(table, writer);
			
			Iterator<Tuple> tupleIter = table.getIterator();
			while (tupleIter.hasNext()) {
				Tuple tuple = tupleIter.next();
				byte[] tupleData = tuple.serialize();
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

	private void writeFooter(FileOutputStream writer) throws IOException {
		String end = "DONE";
		writer.write(end.getBytes());
	}
	
	private String readString(FileInputStream reader, int length) throws IOException {
		byte[] stringData = ByteBuffer.allocate(length).array();
		int bufferOffset = 0;
		reader.read(stringData, bufferOffset, length);
		return stringData.toString();
	}
	
	private int readInt(FileInputStream reader) throws IOException {
		return reader.read();
	}
	
	private void recoverTable(Table table, FileInputStream reader) throws IOException {
		int tableLength = readInt(reader);
		String tableName = readString(reader, tableLength);
		
		int end = readInt(reader);
		assert (end == END_TABLE_NAME);
		
		int tupleCount = readInt(reader);
		int endTupleMarker = readInt(reader);
		assert (endTupleMarker == END_TUPLE_COUNT);
	}

	/***
	 * Table header consists of 
	 * table name length
	 * table name string
	 * 0xFFFFFFFF - marker
	 * int - tuple count
	 * 0xAAAAAAAA - end tuple count marker
	 * schema 
	 * 0xBBBBBBBB - end schema marker
	 * @param table
	 * @param writer
	 * @throws IOException
	 */
	private void writeTableHeader(Table table, FileOutputStream writer) throws IOException {
		String name = table.getTableName();
		writeInt(name.length(), writer);
		writer.write(name.getBytes());
		writeInt(END_TABLE_NAME, writer);
		
		int tupleCount = table.getTupleCount();
		writeInt(tupleCount, writer);
		writeInt(END_TUPLE_COUNT, writer);
		
		writeSchema(table, writer);
		writeInt(END_SCHEMA_MARKER, writer);
	}

	private void writeSchema(Table table, FileOutputStream writer)
			throws IOException {
		byte[] schemaData = table.getSchema().serialize();
		writer.write(schemaData);
	}
	
	private void writeInt(int value, FileOutputStream writer) throws IOException {
		byte[] byteVal = ByteBuffer.allocate(4).putInt(value).array();
		writer.write(byteVal);
	}

	// Return LOGDIR/tableName
	private String getTableLogFile(Table table) {
		String tableName = table.getTableName();
		return LOG_DIR + "/" + tableName + ".dat";
	}

	/***
	 * Redo log just redoes a given SQL query
	 * and reapplies them to the database
	 */
	public void writeRedoLog() {
		
	}
}
