package databaseauditor.Model;

public class Language {
    public String language_id;
    public String name;
    public String last_update;

    public Language(String language_id, String name, String last_update) {
        this.language_id = language_id;
        this.name = name;
        this.last_update = last_update;
    }
}