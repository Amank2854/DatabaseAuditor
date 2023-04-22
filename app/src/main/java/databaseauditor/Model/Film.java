package databaseauditor.Model;

public class Film {
    public String film_id;
    public String title;
    public String description;
    public String release_year;
    public String language_id;
    public String fulltext;
    public String rental_duration;
    public String rental_rate;
    public String length;
    public String replacement_cost;
    public String rating;
    public String special_features;
    public String last_update;

    Film(String film_id, String title, String description, String release_year, String language_id, String fulltext,
         String rental_duration, String rental_rate, String length, String replacement_cost, String rating,
         String special_features, String last_update) {
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
