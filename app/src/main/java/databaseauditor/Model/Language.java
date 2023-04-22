package databaseauditor.Model;

import java.sql.Timestamp;

public class Language {
    public int language_id;
    public String name;
    public Timestamp last_update;

    Language(int language_id, String name, Timestamp last_update) {
        this.language_id = language_id;
        this.name = name;
        this.last_update = last_update;
    }
}