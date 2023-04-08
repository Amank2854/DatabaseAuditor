package databaseauditor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class WorkBench {
    void init() {
        PostgreSQL postgres = new PostgreSQL();
        postgres.connect();

        Address obj = new Address(1, "123 Main St", "Apt 1", "District 1", 1, "12345", "123-456-7890",
                Timestamp.valueOf(LocalDateTime.now()));
        if (postgres.insert(obj)) {
            System.out.print("INSERT SUCCESSFULL");
        }
    }
}

class Address {
    int address_id;
    String address;
    String address2;
    String district;
    int city_id;
    String postal_code;
    String phone;
    Timestamp last_update;

    Address(
            int address_id,
            String address,
            String address2,
            String district,
            int city_id,
            String postal_code,
            String phone,
            Timestamp last_update) {
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
