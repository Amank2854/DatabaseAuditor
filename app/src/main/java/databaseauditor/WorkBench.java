package databaseauditor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkBench {
    void init() {
        // PostgreSQL postgres = new PostgreSQL();
        // postgres.connect();

        Address obj = new Address(1, "House Number - 86, Nasrat Pura", "", "Ghaziabad", 1, "201001", "9250131555",
                Timestamp.valueOf(LocalDateTime.now()));
        // // if (postgres.insertOne(obj) != -1) {
        // //     System.out.println("INSERT SUCCESSFULL");
        // // }

        // // obj.city_id = 2;
        // List<List<String>> prms = Arrays.asList(Arrays.asList("address_id", "1"));
        // // int rows = postgres.updateMany(obj, params);
        // // if (rows != -1) {
        // //     System.out.println("UPDATE SUCCESSFULL " + rows);
        // // }

        // // params = Arrays.asList(Arrays.asList("address_id", "1"), Arrays.asList("city_id", "2"));
        // // rows = postgres.deleteMany(obj, params);
        // // if (rows != -1) {
        // //     System.out.println("DELETE SUCCESSFULL " + rows);
        // // }

        // List<String> reqCols = Arrays.asList("address_id", "address", "address2", "district", "city_id", "postal_code");
        // // int rows = postgres.select(obj, params, reqCols);
        // // if (rows != -1) {
        // //     System.out.println("SELECT SUCCESSFULL " + rows);
        // // } else {
        // //     System.out.println("SELECT FAILED");
        // // }

        // MongoDB mongodb = new MongoDB();
        // mongodb.connect();

        // // if (mongodb.insertOne(obj) != -1) {
        // //     System.out.println("INSERT SUCCESSFULL");
        // // }

        // obj.city_id = 2;
        // if (mongodb.updateMany(obj, params) != -1) {
        //     System.out.println("UPDATE SUCCESSFULL");
        // }
        

        // Utilities utils = new Utilities();
        // Object[] params = new Object[1];
        // params[0] = obj;
        // utils.getConsumedMemory(postgres, "insertOne", params, true);
        // params = new Object[2];
        // params[0] = obj;
        // params[1] = prms;
        // utils.getConsumedMemory(postgres, "updateMany", params, true);
        // utils.getConsumedMemory(postgres, "deleteMany", params, true);


        Init init = new Init();
        List<Object> objs = new ArrayList<Object>();
        objs.add(obj);
        init.postgreSQL(objs);
        init.mongoDB(objs);
    }
}