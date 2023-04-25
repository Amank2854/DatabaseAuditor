package databaseauditor;

import java.util.List;

import databaseauditor.Analyzer.Analyzer;
import databaseauditor.Database.Init;

public class WorkBench {
    Utilities utils = new Utilities();

    void init() {
        Init init = new Init();
        try {
            List<Object> objs = utils.getInstances();
            init.postgreSQL(objs);
            init.mongoDB(objs);
            init.neo4j(objs);
            Analyzer analyzer = new Analyzer();
            analyzer.create(objs, 200);
            analyzer.read(objs, 200);
            analyzer.update(objs, 200);
            analyzer.delete(objs, 200);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        // init.mongoDB(instances.data);
    }
}