package databaseauditor.Analyzer.Neo4j;
// package databaseauditor.Analyzer;

// import org.neo4j.driver.AuthTokens;
// import org.neo4j.driver.GraphDatabase;
// import org.neo4j.driver.*;

// import io.github.cdimascio.dotenv.Dotenv;

// public class NeoSpecific {
//     Dotenv dotenv = Dotenv.load();
//     Session session = null;

//     NeoSpecific(Session session) {
//         session = GraphDatabase.driver(this.dotenv.get("NEO4J_URL"),
//                 AuthTokens.basic(this.dotenv.get("NEO4J_USER"), this.dotenv.get("NEO4J_PASSWORD"))).session();
//     }

//     void query(String query, List<String> args) {
//         int id = 0;
//         String updatedQuery = "";
//         for (int i = 0; i < query.length(); i++) {
//             if (query.charAt(i) == '?') {
//                 updatedQuery += args.get(id);
//                 id++;
//             } else {
//                 updatedQuery += query.charAt(i);
//             }
//         }

//         final String finalQuery = updatedQuery;
//         session.run(finalQuery);
//     }
// }
