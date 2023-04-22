package databaseauditor;

import org.neo4j.driver.*;

import java.util.*;

import static org.neo4j.driver.Values.parameters;

public class Neo4j implements AutoCloseable {
    private Driver driver;

    public void connect(String uri , String username, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    @Override
    public void close() throws RuntimeException {
        driver.close();
    }

    public void insertOne(String label, List<String> propertyKey, List<Object> propertyValue) {
        String query = "CREATE (n:" + label + " {";
        for (int i = 0; i < propertyKey.size(); i++) {
            query = query + propertyKey.get(i) + ": $" + i;
            if(i != propertyKey.size() - 1) {
                query = query + ", ";
            }
        }
        query = query + "})";
        List<Object> params = new ArrayList<>();
        for (int i = 0; i < propertyValue.size(); i++) {
            params.add(i);
                    params.add(propertyValue.get(i));
        }
        Value param = Values.parameters(params);
        try (Session session = driver.session()) {
            String finalQuery = query;
            session.writeTransaction(tx -> tx.run(finalQuery, param));
        }
    }


//    public void printGreeting(final String message) {
//        try (var session = driver.session()) {
//            var greeting = session.executeWrite(tx -> {
//                var query = new Query("CREATE (a:Greeting) SET a.message = $message RETURN a.message + ', from node ' + id(a)", parameters("message", message));
//                var result = tx.run(query);
//                return result.single().get(0).asString();
//            });
//            System.out.println(greeting);
//        }
//    }

    public static void main(String... args) {
        Neo4j ob = new Neo4j();
        ob.connect("bolt://localhost:7687", "neo4j", "123456");
        List<String> propertyKey = new ArrayList<>();
    }
}