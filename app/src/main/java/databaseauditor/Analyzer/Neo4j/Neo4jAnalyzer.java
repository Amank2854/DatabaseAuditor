package databaseauditor.Analyzer.Neo4j;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;

import databaseauditor.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.*;

import io.github.cdimascio.dotenv.Dotenv;

public class Neo4jAnalyzer {
    public List<String> queries = new ArrayList<String>();
    public List<Integer> argLens = new ArrayList<Integer>();

    Dotenv dotenv = Dotenv.load();
    Session session = null;
    Utilities utils = new Utilities();

    // Class constructor for reading the specific queries from Neo4jQueries.cypher
    public Neo4jAnalyzer() throws Exception {
        session = GraphDatabase.driver(this.dotenv.get("NEO4J_URL"),
                AuthTokens.basic(this.dotenv.get("NEO4J_USER"), this.dotenv.get("NEO4J_PASSWORD"))).session();

        File file = new File(
                System.getProperty("user.dir")
                        + "/src/main/java/databaseauditor/Analyzer/Neo4j/Neo4jQueries.cypher");
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

    // Method for executing specific queries in the Neo4j database
    public int query(int queryNum, List<String> args) {
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

        final String finalQuery = updatedQuery;
        session.run(finalQuery);
        return 1;
    }
}
