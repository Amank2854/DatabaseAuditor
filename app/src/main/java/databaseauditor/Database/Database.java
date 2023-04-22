package databaseauditor.Database;

import java.util.List;

interface Database {
    public boolean connect(String url, String username, String password);

    public void disconnect();

    public <T> int insertOne(T obj);

    public <T> int updateMany(T obj, List<List<String>> params);

    public <T> int deleteMany(T obj, List<List<String>> params);

    public <T> int select(T obj, List<List<String>> params, List<String> reqCols);
}