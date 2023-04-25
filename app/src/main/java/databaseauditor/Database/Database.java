package databaseauditor.Database;

import java.util.List;

public interface Database {
    public boolean connect(String url, String username, String password) throws Exception;

    public void disconnect() throws Exception;

    public <T> int insertOne(T obj) throws Exception;

    public <T> int updateMany(T obj, List<List<String>> params) throws Exception;

    public <T> int deleteMany(T obj, List<List<String>> params) throws Exception;

    public <T> int select(T obj, List<List<String>> params, List<String> reqCols) throws Exception;
}