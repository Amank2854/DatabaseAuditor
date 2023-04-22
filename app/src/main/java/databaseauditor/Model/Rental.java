package databaseauditor.Model;

import java.sql.Timestamp;

public class Rental {
    public int rental_id;
    public Timestamp rental_date;
    public int inventory_id;
    public int customer_id;
    public Timestamp return_date;
    public int staff_id;
    public Timestamp last_update;

    Rental(int rental_id, Timestamp rental_date, int inventory_id, int customer_id, Timestamp return_date, int staff_id,
           Timestamp last_update) {
        this.rental_id = rental_id;
        this.rental_date = rental_date;
        this.inventory_id = inventory_id;
        this.customer_id = customer_id;
        this.return_date = return_date;
        this.staff_id = staff_id;
        this.last_update = last_update;
    }
}