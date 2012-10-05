package coffeedb;

import java.util.List;

import coffeedb.parser.Parser;

public class CoffeeDB {
	static CoffeeDB _singleton = null;
	private Config _config = null;
	private Catalog _catalog;
	private ExecutionEngine _engine;
	private Logger _logger;
	
	private CoffeeDB() {
		init();
	}
	
	private void init() {
		_engine = new ExecutionEngine();
		_catalog = new Catalog();
		_logger = new Logger();
	}
	
	private void shutdown() {
		_engine.shutdown();
	}
	
	public void reset() {
		_catalog.clean();
	}

	public void test() {
		Catalog catalog = CoffeeDB.getInstance().getCatalog();
		Table table = new Table("TestTable", null);
		catalog.addTable(table);
	}
	
	private void printResults(Transaction transaction) {
		assert (transaction.didCommit());
		List<Tuple> results = transaction.getResult();
		boolean printSchema = true;
		for (Tuple tuple : results) {
			if (printSchema) {
				System.out.println(tuple.getSchema());
				printSchema = false;
			}
			
			System.out.println(tuple);
		}
		
		if (results.size() == 0) {
			System.out.println("No rows selected");
		}
	}
	
	public void snapshot() {
		getLogger().snapshot(this);
	}
	
	public void recoverFromLog() {
		getLogger().recoverFromSnapshot(this);
	}
	
	public List<Tuple> runQuery(String query) {
		Parser parser = new Parser();
		QueryPlan plan = parser.parseQuery(query);
		
		Transaction transaction = _engine.executeQueryPlan(plan);
		assert (transaction.didCommit());
		printResults(transaction);
		return transaction.getResult();
		/*
		while (!transaction.didCommit()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		printResults(transaction);
		
		/*
		QueryOptimizer optimizer = new QueryOptimizer();
		optimizer.optimizePlan(queryPlan);
		
		ExecutionEngine engine = new ExecutionEngine();
		engine.runPlan(queryPlan);
		*/
	}
	
	public static Catalog catalog() {
		return getInstance().getCatalog();
	}
	
	public static Logger logger() {
		return getInstance().getLogger();
	}
	
	public static CoffeeDB getInstance() {
		if (_singleton == null) {
			_singleton = new CoffeeDB();
		}
		
		return _singleton;
	}
	
	public Catalog getCatalog() {
		return _catalog;
	}
	
	public Logger getLogger() {
		return _logger;
	}
	
	public void setConfig(Config config) {
		_config = config;
	}
	
	public void runTests() {
		runQuery("create table test (a int, b int);");
		runQuery("insert into test values (10, 20);");
		//runQuery("insert into test values (25, 20);");
		//runQuery("insert into test values (40, 10);");
		
		runQuery("create table test2 (c int, d int);");
		runQuery("insert into test2 values (10, 30);");
		runQuery("select * from test2, test where a = c;");
		
	}
	
	public Config getConfig() {
		assert (_config != null);
		return _config;
	}
	
	public static Config parseConfig(String[] args) {
		return new Config();
	}
	
	public static void usage() {
	}
	
	public static void main(String[] args) 
		throws InterruptedException {
		Config config = parseConfig(args);
		CoffeeDB database = CoffeeDB.getInstance();
		database.setConfig(config);
		database.runTests();
		database.shutdown();
	}
}
