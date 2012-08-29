package coffeedb;

public class CoffeeDB {
	static CoffeeDB _singleton = null;
	private Config _config = null;
	private Catalog _catalog;
	
	private CoffeeDB() {
		_catalog = new Catalog();
	}
	
	public void test() {
		Catalog catalog = CoffeeDB.getInstance().getCatalog();
		Table table = new Table("TestTable", null);
		catalog.addTable(table);
	}
	
	public void runQuery(String query) {
		SqlParser parser = new SqlParser(query);
		QueryPlan plan = parser.generateQueryPlan();
		
		ExecutionEngine engine = new ExecutionEngine();
		engine.runPlan(plan);
		
		/*
		QueryOptimizer optimizer = new QueryOptimizer();
		optimizer.optimizePlan(queryPlan);
		
		ExecutionEngine engine = new ExecutionEngine();
		engine.runPlan(queryPlan);
		*/
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
	
	public static void main(String[] args) {
		Config config = parseConfig(args);
		CoffeeDB database = CoffeeDB.getInstance();
		database.setConfig(config);
		//database.test();
		database.runQuery("create table test (a int, b int)");
		//database.runQuery("select * from test;");
		//database.runQuery("insert into test values (10, 20);");
	}
}
