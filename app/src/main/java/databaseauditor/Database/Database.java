package databaseauditor.Database;

import java.util.List;

public interface Database {
    // Method to connect to the database
    public void connect(String url, String username, String password) throws Exception;

    // Method to disconnect from the database
    public void disconnect() throws Exception;

    // Method to insert one record into the database
    public <T> int insertOne(T obj) throws Exception;

    // Method to insert many records into the database
    public <T> int updateMany(T obj, List<List<String>> params) throws Exception;

    // Method to update many records in the database
    public <T> int deleteMany(T obj, List<List<String>> params) throws Exception;

    // Method to select records from the database
    public <T> int select(T obj, List<List<String>> params, List<String> reqCols) throws Exception;
}