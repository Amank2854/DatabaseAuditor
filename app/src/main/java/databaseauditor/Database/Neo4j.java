package databaseauditor.Database;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.summary.SummaryCounters;

import databaseauditor.Utilities;

import org.neo4j.driver.*;

import java.lang.reflect.Field;
import java.util.*;

public class Neo4j implements Database {
    Session session = null;
    Utilities utils = new Utilities();

    @Override
    /**
     * Method to connect to the database
     * 
     * @param url      url of the database
     * @param username username for the database
     * @param password password for the database
     * @throws Exception exception if something went wrong
     */
    public void connect(String url, String username, String password) throws Exception {
        if (session != null) {
            return;
        }

        session = GraphDatabase.driver(url, AuthTokens.basic(username, password)).session();
    }

    @Override
    /**
     * Method to disconnect from the database
     * 
     * @throws Exception exception if something went wrong
     */
    public void disconnect() throws Exception {
        session = null;
    }

    @Override
    /**
     * Method to insert one record into the Neo4j database
     * 
     * @param <T> type of the object to be inserted into the database (model)
     * @param obj object to be inserted into the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
    public <T> int insertOne(T obj) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> propertyKey = new ArrayList<>();
        List<Object> propertyValue = new ArrayList<>();

        for (Field field : fields) {
            propertyKey.add(field.getName().toString());
            propertyValue.add(field.get(obj).toString());
        }

        String query = "CREATE (n:"
                + this.utils.camelToSnakeCase(
                        obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1])
                + " {";
        for (int i = 0; i < propertyKey.size(); i++) {
            query = query + propertyKey.get(i) + ": \"" + propertyValue.get(i).toString() + "\"";
            if (i != propertyKey.size() - 1) {
                query = query + ", ";
            }
        }

        query = query + "});";
        final String finalQuery = query;
        session.executeWrite(tx -> tx.run(finalQuery));
        return 1;
    }

    @Override
    /**
     * Method to update records in the Neo4j database
     * 
     * @param <T>    type of the object to be updated in the database (model)
     * @param obj    object to be updated in the database
     * @param params list of parameters to check for in the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
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
                + this.utils.camelToSnakeCase(
                        obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1])
                + " {"
                + conditions + "}) " + " set " + updates + ";";
        final String finalQuery = query;
        Result result = session.executeWrite(tx -> tx.run(finalQuery));
        int nodesUpdated = (result.consume().counters().propertiesSet()) / noOfFields;
        return nodesUpdated;
    }

    @Override
    /**
     * Method to delete records from the Neo4j database
     * 
     * @param <T>    type of the object to be deleted from the database (model)
     * @param obj    object to be deleted from the database
     * @param params list of parameters to check for in the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
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
                + this.utils.camelToSnakeCase(
                        obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1])
                + " {"
                + conditions + "}) " + " delete n;";
        final String finalQuery = query;
        Result resultSummary = session.executeWrite(tx -> tx.run(finalQuery));
        SummaryCounters counters = resultSummary.consume().counters();
        return counters.nodesDeleted();
    }

    @Override
    /**
     * Method to select records from the Neo4j database
     * 
     * @param <T>     type of the object to be selected from the database (model)
     * @param obj     an object of the model from which results will be selected
     *                from the database
     * @param params  list of parameters to check for in the database
     * @param reqCols list of attributes to be returned from the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
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
                + this.utils.camelToSnakeCase(
                        obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1])
                + " {"
                + conditions + "}) " + " return " + columns + ";";
        final String finalQuery = query;
        Result result = session.run(finalQuery);

        int count = 0;
        while (result.hasNext()) {
            result.next();
            count++;
        }
        return count;
    }
}