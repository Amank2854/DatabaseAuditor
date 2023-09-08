package databaseauditor.Model;

public class FilmActor {
    public String actor_id;
    public String film_id;
    public String last_update;

    public FilmActor(String actor_id, String film_id, String last_update) {
        this.actor_id = actor_id;
        this.film_id = film_id;
        this.last_update = last_update;
    }
}
