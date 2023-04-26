package databaseauditor.Analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

public class PostgresSpecific {
    Dotenv dotenv = Dotenv.load();
    Connection conn = null;
    List<String> queries = new ArrayList<String>();
    List<Integer> argLens = new ArrayList<Integer>();
    int max_num = 10000, min_num = 1;

    public PostgresSpecific() throws Exception {
        this.conn = DriverManager.getConnection(dotenv.get("POSTGRES_URL") + dotenv.get("DB_NAME"),
                dotenv.get("POSTGRES_USER"),
                dotenv.get("POSTGRES_PASSWORD"));

        File file = new File(System.getProperty("user.dir") + "/src/main/java/databaseauditor/Analyzer/PostgresSpecific.sql");
        BufferedReader bfr = new BufferedReader(new FileReader(file));

        int count = 0;
        String query = "", cur = "";
        while ((cur = bfr.readLine()) != null) {
            query += "\n" + cur;
            for (int i = 0; i < cur.length(); i++) {
                if (cur.charAt(i) == '?') {
                    count++;
                }
            }

            if (query.contains(";")) {
                queries.add(query);
                argLens.add(count);
                query = "";
                count = 0;
            }
        }

        bfr.close();
    }

    public void analyze(int numIterations) throws Exception {
        for (int i = 0; i < numIterations; i++) {
            List<String> args = new ArrayList<String>();
            int id = (int) Math.floor(Math.random() * queries.size());

            for (int j = 0; j < argLens.get(id); j++) {
                int random_int = (int) Math.floor(Math.random() * (max_num - min_num + 1) + min_num);
                args.add(Integer.toString(random_int));
            }

            query(queries.get(id), args);
        }
    }

    public void query(String query, List<String> args) throws Exception {
        int id = 0;
        String updateQuery = "";
        for (int i = 0; i < query.length(); i++) {
            if (query.charAt(i) == '?') {
                updateQuery += args.get(id);
                id++;
            } else {
                updateQuery += query.charAt(i);
            }
        }

        PreparedStatement stmt = conn.prepareCall(updateQuery, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        stmt.execute();
    }
}
