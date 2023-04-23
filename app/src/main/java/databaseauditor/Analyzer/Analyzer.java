package databaseauditor.Analyzer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import databaseauditor.LineChart;
import databaseauditor.Utilities;
import databaseauditor.Database.MongoDB;
import databaseauditor.Database.PostgreSQL;
import io.github.cdimascio.dotenv.Dotenv;

public class Analyzer {
    Dotenv dotenv = Dotenv.load();
    PostgreSQL postgres = new PostgreSQL();
    MongoDB mongo = new MongoDB();
    Utilities utils = new Utilities();
    int max_num = 10000, min_num = 1;

    public void create(List<Object> entities, int numIterations) {
        postgres.connect(this.dotenv.get("POSTGRES_URL") + this.dotenv.get("DB_NAME"), this.dotenv.get("POSTGRES_USER"),
                this.dotenv.get("POSTGRES_PASSWORD"));
        mongo.connect(this.dotenv.get("MONGODB_URI"), "", "");

        List<String> entity_type = new ArrayList<String>();
        long[] postgres_times = new long[numIterations];
        long[] mongo_times = new long[numIterations];
        long[] idx = new long[numIterations];
        for (int i = 0; i < numIterations; i++) {
            Object entity = entities.get((int) Math.floor(Math.random() * (entities.size())));
            entity_type.add(entity.getClass().getSimpleName());
            Field[] fields = entity.getClass().getDeclaredFields();

            for (Field field : fields) {
                try {
                    int random_int = (int) Math.floor(Math.random() * (max_num - min_num + 1) + min_num);
                    field.set(entity, Integer.toString(random_int));
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    return;
                } catch (IllegalAccessException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }

            Object[] args = { entity };
            postgres_times[i] = this.utils.getElapsedTime(postgres, "insertOne", args, false);
            mongo_times[i] = this.utils.getElapsedTime(mongo, "insertOne", args, false);
            idx[i] = i + 1;
        }

        // for (int i = 0; i < numIterations; i++) {
        // System.out.println("Entity: " + entity_type.get(i));
        // System.out.println("PostgreSQL: " + postgres_times[i] + "ns");
        // System.out.println("MongoDB: " + mongo_times[i] + "ns\n");
        // }

        List<String> labels = Arrays.asList("PostgreSQL", "MongoDB");
        List<long[]> times = Arrays.asList(postgres_times, mongo_times);
        LineChart.plot(idx, times, labels, "Number Of Basic Insertions", "Time (ns)",
                "Execution Time");

        postgres.disconnect();
        mongo.disconnect();
    }
}