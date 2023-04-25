package databaseauditor;

import databaseauditor.Database.Init;
import databaseauditor.Model.Instances;

public class WorkBench {
    void init() {
        Init init = new Init();
        try {
            Instances instances = new Instances();
            init.postgreSQL(instances.data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        // init.mongoDB(instances.data);
        // Analyzer analyzer = new Analyzer();
        // analyzer.create(instances.data, 200);
        // analyzer.update(instances.data, 200);
        // analyzer.delete(instances.data, 200);
        // analyzer.read(instances.data, 200);
    }
}