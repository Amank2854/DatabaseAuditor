package databaseauditor.Model;

public class Address {
    public String address_id;
    public String address;
    public String address2;
    public String district;
    public String city_id;
    public String postal_code;
    public String phone;
    public String last_update;

    public Address(String address_id, String address, String address2, String district, String city_id, String postal_code,
            String phone, String last_update) {
        this.address_id = address_id;
        this.address = address;
        this.address2 = address2;
        this.district = district;
        this.city_id = city_id;
        this.postal_code = postal_code;
        this.phone = phone;
        this.last_update = last_update;
    }
}