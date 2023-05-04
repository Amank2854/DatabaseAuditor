package databaseauditor.Analyzer;

import io.github.cdimascio.dotenv.Dotenv;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import databaseauditor.LineChart;
import databaseauditor.Utilities;
import databaseauditor.Analyzer.MongoDB.MongoDBAnalyzer;
import databaseauditor.Analyzer.Neo4j.Neo4jAnalyzer;
import databaseauditor.Analyzer.Postgres.PostgresAnalyzer;
import databaseauditor.Database.MongoDB;
import databaseauditor.Database.Neo4j;
import databaseauditor.Database.Postgres;

public class Analyzer {
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_RESET = "\u001B[0m";

    Dotenv dotenv = Dotenv.load();
    Postgres postgres = new Postgres();
    MongoDB mongo = new MongoDB();
    Neo4j neo = new Neo4j();
    Utilities utils = new Utilities();

    String outputDir = System.getProperty("user.dir") + "/src/charts/";
    int maxNum = 10000, minNum = 1;

    /**
     * Constructor for the Analyzer class to initialize the database connections
     * 
     * @throws Exception exception if something went wrong
     */
    public Analyzer() throws Exception {
        postgres.connect(this.dotenv.get("POSTGRES_URL") + this.dotenv.get("DB_NAME"),
                this.dotenv.get("POSTGRES_USER"),
                this.dotenv.get("POSTGRES_PASSWORD"));
        mongo.connect(this.dotenv.get("MONGODB_URI"), "", "");
        neo.connect(this.dotenv.get("NEO4J_URL"), this.dotenv.get("NEO4J_USER"),
                this.dotenv.get("NEO4J_PASSWORD"));
    }

    public void close() throws Exception {
        this.postgres.disconnect();
        this.mongo.disconnect();
        this.neo.disconnect();

        System.out.println("Analyzer closed successfully");
    }

    /**
     * Method to test the basic create operation for each database.
     * It selects a random entity from the list of entities at each iteration and
     * inserts a object which is also generated randomly in the selected entity.
     * This operation is performed for
     * numIterations times and the time and memory consumed for each iteration is
     * recorded for all the databases.
     * 
     * @param entities      list of entities to create
     * @param numIterations number of iterations to run
     * @throws Exception exception if something went wrong
     */
    public void create(List<Object> entities, int numIterations) throws Exception {
        List<String> entityType = new ArrayList<String>();
        for (int i = 0; i < entities.size(); i++) {
            entityType.add(entities.get(i).getClass().getSimpleName());
        }

        System.out.println("Creating " + numIterations
                + " record(s) in each database for the following possible entities:\n" + entityType);
        long[] postgresTimes = new long[numIterations], postgresMemory = new long[numIterations];
        long[] mongoTimes = new long[numIterations], mongoMemory = new long[numIterations];
        long[] neoTimes = new long[numIterations], neoMemory = new long[numIterations];
        long[] idx = new long[numIterations];

        for (int i = 0; i < numIterations; i++) {
            Object entity = entities.get((int) Math.floor(Math.random() * (entities.size())));
            Field[] fields = entity.getClass().getDeclaredFields();

            for (Field field : fields) {
                int random_int = (int) Math.floor(Math.random() * (maxNum - minNum + 1) + minNum);
                field.set(entity, Integer.toString(random_int));
            }

            Object[] args = { entity };
            postgresTimes[i] = this.utils.getElapsedTime(postgres, "insertOne", args, false);
            postgresMemory[i] = this.utils.getConsumedMemory(postgres, "insertOne", args, false);
            mongoTimes[i] = this.utils.getElapsedTime(mongo, "insertOne", args, false);
            mongoMemory[i] = this.utils.getConsumedMemory(mongo, "insertOne", args, false);
            neoTimes[i] = this.utils.getElapsedTime(neo, "insertOne", args, false);
            neoMemory[i] = this.utils.getConsumedMemory(neo, "insertOne", args, false);
            idx[i] = i + 1;
        }

        List<String> labels = Arrays.asList("PostgreSQL", "MongoDB", "Neo4j");
        List<long[]> times = Arrays.asList(postgresTimes, mongoTimes, neoTimes);
        LineChart.plot(idx, times, labels, "Number Of Basic Insertions", "Time (ns)",
                "Execution Time", outputDir + "basic_insert_times.png");

        List<long[]> memory = Arrays.asList(postgresMemory, mongoMemory, neoMemory);
        LineChart.plot(idx, memory, labels, "Number Of Basic Insertions", "Memory (bytes)",
                "Memory Consumption", outputDir + "basic_insert_memory.png");

        utils.writeResults(postgresTimes, mongoTimes, neoTimes, postgresMemory, mongoMemory, neoMemory,
                numIterations,
                "basic_insert", entities);
        System.out.println(ANSI_GREEN + "Creation operation(s) complete\n" + ANSI_RESET);
    }

