package databaseauditor.Model;


public class City {
    public String city_id;
    public String city;
    public String country_id;
    public String last_update;

    City(String city_id, String city, String country_id, String last_update) {
        this.city_id = city_id;
        this.city = city;
        this.country_id = country_id;
        this.last_update = last_update;
    }
}