package databaseauditor.Model;

import java.sql.Timestamp;

public class FilmCategory {
    public int film_id;
    public int category_id;
    public Timestamp last_update;

    FilmCategory(int film_id, int category_id, Timestamp last_update) {
        this.film_id = film_id;
        this.category_id = category_id;
        this.last_update = last_update;
    }
}
