package databaseauditor.Models;

import java.sql.Timestamp;

class Address {
    int address_id;
    String address;
    String address2;
    String district;
    int city_id;
    String postal_code;
    String phone;
    Timestamp last_update;

    Address(int address_id, String address, String address2, String district, int city_id, String postal_code,
            String phone, Timestamp last_update) {
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