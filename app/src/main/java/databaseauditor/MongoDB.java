package databaseauditor;

import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

class MongoDB implements Database {
    final int dbPort = 27017;
    final String dbUrl = "localhost";
    final String dbName = "dvdrental";
    final String username = "ojassvi";
    final String password = "ojassvi";
    MongoDatabase database = null;
    Utilities util = new Utilities();

    public boolean connect() {
        // if (dbClient != null) {
        // return true;
        // }

        // try {
        // dbClient = new MongoClient(dbUrl, dbPort);
        // db = dbClient.getDatabase(dbName);
        // return true;
        // } catch (Exception e) {
        // System.out.println(e.getMessage());
        // return false;
        // } 

        if (this.database != null) {
            return true;
        }

        try {
            MongoClient mongo = new MongoClient("localhost", 27017);
            MongoCredential credential = MongoCredential.createCredential(this.username, this.dbName,
                    this.password.toCharArray());
            MongoDatabase database = mongo.getDatabase(this.dbName);
            return true;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {

    }

    public <T> int insertOne(T obj) {
        // MongoCollection<Document> collection =
        // this.db.getCollection(util.camelToSnakeCase(obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length
        // - 1]));
        // Document document = new Document();
        // Field[] fields = obj.getClass().getDeclaredFields();
        // for (Field field : fields) {
        // try {
        // document.append(field.getName(), field.get(obj));
        // } catch (IllegalArgumentException var11) {
        // System.out.println("Error: " + var11.getMessage());
        // return false;
        // } catch (IllegalAccessException var12) {
        // System.out.println("Error: " + var12.getMessage());
        // return false;
        // }
        // }

        // try {
        // collection.insertOne(document);
        // return true;
        // } catch (Exception var10) {
        // System.out.println(var10.getMessage());
        // return false;
        // }
        return 1;
    }

    public <T> int updateMany(T obj) {
        // MongoCollection<Document> collection =
        // this.db.getCollection(util.camelToSnakeCase(obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length
        // - 1]));
        // Document document = new Document();
        // Field[] fields = obj.getClass().getDeclaredFields();
        // List<Document> updates = new ArrayList<Document>();
        // for (Field field : fields) {
        // try {
        // if (!field.getName().equalsIgnoreCase("id")) {
        // document.append(field.getName(), field.get(obj));
        // updates.add(new Document("$set", document));
        // }
        // } catch (IllegalArgumentException var11) {
        // System.out.println("Error: " + var11.getMessage());
        // return false;
        // } catch (IllegalAccessException var12) {
        // System.out.println("Error: " + var12.getMessage());
        // return false;
        // }
        // }

        // try {
        // collection.updateOne(new Document("id", document.get("id")), updates);
        // return true;
        // } catch (Exception var10) {
        // System.out.println(var10.getMessage());
        // return false;
        // }
        return 1;
    }

    public <T> int deleteMany(T obj) {
        // MongoCollection<Document> collection =
        // this.db.getCollection(util.camelToSnakeCase(obj.getClass().getName().split("\.")[obj.getClass().getName().split("\.").length
        // - 1]));
        // Document document = new Document();
        // Field[] fields = obj.getClass().getDeclaredFields();
        // for (Field field : fields) {
        // try {
        // if (field.getName().equalsIgnoreCase("id")) {
        // document.append(field.getName(), field.get(obj));
        // }
        // } catch (IllegalArgumentException var11) {
        // System.out.println("Error: " + var11.getMessage());
        // return false;
        // } catch (IllegalAccessException var12) {
        // System.out.println("Error: " + var12.getMessage());
        // return false;
        // }
        // }
        // try {
        // collection.deleteOne(document);
        // return true;
        // } catch (Exception var10) {
        // System.out.println(var10.getMessage());
        // return false;
        // }
        return 1;
    }

    @Override
    public <T> int updateMany(T obj, List<List<String>> params) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMany'");
    }

    @Override
    public <T> int deleteMany(T obj, List<List<String>> params) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteMany'");
    }
}