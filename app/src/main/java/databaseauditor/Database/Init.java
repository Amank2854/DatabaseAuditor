package databaseauditor.Database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import databaseauditor.Utilities;
import io.github.cdimascio.dotenv.Dotenv;


public class Init {
    Dotenv dotenv = Dotenv.load();
    Utilities utils = new Utilities();
    String db_name = this.dotenv.get("DB_NAME");

    public void postgreSQL(List<Object> tables) {
        String url = "jdbc:postgresql://localhost/postgres";
        String username = "postgres";
        String password = "postgres";
        Connection conn;

        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        try {
            conn.createStatement().execute("drop database if exists " + this.db_name);
            conn.createStatement().execute("create database " + this.db_name);

            url = "jdbc:postgresql://localhost/" + this.db_name;
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        for (Object table : tables) {
            try {
                Field[] fields = table.getClass().getDeclaredFields();
                String sql = "create table " + this.utils.camelToSnakeCase(
                        table.getClass().getName().split("\\.")[table.getClass().getName().split("\\.").length - 1])
                        + " (";

                for (Field field : fields) {
                    sql = sql + field.getName().toString() + " varchar(255), ";
                }

                sql = sql.substring(0, sql.length() - 2) + ")";
                conn.createStatement().execute(sql);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }
        }

        url = "jdbc:postgresql://localhost/" + this.db_name;
        PostgreSQL postgres = new PostgreSQL();
        postgres.connect(url, username, password);
        for (Object table : tables) {
            postgres.insertOne(table);
        }

        System.out.println("PostgreSQL database created successfully");
    }

    public void mongoDB(List<Object> tables) {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        String uri = "mongodb://localhost:27017";

        try {
            @SuppressWarnings("resource")
            MongoClient mongo = MongoClients.create(uri);
            mongo.getDatabase(db_name);
            MongoDatabase db = mongo.getDatabase(db_name);
            db.drop();

            db = mongo.getDatabase(db_name);
            for (Object table : tables) {
                db.createCollection(this.utils.camelToSnakeCase(
                        table.getClass().getName().split("\\.")[table.getClass().getName().split("\\.").length - 1]));
            }

            MongoDB mongodb = new MongoDB();
            mongodb.connect(uri, uri, uri);
            for (Object table : tables) {
                mongodb.insertOne(table);
            }

            System.out.println("MongoDB database created successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    public void neo4j() {
        // TODO
    }
}