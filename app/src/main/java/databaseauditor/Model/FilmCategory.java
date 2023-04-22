package databaseauditor.Model;


public class FilmCategory {
    public String film_id;
    public String category_id;
    public String last_update;

    FilmCategory(String film_id, String category_id, String last_update) {
        this.film_id = film_id;
        this.category_id = category_id;
        this.last_update = last_update;
    }
}
