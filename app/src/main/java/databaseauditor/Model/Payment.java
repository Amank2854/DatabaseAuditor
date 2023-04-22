package databaseauditor.Model;


public class Payment {
    public String payment_id;
    public String customer_id;
    public String staff_id;
    public String rental_id;
    public String amount;
    public String payment_date;

    Payment(String payment_id, String customer_id, String staff_id, String rental_id, String amount, String payment_date) {
        this.payment_id = payment_id;
        this.customer_id = customer_id;
        this.staff_id = staff_id;
        this.rental_id = rental_id;
        this.amount = amount;
        this.payment_date = payment_date;
    }
}