package databaseauditor.Model;


public class Inventory {
    public String inventory_id;
    public String film_id;
    public String store_id;
    public String last_update;

    Inventory(String inventory_id, String film_id, String store_id, String last_update) {
        this.inventory_id = inventory_id;
        this.film_id = film_id;
        this.store_id = store_id;
        this.last_update = last_update;
    }
}