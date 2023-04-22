package databaseauditor;

import databaseauditor.Database.Init;
import databaseauditor.Models.Instances;

public class WorkBench {
    void init() {
        Init init = new Init();
        Instances instances = new Instances();
        init.postgreSQL(instances.data);
        init.mongoDB(instances.data);
    }
}