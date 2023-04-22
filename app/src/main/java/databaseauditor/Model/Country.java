package databaseauditor.Model;

public class Country {
    public String country_id;
    public String country;
    public String last_update;

    Country(String country_id, String country, String last_update) {
        this.country_id = country_id;
        this.country = country;
        this.last_update = last_update;
    }
}
