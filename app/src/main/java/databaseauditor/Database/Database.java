package databaseauditor.Database;

import java.util.List;

public interface Database {
    /**
     * Method to connect to the database
     * 
     * @param url      url of the database
     * @param username username for the database
     * @param password password for the database
     * @throws Exception exception if something went wrong
     */
    public void connect(String url, String username, String password) throws Exception;

    /**
     * Method to disconnect from the database
     * 
     * @throws Exception exception if something went wrong
     */
    public void disconnect() throws Exception;

    /**
     * Method to insert one record into the database
     * 
     * @param <T> type of the object to be inserted into the database (model)
     * @param obj object to be inserted into the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
    public <T> int insertOne(T obj) throws Exception;

    /**
     * Method to update records in the database
     * 
     * @param <T>    type of the object to be updated in the database (model)
     * @param obj    object to be updated in the database
     * @param params list of parameters to check for in the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
    public <T> int updateMany(T obj, List<List<String>> params) throws Exception;

    /**
     * Method to delete records from the database
     * 
     * @param <T>    type of the object to be deleted from the database (model)
     * @param obj    object to be deleted from the database
     * @param params list of parameters to check for in the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
    public <T> int deleteMany(T obj, List<List<String>> params) throws Exception;

    /**
     * Method to select records from the database
     * 
     * @param <T>     type of the object to be selected from the database (model)
     * @param obj     an object of the model from which results will be selected from the database
     * @param params  list of parameters to check for in the database
     * @param reqCols list of attributes to be selected from the database
     * @return number of rows affected, 0 if unsuccessful
     * @throws Exception exception if something went wrong
     */
    public <T> int select(T obj, List<List<String>> params, List<String> reqCols) throws Exception;
}