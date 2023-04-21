package databaseauditor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

public class PostgreSQL implements Database {
    Dotenv dotenv = Dotenv.load();
    final String jdbcUrl = this.dotenv.get("POSTGRES_URL");
    final String username = this.dotenv.get("POSTGRES_USER");
    final String password = this.dotenv.get("POSTGRES_PASSWORD");
    Connection connectionObj = null;
    Utilities util = new Utilities();

    @Override
    public boolean connect() {
        if (this.connectionObj != null) {
            return true;
        }

        try {
            this.connectionObj = DriverManager.getConnection(this.jdbcUrl, this.username, this.password);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void disconnect() {
        if (this.connectionObj != null) {
            try {
                this.connectionObj.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public <T> int insertOne(T obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        String columns = "(", values = "(";

        for (Field field : fields) {
            try {
                columns = columns + field.getName().toString() + ", ";
                values = values + "'" + field.get(obj).toString() + "', ";
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                return -1;
            } catch (IllegalAccessException e) {
                System.out.println("Error: " + e.getMessage());
                return -1;
            }
        }

        columns = columns.substring(0, columns.length() - 2) + ")";
        values = values.substring(0, values.length() - 2) + ")";
        String sql = "insert into " + this.util.camelToSnakeCase(
                obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1]) + " "
                + columns + " values " + values + ";";

        try {
            PreparedStatement stmt = this.connectionObj.prepareCall(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.execute();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    @Override
    public <T> int updateMany(T obj, List<List<String>> params) {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<String>();
        String updates = "", conditions = "";

        for (Field field : fields) {
            try {
                fieldNames.add(field.getName().toString());
                updates = updates + field.getName().toString() + " = ";
                updates = updates + "'" + field.get(obj).toString() + "', ";
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
                conditions = conditions + " " + param.get(0) + " = ";
                conditions = conditions + "'" + param.get(1) + "' and";
            } else {
                System.out.println("ERROR: Invalid paramater: " + param.get(0));
                return -1;
            }
        }

        updates = updates.substring(0, updates.length() - 2);
        conditions = conditions.substring(0, conditions.length() - 4);
        String sql = "update " + this.util.camelToSnakeCase(
                obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1]) + " set "
                + updates + " where" + conditions + ";";

        try {
            PreparedStatement stmt = this.connectionObj.prepareCall(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    @Override
    public <T> int deleteMany(T obj, List<List<String>> params) {
        Field[] fields = obj.getClass().getDeclaredFields();
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
                conditions = conditions + " " + param.get(0) + " = ";
                conditions = conditions + "'" + param.get(1) + "' and";
            } else {
                System.out.println("ERROR: Invalid paramater: " + param.get(0));
                return -1;
            }
        }

        conditions = conditions.substring(0, conditions.length() - 4);
        String sql = "delete from " + this.util.camelToSnakeCase(
                obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1]) + " where"
                + conditions + ";";

        try {
            PreparedStatement stmt = this.connectionObj.prepareCall(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    @Override
    public <T> int select(T obj, List<List<String>> params, List<String> reqCols) {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<String>();
        String columns = "", conditions = "";

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
                conditions = conditions + " " + param.get(0) + " = ";
                conditions = conditions + "'" + param.get(1) + "' and";
            } else {
                System.out.println("ERROR: Invalid paramater: " + param.get(0));
                return -1;
            }
        }

        for (String reqCol : reqCols) {
            if (fieldNames.contains(reqCol)) {
                columns = columns + reqCol + ", ";
            } else {
                System.out.println("ERROR: Invalid paramater: " + reqCol);
                return -1;
            }
        }

        columns = columns.substring(0, columns.length() - 2);
        conditions = conditions.substring(0, conditions.length() - 4);
        String sql = "select " + columns + " from " + this.util.camelToSnakeCase(
                obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1]) + " where"
                + conditions + ";";

        try {
            PreparedStatement stmt = this.connectionObj.prepareCall(sql,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result = stmt.executeQuery();

            int count = 0;
            if (result.last()) {
                count = result.getRow();
                result.beforeFirst();
            }
            return count;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }
}