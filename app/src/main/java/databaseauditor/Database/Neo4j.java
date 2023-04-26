package databaseauditor.Database;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.summary.ResultSummary;
import org.neo4j.driver.summary.SummaryCounters;
import org.neo4j.driver.*;

import java.lang.reflect.Field;
import java.util.*;

import static org.neo4j.driver.Values.parameters;

public class Neo4j implements Database {
    Session session = null;

    @Override
    // Method to connect to the neo4j database
    public void connect(String url, String username, String password) throws Exception {
        if (session != null) {
            return;
        }

        session = GraphDatabase.driver(url, AuthTokens.basic(username, password)).session();
    }

    @Override
    // Method to disconnect from the neo4j database
    public void disconnect() throws Exception {
        session = null;
    }

    // Method to insert one record into the neo4j database
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
        final String finalQuery = query;
        session.writeTransaction(tx -> tx.run(finalQuery));
        return 1;
    }

    @Override
    // Method to update many records in the neo4j database
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
                throw new Exception("Invalid parameter: " + param.get(0));
            }
        }

        updates = updates.substring(0, updates.length() - 2);
        conditions = conditions.substring(0, conditions.length() - 2);
        String query = "MATCH (n:"
                + obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1] + " {"
                + conditions + "}) " + " set " + updates + ";";
        final String finalQuery = query;
        Result result = session.writeTransaction(tx -> tx.run(finalQuery));
        int nodesUpdated = (result.consume().counters().propertiesSet()) / noOfFields;
        return nodesUpdated;
    }

    @Override
    // Method to delete many records from the neo4j database
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
                throw new Exception("Invalid parameter: " + param.get(0));
            }
        }

        conditions = conditions.substring(0, conditions.length() - 2);
        String query = "MATCH (n:"
                + obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1] + " {"
                + conditions + "}) " + " delete n;";
        final String finalQuery = query;
        Result resultSummary = session.writeTransaction(tx -> tx.run(finalQuery));
        SummaryCounters counters = resultSummary.consume().counters();
        return counters.nodesDeleted();
    }

    // Method to select many records from the neo4j database
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
                throw new Exception("Invalid parameter: " + param.get(0));
            }
        }

        for (String reqCol : reqCols) {
            if (fieldNames.contains(reqCol)) {
                columns = columns + "n." + reqCol + ", ";
            } else {
                throw new Exception("Invalid parameter: " + reqCol);
            }
        }

        columns = columns.substring(0, columns.length() - 2);
        conditions = conditions.substring(0, conditions.length() - 2);
        String query = "MATCH (n:"
                + obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1] + " {"
                + conditions + "}) " + " return " + columns + ";";
        final String finalQuery = query;
        Result result = session.run(finalQuery);

        int count = 0;
        while (result.hasNext()) {
            Record record = result.next();
            count++;
        }
        return count;
    }
}