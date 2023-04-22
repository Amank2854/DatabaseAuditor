package databaseauditor.Model;

import java.sql.Timestamp;

public class Category {
    public int category_id;
    public String name;
    public Timestamp last_update;

    Category(int category_id, String name, Timestamp last_update) {
        this.category_id = category_id;
        this.name = name;
        this.last_update = last_update;
    }
}