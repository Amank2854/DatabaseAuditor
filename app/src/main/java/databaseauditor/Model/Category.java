package databaseauditor.Model;


public class Category {
    public String category_id;
    public String name;
    public String last_update;

    Category(String category_id, String name, String last_update) {
        this.category_id = category_id;
        this.name = name;
        this.last_update = last_update;
    }
}