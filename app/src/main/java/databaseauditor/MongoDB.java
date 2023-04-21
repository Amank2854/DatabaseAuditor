package databaseauditor;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.github.cdimascio.dotenv.Dotenv;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.lang.reflect.Field;
import java.util.List;

import org.bson.Document;

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

    }
    
    @Override
    public <T> int insertOne(T obj) {
        MongoCollection<Document> collection = this.database.getCollection(
                util.camelToSnakeCase(obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1]));
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

    public <T> int updateMany(T obj) {
        // MongoCollection<Document> collection =
        // this.database.getCollection(util.camelToSnakeCase(obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length
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
        // this.database.getCollection(util.camelToSnakeCase(obj.getClass().getName().split("\.")[obj.getClass().getName().split("\.").length
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

    @Override
    public <T> int select(T obj, List<List<String>> params, List<String> reqCols) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'select'");
    }
}