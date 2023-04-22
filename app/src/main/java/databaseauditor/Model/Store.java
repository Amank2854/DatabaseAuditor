package databaseauditor.Model;

import java.sql.Timestamp;

public class Store {
    public int store_id;
    public int manager_staff_id;
    public int address_id;
    public Timestamp last_update;

    Store(int store_id, int manager_staff_id, int address_id, Timestamp last_update) {
        this.store_id = store_id;
        this.manager_staff_id = manager_staff_id;
        this.address_id = address_id;
        this.last_update = last_update;
    }
}