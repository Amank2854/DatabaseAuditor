package databaseauditor.Database;

import databaseauditor.Database.Database;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.summary.ResultSummary;
import org.neo4j.driver.summary.SummaryCounters;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.neo4j.driver.Values.parameters;

public class Neo4j implements Database {
    Driver driver = null;

    @Override
    public boolean connect(String url, String username, String password) throws Exception {
        if (driver != null) {
            return true;
        }

        driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
        return true;
    }

    @Override
    public void disconnect() throws Exception {
        driver.close();
    }

    public <T> int insertOne(T obj) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> propertyKey = new ArrayList<>();
        List<Object> propertyValue = new ArrayList<>();

        for (Field field : fields) {
            propertyKey.add(field.getName().toString());
            propertyValue.add(field.get(obj).toString());
        }

        String query = "CREATE (n:"
                + obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1] + " {";
        for (int i = 0; i < propertyKey.size(); i++) {
            query = query + propertyKey.get(i) + ": \"" + propertyValue.get(i).toString() + "\"";
            if (i != propertyKey.size() - 1) {
                query = query + ", ";
            }
        }

        query = query + "});";
        session.writeTransaction(tx -> tx.run(query));
        return 1;
    }

    @Override
    public <T> int updateMany(T obj, List<List<String>> params) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<String>();
        String updates = "", conditions = "";
        int noOfFields = fields.length;

        for (Field field : fields) {
            fieldNames.add(field.getName().toString());
            updates = updates + "n." + field.getName().toString() + " = ";
            updates = updates + "\"" + field.get(obj).toString() + "\", ";
        }

        for (List<String> param : params) {
            if (fieldNames.contains(param.get(0))) {
                conditions = conditions + " " + param.get(0) + " : ";
                conditions = conditions + "\"" + param.get(1) + "\", ";
            } else {
                System.out.println("INVALID PARAMETER: " + param.get(0));
                return -1;
            }
        }

        updates = updates.substring(0, updates.length() - 2);
        conditions = conditions.substring(0, conditions.length() - 2);
        String query = "MATCH (n:"
                + obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1] + " {"
                + conditions + "}) " + " set " + updates + ";";
        Result result = session.writeTransaction(tx -> tx.run(query));
        int nodesUpdated = (result.consume().counters().propertiesSet()) / noOfFields;
        return nodesUpdated;
    }

    @Override
    public <T> int deleteMany(T obj, List<List<String>> params) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<String>();
        String conditions = "";

        for (Field field : fields) {
            fieldNames.add(field.getName().toString());
        }

        for (List<String> param : params) {
            if (fieldNames.contains(param.get(0))) {
                conditions = conditions + " " + param.get(0) + " : ";
                conditions = conditions + "\"" + param.get(1) + "\", ";
            } else {
                System.out.println("INVALID PARAMETER: " + param.get(0));
                return -1;
            }
        }

        conditions = conditions.substring(0, conditions.length() - 2);
        String query = "MATCH (n:"
                + obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1] + " {"
                + conditions + "}) " + " delete n;";
        Result resultSummary = session.writeTransaction(tx -> tx.run(query));
        SummaryCounters counters = resultSummary.consume().counters();
        return counters.nodesDeleted();
    }

    public <T> int select(T obj, List<List<String>> params, List<String> reqCols) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<String>();
        String columns = "", conditions = "";

        for (Field field : fields) {
            fieldNames.add(field.getName().toString());
        }

        for (List<String> param : params) {
            if (fieldNames.contains(param.get(0))) {
                conditions = conditions + " " + param.get(0) + " : ";
                conditions = conditions + "\"" + param.get(1) + "\", ";
            } else {
                System.out.println("INVALID PARAMETER: " + param.get(0));
                return -1;
            }
        }

        for (String reqCol : reqCols) {
            if (fieldNames.contains(reqCol)) {
                columns = columns + "n." + reqCol + ", ";
            } else {
                System.out.println("INVALID COLUMN: " + reqCol);
                return -1;
            }
        }

        columns = columns.substring(0, columns.length() - 2);
        conditions = conditions.substring(0, conditions.length() - 2);
        String query = "MATCH (n:"
                + obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1] + " {"
                + conditions + "}) " + " return " + columns + ";";
        Result result = session.run(query);
        int count = 0;
        while (result.hasNext()) {
            Record record = result.next();
            count++;
        }
        return count;
    }
}