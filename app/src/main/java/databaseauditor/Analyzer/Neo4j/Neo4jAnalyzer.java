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

    /**
     * Constructor for the Neo4jAnalyzer class to initalize the connection and read
     * the queries from the file Neo4jQueries.cypher
     * 
     * @throws Exception if something goes wrong
     */
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

    /**
     * Method to fire the specified query in the Neo4j database
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

        final String finalQuery = updatedQuery;
        session.run(finalQuery);
        return 1;
    }

    /**
     * Method to create relationships in the Neo4j database
     * 
     * @param relationships the relationships to create
     * @throws Exception if the relationship creation fails
     */
    public void createRelationships(List<List<String>> relationships) throws Exception {
        for (List<String> relationship : relationships) {
            String query = "MATCH (a:"+relationship.get(0).toLowerCase()+"),(b:"+relationship.get(2).toLowerCase()+") WHERE a."+relationship.get(1)+" = b."+relationship.get(3)+" CREATE (a)-[r:"+relationship.get(4)+"]->(b) RETURN r;";
            String finalQuery = query;
            session.executeWrite(tx -> tx.run(finalQuery));
        }
    }
}
