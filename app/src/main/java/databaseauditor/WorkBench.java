package databaseauditor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class WorkBench {
    void init() {
        PostgreSQL postgres = new PostgreSQL();
        postgres.connect();

        Address obj = new Address(1, "123 Main St", "Apt 1", "District 1", 1, "12345", "123-456-7890",
                Timestamp.valueOf(LocalDateTime.now()));
        if (postgres.insertOne(obj) != -1) {
            System.out.println("INSERT SUCCESSFULL");
        }

        obj.city_id = 2;
        List<List<String>> params = Arrays.asList(Arrays.asList("address_id", "1"));
        // int rows = postgres.updateMany(obj, params);
        // if (rows != -1) {
        //     System.out.println("UPDATE SUCCESSFULL " + rows);
        // }

        // params = Arrays.asList(Arrays.asList("address_id", "1"), Arrays.asList("city_id", "2"));
        // rows = postgres.deleteMany(obj, params);
        // if (rows != -1) {
        //     System.out.println("DELETE SUCCESSFULL " + rows);
        // }

        List<String> reqCols = Arrays.asList("address_id", "address", "address2", "district", "city_id", "postal_code");
        int rows = postgres.select(obj, params, reqCols);
        if (rows != -1) {
            System.out.println("SELECT SUCCESSFULL " + rows);
        } else {
            System.out.println("SELECT FAILED");
        }

        // MongoDB mongodb = new MongoDB();
        // mongodb.connect();

        // if (mongodb.insertOne(obj) != -1) {
        //     System.out.println("INSERT SUCCESSFULL");
        // }

    }
}