package databaseauditor;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import databaseauditor.Analyzer.Analyzer;
import databaseauditor.Database.Builder;

@TestMethodOrder(OrderAnnotation.class)
class AppTest {
    static Builder builder;
    static List<Object> objs;

    static PrintStream sysOutBackup, outputStream;
    static ByteArrayOutputStream baos;

    @BeforeAll
    // Constructor to initialize the required objects
    static void init() {
        try {
            Utilities utils = new Utilities();

            builder = new Builder();
            objs = utils.getModels();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        sysOutBackup = System.out;
        baos = new ByteArrayOutputStream();
        outputStream = new PrintStream(baos);
        System.setOut(outputStream);
    }

    @Test
    @Order(1)
    // Test the init method
    void testInit() throws Exception {
        System.out.flush();
        builder.init(objs);
        assert (baos.toString().contains("Database setup complete"));
    }

    @Test
    @Order(2)
    // Test the postgres method
    void testPostgres() throws Exception {
        System.out.flush();
        builder.postgres(objs);
        assert (baos.toString().contains("PostgreSQL database initialized successfully"));
    }

    @Test
    @Order(3)
    // Test the mongoDB method
    void testMongoDB() throws Exception {
        System.out.flush();
        builder.mongoDB(objs);
        assert (baos.toString().contains("MongoDB database initialized successfully"));
    }

    @Test
    @Order(4)
    // Test the neo4j method
    void testNeo4j() throws Exception {
        System.out.flush();
        builder.neo4j(objs);
        assert (baos.toString().contains("Neo4j database initialized successfully"));
    }

    @Test
    @Order(5)
    // Test the create method
    void testCreate() throws Exception {
        Analyzer analyzer = new Analyzer();

        System.out.flush();
        analyzer.create(objs, 100);
        assert (baos.toString().contains("Creation operation(s) complete"));
    }

    @Test
    @Order(6)
    // Test the read method
    void testRead() throws Exception {
        Analyzer analyzer = new Analyzer();

        System.out.flush();
        analyzer.read(objs, 100);
        assert (baos.toString().contains("Read operation(s) complete"));
    }

    @Test
    @Order(7)
    // Test the update method
    void testUpdate() throws Exception {
        Analyzer analyzer = new Analyzer();

        System.out.flush();
        analyzer.update(objs, 100);
        assert (baos.toString().contains("Update operation(s) complete"));
    }

    @Test
    @Order(8)
    // Test the delete method
    void testDelete() throws Exception {
        Analyzer analyzer = new Analyzer();

        System.out.flush();
        analyzer.delete(objs, 100);
        assert (baos.toString().contains("Delete operation(s) complete"));
    }

    @Test
    @Order(9)
    // Test the query method
    void testQuery() throws Exception {
        Analyzer analyzer = new Analyzer();

        System.out.flush();
        analyzer.query(100);
        assert (baos.toString().contains("Specific query operation(s) complete"));
    }
}