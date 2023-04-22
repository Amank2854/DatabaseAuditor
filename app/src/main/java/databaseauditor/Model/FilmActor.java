package databaseauditor.Model;

import java.sql.Timestamp;

public class FilmActor {
    public int actor_id;
    public int film_id;
    public Timestamp last_update;

    FilmActor(int actor_id, int film_id, Timestamp last_update) {
        this.actor_id = actor_id;
        this.film_id = film_id;
        this.last_update = last_update;
    }
}
