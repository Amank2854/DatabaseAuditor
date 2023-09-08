package databaseauditor.Model;

public class Store {
    public String store_id;
    public String manager_staff_id;
    public String address_id;
    public String last_update;

    public Store(String store_id, String manager_staff_id, String address_id, String last_update) {
        this.store_id = store_id;
        this.manager_staff_id = manager_staff_id;
        this.address_id = address_id;
        this.last_update = last_update;
    }
}