package databaseauditor.Model;

import java.sql.Timestamp;

public class Country {
    public int country_id;
    public String country;
    public Timestamp last_update;

    Country(int country_id, String country, Timestamp last_update) {
        this.country_id = country_id;
        this.country = country;
        this.last_update = last_update;
    }
}
