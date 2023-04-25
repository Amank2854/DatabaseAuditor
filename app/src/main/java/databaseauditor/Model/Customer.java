package databaseauditor.Model;

public class Customer {
    public String customer_id;
    public String store_id;
    public String first_name;
    public String last_name;
    public String email;
    public String address_id;
    public String activebool;
    public String create_date;
    public String last_update;
    public String active;

    public Customer(String customer_id, String store_id, String first_name, String last_name, String email,
            String address_id,
            String activebool, String create_date, String last_update, String active) {
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