    /**
     * Method to test the basic update operation for each database.
     * It selects a random entity from the list of entities at each iteration and
     * updates the object with the parameters which are also generated and selected
     * randomly in the selected entity.
     * This operation is performed for numIterations times and the time and memory
     * consumed for each iteration is recorded for all the databases.
     * 
     * @param entities      list of entities to update
     * @param numIterations number of iterations to run
     * @throws Exception exception if something went wrong
     */
    public void update(List<Object> entities, int numIterations) throws Exception {
        List<String> entityType = new ArrayList<String>();
        for (int i = 0; i < entities.size(); i++) {
            entityType.add(entities.get(i).getClass().getSimpleName());
        }

        System.out.println("Updating " + numIterations
                + " record(s) in each database for the following possible entities:\n" + entityType);
        long[] postgresTimes = new long[numIterations], postgresMemory = new long[numIterations];
        long[] mongoTimes = new long[numIterations], mongoMemory = new long[numIterations];
        long[] neoTimes = new long[numIterations], neoMemory = new long[numIterations];
        long[] idx = new long[numIterations];

        for (int i = 0; i < numIterations; i++) {
            Object entity = entities.get((int) Math.floor(Math.random() * (entities.size())));
            entityType.add(entity.getClass().getSimpleName());
            Field[] fields = entity.getClass().getDeclaredFields();

            for (Field field : fields) {
                int random_int = (int) Math.floor(Math.random() * (maxNum - minNum + 1) + minNum);
                field.set(entity, Integer.toString(random_int));
            }

            List<List<String>> conditions = new ArrayList<List<String>>();
            conditions.add(Arrays.asList(fields[0].getName().toString(),
                    Integer.toString((int) Math
                            .floor(Math.random() * (maxNum - minNum + 1) + minNum))));

            Object[] args = { entity, conditions };
            postgresTimes[i] = this.utils.getElapsedTime(postgres, "updateMany", args, false);
            postgresMemory[i] = this.utils.getConsumedMemory(postgres, "updateMany", args, false);
            mongoTimes[i] = this.utils.getElapsedTime(mongo, "updateMany", args, false);
            mongoMemory[i] = this.utils.getConsumedMemory(mongo, "updateMany", args, false);
            neoTimes[i] = this.utils.getElapsedTime(neo, "updateMany", args, false);
            neoMemory[i] = this.utils.getConsumedMemory(neo, "updateMany", args, false);
            idx[i] = i + 1;
        }

        List<String> labels = Arrays.asList("PostgreSQL", "MongoDB", "Neo4j");
        List<long[]> times = Arrays.asList(postgresTimes, mongoTimes, neoTimes);
        LineChart.plot(idx, times, labels, "Number Of Basic Updates", "Time (ns)",
                "Execution Time", outputDir + "basic_update_times.png");

        List<long[]> memory = Arrays.asList(postgresMemory, mongoMemory, neoMemory);
        LineChart.plot(idx, memory, labels, "Number Of Basic Updates", "Memory (bytes)",
                "Memory Consumption", outputDir + "basic_update_memory.png");

        utils.writeResults(postgresTimes, mongoTimes, neoTimes, postgresMemory, mongoMemory, neoMemory,
                numIterations,
                "basic_update", entities);
        System.out.println(ANSI_GREEN + "Update operation(s) complete\n" + ANSI_RESET);
    }

