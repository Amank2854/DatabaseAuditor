package databaseauditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import databaseauditor.Analyzer.Analyzer;
import databaseauditor.Database.Builder;

public class WorkBench {
    Utilities utils = new Utilities();

    void init() {
        try {
            List<Object> objs = utils.getModels();
            Builder builder = new Builder();
            builder.init(objs);

            // List<Object> selectedObj = new ArrayList<>();
            // Random rand = new Random();
            // selectedObj.add(objs.get(rand.nextInt(objs.size())));
            // System.out.println(selectedObj);

            Analyzer analyzer = new Analyzer();
            analyzer.create(objs, 100);
            
            analyzer.create(objs, 100);
            // analyzer.read(objs, 100);
            // analyzer.update(objs, 100);
            // analyzer.delete(objs, 100);
            
            analyzer.addRelationships();
            analyzer.query(200);

            analyzer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}