package databaseauditor.Model;

public class Rental {
    public String rental_id;
    public String rental_date;
    public String inventory_id;
    public String customer_id;
    public String return_date;
    public String staff_id;
    public String last_update;

    public Rental(String rental_id, String rental_date, String inventory_id, String customer_id, String return_date, String staff_id,
           String last_update) {
        this.rental_id = rental_id;
        this.rental_date = rental_date;
        this.inventory_id = inventory_id;
        this.customer_id = customer_id;
        this.return_date = return_date;
        this.staff_id = staff_id;
        this.last_update = last_update;
    }
}