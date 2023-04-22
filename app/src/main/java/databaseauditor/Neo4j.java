package databaseauditor;

import databaseauditor.Database.Database;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.*;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.neo4j.driver.Values.parameters;

public class Neo4j implements Database {
    private Driver driver;
    String url = "bolt://localhost:7687";
    String user = "neo4j";
    String password = "database_auditor";
    @Override
    public boolean connect(String url, String username, String password) {
        driver = GraphDatabase.driver(url, AuthTokens.basic(user, password));
        return true;
    }
    @Override
    public void disconnect() {
        driver.close();
    }
    public <T> int insertOne(T obj) {
        Field[] fields = obj.getClass().getDeclaredFields();

        String label = obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1];
        List<String> propertyKey= new ArrayList<>();
        List<Object> propertyValue = new ArrayList<>();

        for (Field field : fields) {
            try {
                propertyKey.add(field.getName().toString());
                propertyValue.add(field.get(obj).toString());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                return -1;
            } catch (IllegalAccessException e) {
                System.out.println("Error: " + e.getMessage());
                return -1;
            }
        }

        String query = "CREATE (n:" + label + " {";
        for (int i = 0; i < propertyKey.size(); i++) {
            query = query + propertyKey.get(i) + ": \"" + propertyValue.get(i).toString() + "\"";
            if(i != propertyKey.size() - 1) {
                query = query + ", ";
            }
        }
        query = query + "});";

        System.out.println(query);

        try (Session session = driver.session()) {
            String finalQuery = query;
            session.writeTransaction(tx -> tx.run(finalQuery));
        }

        return 1;
    }

    @Override
    public <T> int updateMany(T obj, List<List<String>> params) {
        Field[] fields = obj.getClass().getDeclaredFields();

        String label = obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1];
        List<String> fieldNames = new ArrayList<String>();
        String updates = "", conditions = "";

        for (Field field : fields) {
            try {
                fieldNames.add(field.getName().toString());
                updates = updates + "n." + field.getName().toString() + " = ";
                updates = updates + "\"" + field.get(obj).toString() + "\", ";
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                return -1;
            } catch (IllegalAccessException e) {
                System.out.println("Error: " + e.getMessage());
                return -1;
            }
        }

        for (List<String> param : params) {
            if (fieldNames.contains(param.get(0))) {
                conditions = conditions + " " + param.get(0) + " : ";
                conditions = conditions + "\"" + param.get(1) + "\", ";
            } else {
                System.out.println("ERROR: Invalid paramater: " + param.get(0));
                return -1;
            }
        }

        updates = updates.substring(0, updates.length() - 2);
        conditions = conditions.substring(0, conditions.length() - 2);
        String query = "MATCH (n:" + label + " {" + conditions + "}) " + " set " + updates + ";";

        System.out.println(query);

        try (Session session = driver.session()) {
            String finalQuery = query;
            session.writeTransaction(tx -> tx.run(finalQuery));
        }

        return 1;
    }

    @Override
    public <T> int deleteMany(T obj, List<List<String>> params) {
        Field[] fields = obj.getClass().getDeclaredFields();

        String label = obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1];
        List<String> fieldNames = new ArrayList<String>();
        String conditions = "";

        for (Field field : fields) {
            try {
                fieldNames.add(field.getName().toString());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                return -1;
            }
        }

        for (List<String> param : params) {
            if (fieldNames.contains(param.get(0))) {
                conditions = conditions + " " + param.get(0) + " : ";
                conditions = conditions + "\"" + param.get(1) + "\", ";
            } else {
                System.out.println("ERROR: Invalid paramater: " + param.get(0));
                return -1;
            }
        }

        conditions = conditions.substring(0, conditions.length() - 2);
        String query = "MATCH (n:" + label + " {" + conditions + "}) " + " delete n;";

        System.out.println(query);

        try (Session session = driver.session()) {
            String finalQuery = query;
            session.writeTransaction(tx -> tx.run(finalQuery));
        }

        return 1;
    }

    public <T> int select(T obj, List<List<String>> params, List<String> reqCols){
        return 1;
    }
}