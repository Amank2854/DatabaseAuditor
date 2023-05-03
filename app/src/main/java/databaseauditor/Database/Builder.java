package databaseauditor.Database;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.*;

import databaseauditor.Utilities;

public class Builder {
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_RESET = "\u001B[0m";

    Dotenv dotenv = Dotenv.load();
    Utilities utils = new Utilities();
    String db_name = this.dotenv.get("DB_NAME");

    // Method to initialize all the databases
    public void init(List<Object> objs) throws Exception {
        this.postgres(objs);
        this.mongoDB(objs);
        this.neo4j(objs);
        
        Files.deleteIfExists(Paths.get(System.getProperty("user.dir")
        + "/src/results/results.txt"));
        File directory = new File(System.getProperty("user.dir")
        + "/src/charts");
        for (File file: Objects.requireNonNull(directory.listFiles())) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }

        System.out.println(ANSI_GREEN + "Database setup complete\n" + ANSI_RESET);
    }

    // Method to intialize the postgres database
    public void postgres(List<Object> tables) throws Exception {
        String url = this.dotenv.get("POSTGRES_URL") + "postgres";
        String username = this.dotenv.get("POSTGRES_USER");
        String password = this.dotenv.get("POSTGRES_PASSWORD");
        Connection conn;

        conn = DriverManager.getConnection(url, username, password);
        conn.createStatement().execute("drop database if exists " + this.db_name);
        conn.createStatement().execute("create database " + this.db_name);

        url = this.dotenv.get("POSTGRES_URL") + this.db_name;
        conn = DriverManager.getConnection(url, username, password);

        for (Object table : tables) {
            Field[] fields = table.getClass().getDeclaredFields();
            String sql = "create table " + this.utils.camelToSnakeCase(
                    table.getClass().getName().split("\\.")[table.getClass().getName().split("\\.").length - 1])
                    + " (";

            for (Field field : fields) {
                sql = sql + field.getName().toString() + " varchar(255), ";
            }

            sql = sql.substring(0, sql.length() - 2) + ")";
            conn.createStatement().execute(sql);
        }

        Postgres postgres = new Postgres();
        postgres.connect(url, username, password);
        for (Object table : tables) {
            postgres.insertOne(table);
        }

        System.out.println(ANSI_GREEN + "PostgreSQL database initialized successfully" + ANSI_RESET);
    }

    // Method to initialize the mongodb database
    public void mongoDB(List<Object> tables) throws Exception {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        String uri = this.dotenv.get("MONGODB_URI");

        try (MongoClient mongo = MongoClients.create(uri)) {
            mongo.getDatabase(db_name);
            MongoDatabase db = mongo.getDatabase(db_name);
            db.drop();

            db = mongo.getDatabase(db_name);
            for (Object table : tables) {
                db.createCollection(this.utils.camelToSnakeCase(
                        table.getClass().getName().split("\\.")[table.getClass().getName().split("\\.").length - 1]));
            }
        }
        MongoDB mongodb = new MongoDB();
        mongodb.connect(uri, uri, uri);
        for (Object table : tables) {
            mongodb.insertOne(table);
        }

        System.out.println(ANSI_GREEN + "MongoDB database initialized successfully" + ANSI_RESET);
    }

    // Method to initialize the neo4j database
    public void neo4j(List<Object> nodes) throws Exception {
        String url = this.dotenv.get("NEO4J_URL");
        String username = this.dotenv.get("NEO4J_USER");
        String password = this.dotenv.get("NEO4J_PASSWORD");

        final String finalQuery = "match (n) detach delete n";
        Session session = GraphDatabase.driver(url, AuthTokens.basic(username, password)).session();
        session.run(finalQuery);

        Neo4j neo = new Neo4j();
        neo.connect(url, username, password);
        for (Object node : nodes) {
            neo.insertOne(node);
        }

        System.out.println(ANSI_GREEN + "Neo4j database initialized successfully" + ANSI_RESET);
    }
}