package databaseauditor;

import java.util.List;

import databaseauditor.Analyzer.Analyzer;
import databaseauditor.Database.Builder;

public class WorkBench {
    Utilities utils = new Utilities();

    void init() {
        Builder builder = new Builder();
        try {
            List<Object> objs = utils.getModels();
            builder.init(objs);

            Analyzer analyzer = new Analyzer();
            analyzer.create(objs, 10000);
            analyzer.read(objs, 200);
            analyzer.update(objs, 200);
            analyzer.delete(objs, 200);
            analyzer.query(1000);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}