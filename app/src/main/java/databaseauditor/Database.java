package databaseauditor;

interface Database {
    boolean connect();
    public <T> boolean insert(T obj);
    public <T> boolean update(T obj);
    public <T> boolean delete(T obj);
    public <T> boolean select(T obj);
}