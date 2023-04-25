package databaseauditor;

import databaseauditor.Database.Neo4j;
import databaseauditor.Model.Actor;
import databaseauditor.Model.Instances;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        WorkBench obj = new WorkBench();
        obj.init();
    }
}