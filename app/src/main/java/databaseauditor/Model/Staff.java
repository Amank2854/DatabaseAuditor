package databaseauditor.Model;

public class Staff {
    public String staff_id;
    public String address_id;
    public String first_name;
    public String last_name;
    public String email;
    public String store_id;
    public String picture;
    public String username;
    public String password;
    public String last_update;
    public String active;

    public Staff(String staff_id, String address_id, String first_name, String last_name, String email, String store_id, String picture,
          String username, String password, String last_update, String active) {
        this.staff_id = staff_id;
        this.address_id = address_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.store_id = store_id;
        this.picture = picture;
        this.username = username;
        this.password = password;
        this.last_update = last_update;
        this.active = active;
    }
}