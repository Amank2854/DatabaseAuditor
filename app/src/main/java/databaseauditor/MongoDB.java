package databaseauditor;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.Updates;
import io.github.cdimascio.dotenv.Dotenv;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.*;

class MongoDB implements Database {
    Dotenv dotenv = Dotenv.load();
    final String mongoUri = this.dotenv.get("MONGODB_URI");
    final String dbName = this.dotenv.get("MONGODB_DBNAME");
    MongoDatabase database = null;
    Utilities util = new Utilities();

    @Override
    public boolean connect() {
        if (this.database != null) {
            return true;
        }

        try {
            MongoClient mongo = new MongoClient(new MongoClientURI(mongoUri));
            this.database = mongo.getDatabase(this.dbName);
            return true;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void disconnect() {
        this.database = null;
    }

    @Override
    public <T> int insertOne(T obj) {
        MongoCollection<Document> collection = this.database.getCollection(
                util.camelToSnakeCase(
                        obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1]));
        Document document = new Document();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                document.append(field.getName().toString(), field.get(obj).toString());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                return -1;
            } catch (IllegalAccessException e) {
                System.out.println("Error: " + e.getMessage());
                return -1;
            }
        }

        try {
            collection.insertOne(document);
            return 1;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public <T> int updateMany(T obj, List<List<String>> params) {
        MongoCollection<Document> collection = this.database.getCollection(
                util.camelToSnakeCase(obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length
                        - 1]));
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<String>();
        List<Bson> updates = new ArrayList<>();
        for (Field field : fields) {
            try {
                fieldNames.add(field.getName().toString());
                updates.add(Updates.set(field.getName().toString(), field.get(obj).toString()));
            } catch (IllegalAccessException e) {
                System.out.println("Error: " + e.getMessage());
                return -1;
            }
        }

        Bson filter = null;
        for (List<String> param : params) {
            if (fieldNames.contains(param.get(0))) {
                if (filter == null) {
                    filter = eq(param.get(0).toString(), param.get(1).toString());
                } else {
                    filter = and(filter, eq(param.get(0).toString(), param.get(1).toString()));
                }

            } else {
                System.out.println("ERROR: Invalid paramater: " + param.get(0));
                return -1;
            }
        }

        try {
            UpdateResult result = collection.updateMany(filter, Updates.combine(updates));
            return (int) result.getModifiedCount();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        }
    }

    public <T> int deleteMany(T obj,List<List<String>> params) {
        MongoCollection<Document> collection = this.database.getCollection(
                util.camelToSnakeCase(obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length
                        - 1]));
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<String>();
        for (Field field : fields) {
            fieldNames.add(field.getName().toString());
        }

        Bson filter = null;
        for (List<String> param : params) {
            if (fieldNames.contains(param.get(0))) {
                if (filter == null) {
                    filter = eq(param.get(0).toString(), param.get(1).toString());
                } else {
                    filter = and(filter, eq(param.get(0).toString(), param.get(1).toString()));
                }

            } else {
                System.out.println("ERROR: Invalid paramater: " + param.get(0));
                return -1;
            }
        }

        try {
            DeleteResult result = collection.deleteMany(filter);
            return (int) result.getDeletedCount();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        }
    }

    // @Override
    // public <T> int deleteMany(T obj, List<List<String>> params) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'deleteMany'");
    // }

    @Override
    public <T> int select(T obj, List<List<String>> params, List<String> reqCols) {
        MongoCollection<Document> collection = this.database.getCollection(
                util.camelToSnakeCase(obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length
                        - 1]));
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<String>();
        for (Field field : fields) {
            fieldNames.add(field.getName().toString());
        }

        Bson filter = null;
        for (List<String> param : params) {
            if (fieldNames.contains(param.get(0))) {
                if (filter == null) {
                    filter = eq(param.get(0).toString(), param.get(1).toString());
                } else {
                    filter = and(filter, eq(param.get(0).toString(), param.get(1).toString()));
                }

            } else {
                System.out.println("ERROR: Invalid paramater: " + param.get(0));
                return -1;
            }
        }

        Document projection = new Document();
        for (String col : reqCols) {
            if (fieldNames.contains(col)) {
                projection.append(col, 1);
            } else {
                System.out.println("ERROR: Invalid column: " + col);
                return -1;
            }
        }

        try {
            ArrayList<Document> results = collection.find(filter).projection(projection).into(new ArrayList<Document>());
            return results.size();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        }
    }
}