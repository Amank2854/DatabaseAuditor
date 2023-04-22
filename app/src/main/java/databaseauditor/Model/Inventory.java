package databaseauditor.Model;

import java.sql.Timestamp;

public class Inventory {
    public int inventory_id;
    public int film_id;
    public int store_id;
    public Timestamp last_update;

    Inventory(int inventory_id, int film_id, int store_id, Timestamp last_update) {
        this.inventory_id = inventory_id;
        this.film_id = film_id;
        this.store_id = store_id;
        this.last_update = last_update;
    }
}