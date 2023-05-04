package databaseauditor.Database;

import com.mongodb.client.model.Updates;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import io.github.cdimascio.dotenv.Dotenv;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.bson.Document;

import databaseauditor.Utilities;

import static com.mongodb.client.model.Filters.*;

public class MongoDB implements Database {
    MongoDatabase database = null;
    Utilities util = new Utilities();

    @Override
    /**
     * Method to connect to the mongodb database
     * 
     * @param url      url of the database
     * @param username username of the database
     * @param password password of the database
     * @throws Exception exception if something went wrong
     * @return void
     */
    public void connect(String url, String username, String password) throws Exception {
        if (this.database != null) {
            return;
        }

        Dotenv dotenv = Dotenv.load();
        MongoClient mongo = new MongoClient(new MongoClientURI(url));
        this.database = mongo.getDatabase(dotenv.get("DB_NAME"));
    }

    @Override
    /**
     * Method to disconnect from the mongodb database
     * 
     * @throws Exception exception if something went wrong
     */
    public void disconnect() {
        this.database = null;
    }

    @Override
    /**
     * Method to insert one record into the MongoDB database
     * 
     * @param <T> type of the object to be inserted into the database (model)
     * @param obj object to be inserted into the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
    public <T> int insertOne(T obj) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection(
                util.camelToSnakeCase(
                        obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1]));
        Document document = new Document();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            document.append(field.getName().toString(), field.get(obj).toString());
        }

        collection.insertOne(document);
        return 1;
    }

    @Override
    /**
     * Method to update records in the MongoDB database
     * 
     * @param <T>    type of the object to be updated in the database (model)
     * @param obj    object to be updated in the database
     * @param params list of parameters to check for in the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
    public <T> int updateMany(T obj, List<List<String>> params) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection(
                util.camelToSnakeCase(obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length
                        - 1]));
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<String>();
        List<Bson> updates = new ArrayList<>();

        for (Field field : fields) {
            fieldNames.add(field.getName().toString());
            updates.add(Updates.set(field.getName().toString(), field.get(obj).toString()));
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
                throw new Exception("\nSomething went wrong: Invalid parameter " + param.get(0) + "\n");
            }
        }

        UpdateResult result = collection.updateMany(filter, Updates.combine(updates));
        return (int) result.getModifiedCount();
    }

    @Override
    /**
     * Method to delete records from the MongoDB database
     * 
     * @param <T>    type of the object to be deleted from the database (model)
     * @param obj    object to be deleted from the database
     * @param params list of parameters to check for in the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
    public <T> int deleteMany(T obj, List<List<String>> params) throws Exception {
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
                throw new Exception("\nSomething went wrong: Invalid parameter " + param.get(0) + "\n");
            }
        }

        DeleteResult result = collection.deleteMany(filter);
        return (int) result.getDeletedCount();
    }

    @Override
    /**
     * Method to select records from the MongoDB database
     * 
     * @param <T>     type of the object to be selected from the database (model)
     * @param obj     an object of the model from which results will be selected from the database
     * @param params  list of parameters to check for in the database
     * @param reqCols list of attributes to be selected from the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
    public <T> int select(T obj, List<List<String>> params, List<String> reqCols) throws Exception {
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
                throw new Exception("\nSomething went wrong: Invalid parameter " + param.get(0) + "\n");
            }
        }

        Document projection = new Document();
        for (String col : reqCols) {
            if (fieldNames.contains(col)) {
                projection.append(col, 1);
            } else {
                throw new Exception("\nSomething went wrong: Invalid parameter " + col + "\n");
            }
        }

        ArrayList<Document> results = collection.find(filter).projection(projection)
                .into(new ArrayList<Document>());
        return results.size();
    }
}