package databaseauditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import databaseauditor.Analyzer.Analyzer;
import databaseauditor.Database.Builder;

public class WorkBench {
    Utilities utils = new Utilities();

    void init() {
        Builder builder = new Builder();
        try {
            List<Object> objs = utils.getModels();

            List<Object> ob1 = new ArrayList<>();
            Random rand = new Random();
            ob1.add(objs.get(rand.nextInt(objs.size())));
            // System.out.println(ob1);

            Analyzer analyzer = new Analyzer();
            analyzer.create(ob1, 10000);
            
            // analyzer.create(ob1, 10000);
            analyzer.read(ob1, 10000);
            analyzer.update(ob1, 10000);
            analyzer.delete(ob1, 10000);
            
            // analyzer.query(200);

            analyzer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}