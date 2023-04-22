package databaseauditor.Model;

import java.sql.Timestamp;

public class Customer {
    public int customer_id;
    public int store_id;
    public String first_name;
    public String last_name;
    public String email;
    public int address_id;
    public boolean activebool;
    public Timestamp create_date;
    public Timestamp last_update;
    public int active;

    Customer(int customer_id, int store_id, String first_name, String last_name, String email, int address_id,
             boolean activebool, Timestamp create_date, Timestamp last_update, int active) {
        this.customer_id = customer_id;
        this.store_id = store_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.address_id = address_id;
        this.activebool = activebool;
        this.create_date = create_date;
        this.last_update = last_update;
        this.active = active;
    }
}