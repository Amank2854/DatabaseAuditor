package databaseauditor.Model;

public class Actor {
    public String actor_id;
    public String first_name;
    public String last_name;
    public String last_update;

    public Actor(String actor_id, String first_name, String last_name, String last_update) {
        this.actor_id = actor_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.last_update = last_update;
    }
}