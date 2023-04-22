package databaseauditor.Model;

import java.sql.Timestamp;

public class Staff {
    public int staff_id;
    public int address_id;
    public String first_name;
    public String last_name;
    public String email;
    public int store_id;
    public int picture;
    public String username;
    public String password;
    public Timestamp last_update;
    public int active;

    Staff(int staff_id, int address_id, String first_name, String last_name, String email, int store_id, int picture,
          String username, String password, Timestamp last_update, int active) {
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