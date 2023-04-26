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
    // Method to connect to the postgres database
    public void connect(String url, String username, String password) throws Exception {
        if (this.conn != null) {
            return;
        }

        this.conn = DriverManager.getConnection(url, username, password);
    }

    @Override
    // Method to disconnect from the postgres database
    public void disconnect() throws Exception {
        this.conn.close();
    }

    @Override
    // Method to insert one record into the postgres database
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
    // Method to update many records in the postgres database
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
    // Method to delete many records from the postgres database
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
    // Method to select many records from the postgres database
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