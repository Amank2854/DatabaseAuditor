package databaseauditor;

import databaseauditor.Analyzer.Analyzer;
import databaseauditor.Database.Init;
import databaseauditor.Model.Instances;

public class WorkBench {
    void init() {
        Init init = new Init();
        Instances instances = new Instances();
        init.postgreSQL(instances.data);
        init.mongoDB(instances.data);

        Analyzer analyzer = new Analyzer();
        analyzer.create(instances.data, 200);
    }
}