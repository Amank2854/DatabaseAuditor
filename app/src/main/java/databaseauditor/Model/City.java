package databaseauditor.Model;

import java.sql.Timestamp;

public class City {
    public int city_id;
    public String city;
    public int country_id;
    public Timestamp last_update;

    City(int city_id, String city, int country_id, Timestamp last_update) {
        this.city_id = city_id;
        this.city = city;
        this.country_id = country_id;
        this.last_update = last_update;
    }
}