    /**
     * Method to test the basic delete operation for each database.
     * It selects a random entity from the list of entities at each iteration and
     * deletes the object with the parameters which are also generated and selected
     * randomly in the selected entity.
     * This operation is performed for numIterations times and the time and memory
     * consumed for each iteration is recorded for all the databases.
     * 
     * @param entities      list of entities to delete
     * @param numIterations number of iterations to run
     * @throws Exception exception if something went wrong
     */
    public void delete(List<Object> entities, int numIterations) throws Exception {
        List<String> entityType = new ArrayList<String>();
        for (int i = 0; i < entities.size(); i++) {
            entityType.add(entities.get(i).getClass().getSimpleName());
        }

        System.out.println("Deleting " + numIterations
                + " record(s) from each database for the following possible entities:\n" + entityType);
        long[] postgresTimes = new long[numIterations], postgresMemory = new long[numIterations];
        long[] mongoTimes = new long[numIterations], mongoMemory = new long[numIterations];
        long[] neoTimes = new long[numIterations], neoMemory = new long[numIterations];
        long[] idx = new long[numIterations];

        for (int i = 0; i < numIterations; i++) {
            Object entity = entities.get((int) Math.floor(Math.random() * (entities.size())));
            entityType.add(entity.getClass().getSimpleName());
            Field[] fields = entity.getClass().getDeclaredFields();

            List<List<String>> conditions = new ArrayList<List<String>>();
            conditions.add(Arrays.asList(fields[0].getName().toString(),
                    Integer.toString((int) Math
                            .floor(Math.random() * (maxNum - minNum + 1) + minNum))));

            Object[] args = { entity, conditions };
            postgresTimes[i] = this.utils.getElapsedTime(postgres, "deleteMany", args, false);
            postgresMemory[i] = this.utils.getConsumedMemory(postgres, "deleteMany", args, false);
            mongoTimes[i] = this.utils.getElapsedTime(mongo, "deleteMany", args, false);
            mongoMemory[i] = this.utils.getConsumedMemory(mongo, "deleteMany", args, false);
            neoTimes[i] = this.utils.getElapsedTime(neo, "deleteMany", args, false);
            neoMemory[i] = this.utils.getConsumedMemory(neo, "deleteMany", args, false);
            idx[i] = i + 1;
        }

        List<String> labels = Arrays.asList("PostgreSQL", "MongoDB", "Neo4j");
        List<long[]> times = Arrays.asList(postgresTimes, mongoTimes, neoTimes);
        LineChart.plot(idx, times, labels, "Number Of Basic Deletes", "Time (ns)",
                "Execution Time", outputDir + "basic_delete_times.png");

        List<long[]> memory = Arrays.asList(postgresMemory, mongoMemory, neoMemory);
        LineChart.plot(idx, memory, labels, "Number Of Basic Deletes", "Memory (bytes)",
                "Memory Consumption", outputDir + "basic_delete_memory.png");

        utils.writeResults(postgresTimes, mongoTimes, neoTimes, postgresMemory, mongoMemory, neoMemory,
                numIterations,
                "basic_delete", entities);
        System.out.println(ANSI_GREEN + "Delete operation(s) complete\n" + ANSI_RESET);
    }

    /**
     * Method to test the basic read operation for each database.
     * It selects a random entity from the list of entities at each iteration and
     * reads the object with the parameters which are also generated and selected
     * randomly in the selected entity.
     * This operation is performed for numIterations times and the time and memory
     * consumed for each iteration is recorded for all the databases.
     * 
     * @param entities      list of entities to read
     * @param numIterations number of iterations to run
     * @throws Exception exception if something went wrong
     */
    public void read(List<Object> entities, int numIterations) throws Exception {
        List<String> entityType = new ArrayList<String>();
        for (int i = 0; i < entities.size(); i++) {
            entityType.add(entities.get(i).getClass().getSimpleName());
        }

        System.out.println("Reading " + numIterations
                + " record(s) from each database for the following possible entities:\n" + entityType);
        long[] postgresTimes = new long[numIterations], postgresMemory = new long[numIterations];
        long[] mongoTimes = new long[numIterations], mongoMemory = new long[numIterations];
        long[] neoTimes = new long[numIterations], neoMemory = new long[numIterations];
        long[] idx = new long[numIterations];

        for (int i = 0; i < numIterations; i++) {
            Object entity = entities.get((int) Math.floor(Math.random() * (entities.size())));
            entityType.add(entity.getClass().getSimpleName());
            Field[] fields = entity.getClass().getDeclaredFields();

            List<List<String>> conditions = new ArrayList<List<String>>();
            conditions.add(Arrays.asList(fields[0].getName().toString(),
                    Integer.toString((int) Math
                            .floor(Math.random() * (maxNum - minNum + 1) + minNum))));

            List<String> columns = new ArrayList<String>();
            columns.add(fields[0].getName().toString());

            Object[] args = { entity, conditions, columns };
            postgresTimes[i] = this.utils.getElapsedTime(postgres, "select", args, false);
            postgresMemory[i] = this.utils.getConsumedMemory(postgres, "select", args, false);
            mongoTimes[i] = this.utils.getElapsedTime(mongo, "select", args, false);
            mongoMemory[i] = this.utils.getConsumedMemory(mongo, "select", args, false);
            neoTimes[i] = this.utils.getElapsedTime(neo, "select", args, false);
            neoMemory[i] = this.utils.getConsumedMemory(neo, "select", args, false);
            idx[i] = i + 1;
        }

        List<String> labels = Arrays.asList("PostgreSQL", "MongoDB", "Neo4j");
        List<long[]> times = Arrays.asList(postgresTimes, mongoTimes, neoTimes);
        LineChart.plot(idx, times, labels, "Number Of Basic Reads", "Time (ns)",
                "Execution Time", outputDir + "basic_read_times.png");

        List<long[]> memory = Arrays.asList(postgresMemory, mongoMemory, neoMemory);
        LineChart.plot(idx, memory, labels, "Number Of Basic Reads", "Memory (bytes)",
                "Memory Consumption", outputDir + "basic_read_memory.png");

        utils.writeResults(postgresTimes, mongoTimes, neoTimes, postgresMemory, mongoMemory, neoMemory,
                numIterations,
                "basic_read", entities);
        System.out.println(ANSI_GREEN + "Read operation(s) complete\n" + ANSI_RESET);
    }

