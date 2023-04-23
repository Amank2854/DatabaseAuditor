package databaseauditor.Model;

import java.util.ArrayList;
import java.util.List;

public class Instances {
    public List<Object> data = new ArrayList<Object>();

    public Instances() {
        data.add(new Actor("TEST", "TEST", "TEST", "TEST"));
        data.add(new Address("TEST", "TEST", "", "TEST", "TEST", "TEST", "TEST",
                "TEST"));
        data.add(new Category("TEST", "TEST", "TEST"));
        data.add(new City("TEST", "TEST", "TEST", "TEST"));
    }
}
