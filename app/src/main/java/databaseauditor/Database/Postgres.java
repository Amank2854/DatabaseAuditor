package databaseauditor.Database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import databaseauditor.Utilities;

public class Postgres implements Database {
    Connection conn = null;
    Utilities util = new Utilities();

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
        if (this.conn != null) {
            return;
        }

        this.conn = DriverManager.getConnection(url, username, password);
    }

    @Override
    /**
     * Method to disconnect from the database
     * 
     * @throws Exception exception if something went wrong
     */
    public void disconnect() throws Exception {
        this.conn.close();
    }

    @Override
    /**
     * Method to insert one record into the Postgres database
     * 
     * @param <T> type of the object to be inserted into the database (model)
     * @param obj object to be inserted into the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
    public <T> int insertOne(T obj) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        String columns = "(", values = "(";

        for (Field field : fields) {
            columns = columns + field.getName().toString() + ", ";
            values = values + "'" + field.get(obj).toString() + "', ";
        }

        columns = columns.substring(0, columns.length() - 2) + ")";
        values = values.substring(0, values.length() - 2) + ")";
        String sql = "insert into " + this.util.camelToSnakeCase(
                obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1]) + " "
                + columns + " values " + values + ";";

        PreparedStatement stmt = this.conn.prepareCall(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        stmt.execute();
        return 1;
    }

    @Override
    /**
     * Method to update records in the Postgres database
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

        for (Field field : fields) {
            fieldNames.add(field.getName().toString());
            updates = updates + field.getName().toString() + " = ";
            updates = updates + "'" + field.get(obj).toString() + "', ";
        }

        for (List<String> param : params) {
            if (fieldNames.contains(param.get(0))) {
                conditions = conditions + " " + param.get(0) + " = ";
                conditions = conditions + "'" + param.get(1) + "' and";
            } else {
                throw new Exception("\nSomething went wrong: Invalid parameter " + param.get(0) + "\n");
            }
        }

        updates = updates.substring(0, updates.length() - 2);
        conditions = conditions.substring(0, conditions.length() - 4);
        String sql = "update " + this.util.camelToSnakeCase(
                obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1]) + " set "
                + updates + " where" + conditions + ";";

        PreparedStatement stmt = this.conn.prepareCall(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        return stmt.executeUpdate();
    }

    @Override
    /**
     * Method to delete records from the Postgres database
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
                conditions = conditions + " " + param.get(0) + " = ";
                conditions = conditions + "'" + param.get(1) + "' and";
            } else {
                throw new Exception("\nSomething went wrong: Invalid parameter " + param.get(0) + "\n");
            }
        }

        conditions = conditions.substring(0, conditions.length() - 4);
        String sql = "delete from " + this.util.camelToSnakeCase(
                obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1]) + " where"
                + conditions + ";";

        PreparedStatement stmt = this.conn.prepareCall(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        return stmt.executeUpdate();
    }

    @Override
        /**
     * Method to select records from the Postgres database
     * 
     * @param <T>     type of the object to be selected from the database (model)
     * @param obj     an object of the model from which results will be selected from the database
     * @param params  list of parameters to check for in the database
     * @param reqCols list of attributes to be selected from the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
    public <T> int select(T obj, List<List<String>> params, List<String> reqCols) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<String>();
        String columns = "", conditions = "";

        for (Field field : fields) {
            try {
                fieldNames.add(field.getName().toString());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return -1;
            }
        }

        for (List<String> param : params) {
            if (fieldNames.contains(param.get(0))) {
                conditions = conditions + " " + param.get(0) + " = ";
                conditions = conditions + "'" + param.get(1) + "' and";
            } else {
                throw new Exception("\nSomething went wrong: Invalid parameter " + param.get(0) + "\n");
            }
        }

        for (String reqCol : reqCols) {
            if (fieldNames.contains(reqCol)) {
                columns = columns + reqCol + ", ";
            } else {
                throw new Exception("\nSomething went wrong: Invalid parameter " + reqCol + "\n");
            }
        }

        columns = columns.substring(0, columns.length() - 2);
        conditions = conditions.substring(0, conditions.length() - 4);
        String sql = "select " + columns + " from " + this.util.camelToSnakeCase(
                obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1]) + " where"
                + conditions + ";";

        PreparedStatement stmt = this.conn.prepareCall(sql,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet result = stmt.executeQuery();

        int count = 0;
        if (result.last()) {
            count = result.getRow();
            result.beforeFirst();
        }
        return count;
    }
}