    /**
     * Method to test the databases against specific queries.
     * It selects a random entity from the list of entities at each iteration and
     * performs a complicated specific query in the database. This selected query
     * involves joins, aggregations, etc., and is same for all the databases.
     * This operation is performed for numIterations times and the time and memory
     * consumed for each iteration is recorded for all the databases.
     * 
     * @param numIterations number of iterations to run
     * @throws Exception exception if something went wrong
     */
    public void query(int numIterations) throws Exception {
        PostgresAnalyzer postgresAnalyzer = new PostgresAnalyzer();
        MongoDBAnalyzer mongoDBAnalyzer = new MongoDBAnalyzer();
        Neo4jAnalyzer neo4jAnalyzer = new Neo4jAnalyzer();

        System.out.println("Executing " + numIterations + " specific queries for each database");
        long[] idx = new long[numIterations];
        long[] queryTypes = new long[numIterations];
        long[] postgresTimes = new long[numIterations], postgresMemory = new long[numIterations];
        long[] mongoTimes = new long[numIterations], mongoMemory = new long[numIterations];
        long[] neoTimes = new long[numIterations], neoMemory = new long[numIterations];

        for (int i = 0; i < numIterations; i++) {
            idx[i] = i;
            List<String> args = new ArrayList<String>();
            int id = (int) Math.floor(Math.random() * postgresAnalyzer.queries.size());
            queryTypes[i] = id;

            for (int j = 0; j < postgresAnalyzer.argLens.get(id); j++) {
                int random_int = (int) Math.floor(Math.random() * (maxNum - minNum + 1) + minNum);
                args.add(Integer.toString(random_int));
            }

            Object[] params = { id, args };
            postgresTimes[i] = utils.getElapsedTime(postgresAnalyzer, "query", params, false);
            postgresMemory[i] = utils.getConsumedMemory(postgresAnalyzer, "query", params, false);
            mongoTimes[i] = utils.getElapsedTime(mongoDBAnalyzer, "query", params, false);
            mongoMemory[i] = utils.getConsumedMemory(mongoDBAnalyzer, "query", params, false);
            // neoTimes[i] = utils.getElapsedTime(neo4jAnalyzer, "query", params, false);
            // neoMemory[i] = utils.getConsumedMemory(neo4jAnalyzer, "query", params,
            // false);
        }

        List<String> labels = Arrays.asList("PostgreSQL", "MongoDB", "Neo4j");
        List<long[]> times = Arrays.asList(postgresTimes, mongoTimes, neoTimes);
        LineChart.plot(idx, times, labels, "Number Of Insertions", "Time (ns)",
                "Execution Time", outputDir + "specific_query_times.png");

        List<long[]> memory = Arrays.asList(postgresMemory, mongoMemory, neoMemory);
        LineChart.plot(idx, memory, labels, "Number Of Insertions", "Memory (bytes)",
                "Memory Consumption", outputDir + "specific_query_memory.png");

        List<Object> entities = new ArrayList<Object>();
        utils.writeResults(postgresTimes, mongoTimes, neoTimes, postgresMemory, mongoMemory, neoMemory,
                numIterations,
                "specific_query", entities);
        System.out.println(ANSI_GREEN + "Specific query operation(s) complete\n" + ANSI_RESET);
    }

    /**
     * Method to add the relationships between the entities in the databases.
     * 
     * @throws Exception exception if something went wrong
     */
    public void addRelationships() throws Exception {
        Neo4jAnalyzer neo4jAnalyzer = new Neo4jAnalyzer();
        neo4jAnalyzer.createRelationships(utils.getRelationships());
    }
}