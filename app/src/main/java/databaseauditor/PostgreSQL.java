package databaseauditor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class PostgreSQL implements Database {
    static final String jdbcUrl = "jdbc:postgresql://localhost/dvdrental";
    static final String username = "ojassvi";
    static final String password = "ojassvi";
    Connection connectionObj = null;
    Utilities util = new Utilities();

    public boolean connect() {
        if (connectionObj != null) {
            return true;
        }

        try {
            connectionObj = DriverManager.getConnection(jdbcUrl, username, password);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public <T> boolean insert(T obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        String columns = "(";
        String values = "(";

        for (Field field : fields) {
            try {
                columns = columns + field.getName().toString() + ", ";
                if (field.getType().getName().equals("java.lang.String")
                        || field.getType().getName().equals("java.sql.Timestamp")) {
                    values = values + "'" + field.get(obj).toString() + "', ";
                } else {
                    values = values + field.get(obj).toString() + ", ";
                }
            } catch (IllegalArgumentException e1) {
                System.out.println("Error: " + e1.getMessage());
                return false;
            } catch (IllegalAccessException e1) {
                System.out.println("Error: " + e1.getMessage());
                return false;
            }
        }

        columns = columns.substring(0, columns.length() - 2) + ")";
        values = values.substring(0, values.length() - 2) + ")";
        String sql = "insert into " + util.camelToSnakeCase(
                obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1]) + " "
                + columns + " values " + values + ";";

        try {
            PreparedStatement stmt = connectionObj.prepareCall(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public <T> boolean update(T obj) {

        return true;
    }

    public <T> boolean delete(T obj) {

        return true;
    }

    public <T> boolean select(T obj) {

        return true;
    }
}