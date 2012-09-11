package coffeedb;

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
		for (Tuple tuple : transaction.getResult()) {
			System.out.println(tuple);
		}
	}
	
	public void snapshot() {
		getLogger().snapshot(this);
	}
	
	public void recoverFromLog() {
		getLogger().recoverFromSnapshot(this);
	}
	
	public void runQuery(String query) {
		SqlParser parser = new SqlParser(query);
		QueryPlan plan = parser.generateQueryPlan();
		
		Transaction transaction = _engine.executeQueryPlan(plan);
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
		//database.test();
		database.runQuery("create table test (a int, b int)");
		database.runQuery("insert into test values (10, 20);");
		database.runQuery("select * from test;");
		database.snapshot();
		
		Catalog catalog = database.getCatalog();
		Schema testSchema = catalog.getTable("test").getSchema();
		
		database.reset();
		database.recoverFromLog();
		
		Schema recoverSchema = catalog.getTable("test").getSchema();
		assert (testSchema.equals(recoverSchema));
		
		database.runQuery("select * from test;");
		database.shutdown();
		
		/*
		database.runQuery("insert into test values (10, 20);");
		database.runQuery("select * from test;");
		
		System.out.println("Running where");
		database.runQuery("select * from test where test.a < 15");
		database.shutdown();
		*/
	}

	}
