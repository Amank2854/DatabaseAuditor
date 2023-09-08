package databaseauditor.Analyzer.Postgres;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import databaseauditor.Utilities;

public class PostgresAnalyzer {
    public List<String> queries = new ArrayList<String>();
    public List<Integer> argLens = new ArrayList<Integer>();

    Dotenv dotenv = Dotenv.load();
    Connection conn = null;
    Utilities utils = new Utilities();

    /**
     * Constructor for the PostgresAnalyzer class to initalize the connection and
     * read the queries from the file PostgresQueries.sql
     * 
     * @throws Exception if something goes wrong
     */
    public PostgresAnalyzer() throws Exception {
        this.conn = DriverManager.getConnection(dotenv.get("POSTGRES_URL") + dotenv.get("DB_NAME"),
                dotenv.get("POSTGRES_USER"),
                dotenv.get("POSTGRES_PASSWORD"));

        File file = new File(
                System.getProperty("user.dir")
                        + "/src/main/java/databaseauditor/Analyzer/Postgres/PostgresQueries.sql");
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
                this.queries.add(query);
                this.argLens.add(count);
                query = "";
                count = 0;
            }
        }

        bfr.close();
    }

    /**
     * Method to fire the specified query in the Postgres database
     * 
     * @param queryNum the query number to fire
     * @param args     the arguments to pass to the query
     * @return 1 if the query is successful
     * @throws Exception if the query fails
     */
    public int query(int queryNum, List<String> args) throws Exception {
        int id = 0;
        String query = this.queries.get(queryNum), updatedQuery = "";
        for (int i = 0; i < query.length(); i++) {
            if (query.charAt(i) == '?') {
                updatedQuery += args.get(id);
                id++;
            } else {
                updatedQuery += query.charAt(i);
            }
        }

        PreparedStatement stmt = conn.prepareCall(updatedQuery, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        stmt.execute();
        return 1;
    }
}
