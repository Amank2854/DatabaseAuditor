package databaseauditor.Model;

import java.sql.Timestamp;

public class Payment {
    public int payment_id;
    public int customer_id;
    public int staff_id;
    public int rental_id;
    public double amount;
    public Timestamp payment_date;

    Payment(int payment_id, int customer_id, int staff_id, int rental_id, double amount, Timestamp payment_date) {
        this.payment_id = payment_id;
        this.customer_id = customer_id;
        this.staff_id = staff_id;
        this.rental_id = rental_id;
        this.amount = amount;
        this.payment_date = payment_date;
    }
}