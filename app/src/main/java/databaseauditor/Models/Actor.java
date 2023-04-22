package databaseauditor.Models;

import java.sql.Timestamp;

class Actor {
    int actor_id;
    String first_name;
    String last_name;
    Timestamp last_update;

    Actor(int actor_id, String first_name, String last_name, Timestamp last_update) {
        this.actor_id = actor_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.last_update = last_update;
    }
}