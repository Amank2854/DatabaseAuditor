package databaseauditor.Model;

import java.sql.Timestamp;
public class Film {
    public int film_id;
    public String title;
    public String description;
    public Timestamp release_year;
    public int language_id;
    public String fulltext;
    public int rental_duration;
    public double rental_rate;
    public int length;
    public double replacement_cost;
    public String rating;
    public String special_features;
    public Timestamp last_update;

    Film(int film_id, String title, String description, Timestamp release_year, int language_id, String fulltext,
         int rental_duration, double rental_rate, int length, double replacement_cost, String rating,
         String special_features, Timestamp last_update) {
        this.film_id = film_id;
        this.title = title;
        this.description = description;
        this.release_year = release_year;
        this.language_id = language_id;
        this.fulltext = fulltext;
        this.rental_duration = rental_duration;
        this.rental_rate = rental_rate;
        this.length = length;
        this.replacement_cost = replacement_cost;
        this.rating = rating;
        this.special_features = special_features;
        this.last_update = last_update;
    }
}
