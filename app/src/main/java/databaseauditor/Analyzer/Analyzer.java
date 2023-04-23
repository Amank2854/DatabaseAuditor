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
    String output_dir = System.getProperty("user.dir") + "/src/charts/";
    int max_num = 10000, min_num = 1;

    public Analyzer() {
        postgres.connect(this.dotenv.get("POSTGRES_URL") + this.dotenv.get("DB_NAME"), this.dotenv.get("POSTGRES_USER"),
                this.dotenv.get("POSTGRES_PASSWORD"));
        mongo.connect(this.dotenv.get("MONGODB_URI"), "", "");
    }

    public void create(List<Object> entities, int numIterations) {
        List<String> entity_type = new ArrayList<String>();
        long[] postgres_times = new long[numIterations], postgres_memory = new long[numIterations];
        long[] mongo_times = new long[numIterations], mongo_memory = new long[numIterations];
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
            postgres_memory[i] = this.utils.getConsumedMemory(postgres, "insertOne", args, false);
            mongo_times[i] = this.utils.getElapsedTime(mongo, "insertOne", args, false);
            mongo_memory[i] = this.utils.getConsumedMemory(mongo, "insertOne", args, false);
            idx[i] = i + 1;
        }

        List<String> labels = Arrays.asList("PostgreSQL", "MongoDB");
        List<long[]> times = Arrays.asList(postgres_times, mongo_times);
        LineChart.plot(idx, times, labels, "Number Of Basic Insertions", "Time (ns)",
                "Execution Time", output_dir + "basic_insert_times.png");

        // List<long[]> memory = Arrays.asList(postgres_memory, mongo_memory);
        // LineChart.plot(idx, memory, labels, "Number Of Basic Insertions", "Memory (bytes)",
        //         "Memory Consumption", output_dir + "basic_insert_memory.png");
    }

    public void update(List<Object> entities, int numIterations) {
        List<String> entity_type = new ArrayList<String>();
        long[] postgres_times = new long[numIterations], postgres_memory = new long[numIterations];
        long[] mongo_times = new long[numIterations], mongo_memory = new long[numIterations];
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

            List<List<String>> conditions = new ArrayList<List<String>>();
            conditions.add(Arrays.asList(fields[0].getName().toString(),
                    Integer.toString((int) Math.floor(Math.random() * (max_num - min_num + 1) + min_num))));

            Object[] args = { entity, conditions };
            postgres_times[i] = this.utils.getElapsedTime(postgres, "updateMany", args, false);
            postgres_memory[i] = this.utils.getConsumedMemory(postgres, "updateMany", args, false);
            mongo_times[i] = this.utils.getElapsedTime(mongo, "updateMany", args, false);
            mongo_memory[i] = this.utils.getConsumedMemory(mongo, "updateMany", args, false);
            idx[i] = i + 1;
        }

        List<String> labels = Arrays.asList("PostgreSQL", "MongoDB");
        List<long[]> times = Arrays.asList(postgres_times, mongo_times);
        LineChart.plot(idx, times, labels, "Number Of Basic Updates", "Time (ns)",
                "Execution Time", output_dir + "basic_update_times.png");

        // List<long[]> memory = Arrays.asList(postgres_memory, mongo_memory);
        // LineChart.plot(idx, memory, labels, "Number Of Basic Updates", "Memory (bytes)",
        //         "Memory Consumption", output_dir + "basic_update_memory.png");
    }

    public void delete(List<Object> entities, int numIterations) {
        List<String> entity_type = new ArrayList<String>();
        long[] postgres_times = new long[numIterations], postgres_memory = new long[numIterations];
        long[] mongo_times = new long[numIterations], mongo_memory = new long[numIterations];
        long[] idx = new long[numIterations];

        for (int i = 0; i < numIterations; i++) {
            Object entity = entities.get((int) Math.floor(Math.random() * (entities.size())));
            entity_type.add(entity.getClass().getSimpleName());
            Field[] fields = entity.getClass().getDeclaredFields();

            List<List<String>> conditions = new ArrayList<List<String>>();
            conditions.add(Arrays.asList(fields[0].getName().toString(),
                    Integer.toString((int) Math.floor(Math.random() * (max_num - min_num + 1) + min_num))));

            Object[] args = { entity, conditions };
            postgres_times[i] = this.utils.getElapsedTime(postgres, "deleteMany", args, false);
            postgres_memory[i] = this.utils.getConsumedMemory(postgres, "deleteMany", args, false);
            mongo_times[i] = this.utils.getElapsedTime(mongo, "deleteMany", args, false);
            mongo_memory[i] = this.utils.getConsumedMemory(mongo, "deleteMany", args, false);
            idx[i] = i + 1;
        }

        List<String> labels = Arrays.asList("PostgreSQL", "MongoDB");
        List<long[]> times = Arrays.asList(postgres_times, mongo_times);
        LineChart.plot(idx, times, labels, "Number Of Basic Deletes", "Time (ns)",
                "Execution Time", output_dir + "basic_delete_times.png");

        // List<long[]> memory = Arrays.asList(postgres_memory, mongo_memory);
        // LineChart.plot(idx, memory, labels, "Number Of Basic Deletes", "Memory (bytes)",
        //         "Memory Consumption", output_dir + "basic_delete_memory.png");
    }

    public void read(List<Object> entities, int numIterations) {
        List<String> entity_type = new ArrayList<String>();
        long[] postgres_times = new long[numIterations], postgres_memory = new long[numIterations];
        long[] mongo_times = new long[numIterations], mongo_memory = new long[numIterations];
        long[] idx = new long[numIterations];

        for (int i = 0; i < numIterations; i++) {
            Object entity = entities.get((int) Math.floor(Math.random() * (entities.size())));
            entity_type.add(entity.getClass().getSimpleName());
            Field[] fields = entity.getClass().getDeclaredFields();

            List<List<String>> conditions = new ArrayList<List<String>>();
            conditions.add(Arrays.asList(fields[0].getName().toString(),
                    Integer.toString((int) Math.floor(Math.random() * (max_num - min_num + 1) + min_num))));

            List<String> columns = new ArrayList<String>();
            columns.add(fields[0].getName().toString());

            Object[] args = { entity, conditions, columns };
            postgres_times[i] = this.utils.getElapsedTime(postgres, "select", args, false);
            postgres_memory[i] = this.utils.getConsumedMemory(postgres, "select", args, false);
            mongo_times[i] = this.utils.getElapsedTime(mongo, "select", args, false);
            mongo_memory[i] = this.utils.getConsumedMemory(mongo, "select", args, false);
            idx[i] = i + 1;
        }

        List<String> labels = Arrays.asList("PostgreSQL", "MongoDB");
        List<long[]> times = Arrays.asList(postgres_times, mongo_times);
        LineChart.plot(idx, times, labels, "Number Of Basic Reads", "Time (ns)",
                "Execution Time", output_dir + "basic_read_times.png");

        // List<long[]> memory = Arrays.asList(postgres_memory, mongo_memory);
        // LineChart.plot(idx, memory, labels, "Number Of Basic Reads", "Memory (bytes)",
        //         "Memory Consumption", output_dir + "basic_read_memory.png");
    }
}