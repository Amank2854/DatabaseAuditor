package databaseauditor.Analyzer.MongoDB;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import databaseauditor.Utilities;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MongoDBAnalyzer {
    public List<Function<List<String>, Integer>> queries = new ArrayList<Function<List<String>, Integer>>();

    Dotenv dotenv = Dotenv.load();
    MongoDatabase database = null;
    Utilities util = new Utilities();

    /**
     * Constructor for the MongoDBAnalyzer class to initalize the connection
     * 
     * @throws Exception if something goes wrong
     */
    public MongoDBAnalyzer() throws Exception {
        MongoClient mongo = new MongoClient(new MongoClientURI(dotenv.get("MONGODB_URI")));
        this.database = mongo.getDatabase(dotenv.get("DB_NAME"));
    }

    /**
     * Method to fire the specified query in the MongoDB database
     * 
     * @param queryNum the query number to fire
     * @param args     the arguments to pass to the query
     * @return 1 if the query is successful
     * @throws Exception if something goes wrong
     */
    public int query(int queryNum, List<String> args) throws Exception {
        Method method = this.getClass().getMethod("query" + Integer.toString(queryNum + 1), List.class);
        return (int) method.invoke(this, args);
    }

    public int query1(List<String> args) throws Exception {
        List<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document("$lookup", new Document("from", "rental")
                .append("localField", "customer_id")
                .append("foreignField", "customer_id")
                .append("as", "rentals")));
        pipeline.add(new Document("$project", new Document("customer_id", 1)
                .append("first_name", 1)
                .append("last_name", 1)
                .append("total_rentals", new Document("$size", "$rentals"))));
        pipeline.add(new Document("$sort", new Document("total_rentals", -1)));

        this.database.getCollection("customer").aggregate(pipeline);
        return 1;
    }

    public int query2(List<String> args) throws Exception {
        MongoCollection<Document> rentalCollection = this.database.getCollection("rental");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup", new Document()
                        .append("from", "inventory")
                        .append("localField", "inventory_id")
                        .append("foreignField", "inventory_id")
                        .append("as", "inventory")),
                new Document("$unwind", "$inventory"),
                new Document("$lookup", new Document()
                        .append("from", "film")
                        .append("localField", "inventory.film_id")
                        .append("foreignField", "film_id")
                        .append("as", "film")),
                new Document("$unwind", "$film"),
                new Document("$lookup", new Document()
                        .append("from", "payment")
                        .append("localField", "rental_id")
                        .append("foreignField", "rental_id")
                        .append("as", "payment")),
                new Document("$unwind", "$payment"),
                new Document("$group", new Document()
                        .append("_id", "$film.film_id")
                        .append("title", new Document("$first", "$film.title"))
                        .append("total_revenue", new Document("$sum", "$payment.amount"))),
                new Document("$sort", new Document("total_revenue", -1))

        );

        rentalCollection.aggregate(pipeline);
        return 1;
    }

    public int query3(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("film");
        var pipeline = Arrays.asList(
                new Document("$lookup", new Document()
                        .append("from", "inventory")
                        .append("localField", "inventory_id")
                        .append("foreignField", "inventory_id")
                        .append("as", "inventory")),
                new Document("$unwind", "$inventory"),
                new Document("$lookup", new Document()
                        .append("from", "store")
                        .append("localField", "inventory.store_id")
                        .append("foreignField", "store_id")
                        .append("as", "store")),
                new Document("$unwind", "$store"),
                new Document("$lookup", new Document()
                        .append("from", "payment")
                        .append("localField", "rental_id")
                        .append("foreignField", "rental_id")
                        .append("as", "payment")),
                new Document("$unwind", "$payment"),
                new Document("$group", new Document()
                        .append("_id", "$store.store_id")
                        .append("total_revenue", new Document("$sum", "$payment.amount"))),
                new Document("$sort", new Document()
                        .append("total_revenue", -1)));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query4(List<String> args) throws Exception {
        MongoCollection<Document> rentalCollection = this.database.getCollection("rental");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup", new Document()
                        .append("from", "inventory")
                        .append("localField", "inventory_id")
                        .append("foreignField", "inventory_id")
                        .append("as", "inventory")),
                new Document("$unwind", "$inventory"),
                new Document("$lookup", new Document()
                        .append("from", "store")
                        .append("localField", "inventory.store_id")
                        .append("foreignField", "store_id")
                        .append("as", "store")),
                new Document("$unwind", "$store"),
                new Document("$lookup", new Document()
                        .append("from", "staff")
                        .append("localField", "store.manager_staff_id")
                        .append("foreignField", "staff_id")
                        .append("as", "staff")),
                new Document("$unwind", "$staff"),
                new Document("$lookup", new Document()
                        .append("from", "payment")
                        .append("localField", "rental_id")
                        .append("foreignField", "rental_id")
                        .append("as", "payment")),
                new Document("$unwind", "$payment"),
                new Document("$group", new Document()
                        .append("_id", "$staff.staff_id")
                        .append("total_revenue", new Document("$sum", "$payment.amount"))),
                new Document("$sort", new Document("total_revenue", -1)));

        rentalCollection.aggregate(pipeline);
        return 1;
    }

    public int query5(List<String> args) throws Exception {
        MongoCollection<Document> rentalCollection = this.database.getCollection("rental");
        List<Document> pipeline = Arrays.asList(
                new Document("$lookup", new Document("from", "customer")
                        .append("localField", "customer_id")
                        .append("foreignField", "customer_id")
                        .append("as", "customer")),
                new Document("$unwind", "$customer"),
                new Document("$lookup", new Document("from", "payment")
                        .append("localField", "rental_id")
                        .append("foreignField", "rental_id")
                        .append("as", "payment")),
                new Document("$unwind", "$payment"),
                new Document("$group", new Document("_id", "$customer.customer_id")
                        .append("first_name", new Document("$first", "$customer.first_name"))
                        .append("last_name", new Document("$first", "$customer.last_name"))
                        .append("total_revenue", new Document("$sum", "$payment.amount"))),
                new Document("$sort", new Document("total_revenue", -1)));

        rentalCollection.aggregate(pipeline);
        return 1;
    }

    public int query6(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("rental");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup", new Document("from", "inventory")
                        .append("localField", "inventory_id")
                        .append("foreignField", "inventory_id")
                        .append("as", "inventory")),
                new Document("$unwind", "$inventory"),
                new Document("$lookup", new Document("from", "store")
                        .append("localField", "inventory.store_id")
                        .append("foreignField", "store_id")
                        .append("as", "store")),
                new Document("$unwind", "$store"),
                new Document("$lookup", new Document("from", "customer")
                        .append("localField", "customer_id")
                        .append("foreignField", "customer_id")
                        .append("as", "customer")),
                new Document("$unwind", "$customer"),
                new Document("$lookup", new Document("from", "payment")
                        .append("localField", "rental_id")
                        .append("foreignField", "rental_id")
                        .append("as", "payment")),
                new Document("$unwind", "$payment"),
                new Document("$group",
                        new Document("_id", new Document("customer_id", "$customer.customer_id")
                                .append("store_id", "$store.store_id"))
                                .append("customer_first_name",
                                        new Document("$first",
                                                "$customer.first_name"))
                                .append("customer_last_name",
                                        new Document("$first",
                                                "$customer.last_name"))
                                .append("store_id",
                                        new Document("$first",
                                                "$store.store_id"))
                                .append("total_revenue",
                                        new Document("$sum",
                                                "$payment.amount"))),
                new Document("$project", new Document("_id", 0)
                        .append("customer_id", "$_id.customer_id")
                        .append("customer_first_name", 1)
                        .append("customer_last_name", 1)
                        .append("store_id", 1)
                        .append("total_revenue", 1)),
                new Document("$sort", new Document("total_revenue", -1)));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query7(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("rental");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup", new Document("from", "inventory")
                        .append("localField", "inventory_id")
                        .append("foreignField", "inventory_id")
                        .append("as", "inventory")),
                new Document("$unwind", "$inventory"),
                new Document("$lookup", new Document("from", "film")
                        .append("localField", "inventory.film_id")
                        .append("foreignField", "film_id")
                        .append("as", "film")),
                new Document("$unwind", "$film"),
                new Document("$group", new Document("_id", new Document("rating", "$film.rating")
                        .append("language_id", "$film.language_id"))
                        .append("num_rentals", new Document("$sum", 1))));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query8(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("film");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup", new Document("from", "inventory")
                        .append("localField", "film_id")
                        .append("foreignField", "film_id")
                        .append("as", "inventory")),
                new Document("$unwind", "$inventory"),
                new Document("$lookup", new Document("from", "rental")
                        .append("localField", "inventory.inventory_id")
                        .append("foreignField", "inventory_id")
                        .append("as", "rental")),
                new Document("$unwind", "$rental"),
                new Document("$lookup", new Document("from", "customer")
                        .append("localField", "rental.customer_id")
                        .append("foreignField", "customer_id")
                        .append("as", "customer")),
                new Document("$unwind", "$customer"),
                new Document("$group", new Document("_id", "$customer.customer_id")
                        .append("count", new Document("$sum", 1))),
                new Document("$match", new Document("count", new Document("$gt", 45))),
                new Document("$lookup", new Document("from", "rental")
                        .append("localField", "_id")
                        .append("foreignField", "customer_id")
                        .append("as", "rentals")),
                new Document("$unwind", "$rentals"),
                new Document("$lookup", new Document("from", "inventory")
                        .append("localField", "rentals.inventory_id")
                        .append("foreignField", "inventory_id")
                        .append("as", "inventory")),
                new Document("$unwind", "$inventory"),
                new Document("$lookup", new Document("from", "film")
                        .append("localField", "inventory.film_id")
                        .append("foreignField", "film_id")
                        .append("as", "film")),
                new Document("$unwind", "$film"),
                new Document("$group", new Document("_id", "$film.title")));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query9(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("customer");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup", new Document()
                        .append("from", "rental")
                        .append("localField", "customer_id")
                        .append("foreignField", "customer_id")
                        .append("as", "rentals")),
                new Document("$unwind", "$rentals"),
                new Document("$lookup", new Document()
                        .append("from", "payment")
                        .append("localField", "rentals.rental_id")
                        .append("foreignField", "rental_id")
                        .append("as", "payments")),
                new Document("$unwind", "$payments"),
                new Document("$group", new Document()
                        .append("_id", "$customer_id")
                        .append("first_name", new Document("$first", "$first_name"))
                        .append("last_name", new Document("$first", "$last_name"))
                        .append("total_spent", new Document("$sum", "$payments.amount"))),
                new Document("$sort", new Document("total_spent", -1)),
                new Document("$limit", 10));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query10(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("category");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup", new Document("from", "film_category")
                        .append("localField", "category_id")
                        .append("foreignField", "category_id")
                        .append("as", "films")),
                new Document("$unwind", "$films"),
                new Document("$lookup", new Document("from", "inventory")
                        .append("localField", "films.film_id")
                        .append("foreignField", "film_id")
                        .append("as", "inventories")),
                new Document("$unwind", "$inventories"),
                new Document("$lookup", new Document("from", "rental")
                        .append("localField", "inventories.inventory_id")
                        .append("foreignField", "inventory_id")
                        .append("as", "rentals")),
                new Document("$unwind", "$rentals"),
                new Document("$lookup", new Document("from", "payment")
                        .append("localField", "rentals.rental_id")
                        .append("foreignField", "rental_id")
                        .append("as", "payments")),
                new Document("$unwind", "$payments"),
                new Document("$group", new Document("_id", "$category_id")
                        .append("name", new Document("$first", "$name"))
                        .append("total_revenue", new Document("$sum", "$payments.amount"))),
                new Document("$sort", new Document("total_revenue", -1)),
                new Document("$limit", 5));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query11(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("category");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup",
                        new Document("from", "film_category")
                                .append("localField", "category_id")
                                .append("foreignField", "category_id")
                                .append("as", "films")),
                new Document("$project",
                        new Document("name", 1)
                                .append("num_films", new Document("$size", "$films"))),
                new Document("$sort", new Document("num_films", -1)),
                new Document("$limit", 10));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query12(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("customer");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup",
                        new Document("from", "address")
                                .append("localField", "address_id")
                                .append("foreignField", "address_id")
                                .append("as", "address")),
                new Document("$unwind", "$address"),
                new Document("$lookup",
                        new Document("from", "city")
                                .append("localField", "address.city_id")
                                .append("foreignField", "city_id")
                                .append("as", "city")),
                new Document("$unwind", "$city"),
                new Document("$lookup",
                        new Document("from", "country")
                                .append("localField", "city.country_id")
                                .append("foreignField", "country_id")
                                .append("as", "country")),
                new Document("$unwind", "$country"),
                new Document("$lookup",
                        new Document("from", "payment")
                                .append("localField", "customer_id")
                                .append("foreignField", "customer_id")
                                .append("as", "payments")),
                new Document("$unwind", "$payments"),
                new Document("$group",
                        new Document("_id", "$country.country")
                                .append("total_customers",
                                        new Document("$addToSet",
                                                "$customer_id"))
                                .append("total_payments",
                                        new Document("$sum",
                                                "$payments.amount"))),
                new Document("$project",
                        new Document("_id", 0)
                                .append("country", "$_id")
                                .append("total_customers",
                                        new Document("$size",
                                                "$total_customers"))
                                .append("avg_payment_amount",
                                        new Document("$divide",
                                                Arrays.asList("$total_payments",
                                                        new Document("$size",
                                                                "$total_customers"))))),
                new Document("$sort", new Document("total_customers", -1)));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query13(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("customer");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup", new Document("from", "address")
                        .append("localField", "address_id")
                        .append("foreignField", "address_id")
                        .append("as", "address")),
                new Document("$unwind", "$address"),
                new Document("$lookup", new Document("from", "city")
                        .append("localField", "address.city_id")
                        .append("foreignField", "city_id")
                        .append("as", "city")),
                new Document("$unwind", "$city"),
                new Document("$lookup", new Document("from", "country")
                        .append("localField", "city.country_id")
                        .append("foreignField", "country_id")
                        .append("as", "country")),
                new Document("$unwind", "$country"),
                new Document("$lookup", new Document("from", "payment")
                        .append("localField", "customer_id")
                        .append("foreignField", "customer_id")
                        .append("as", "payments")),
                new Document("$unwind", "$payments"),
                new Document("$group", new Document("_id", "$country.country")
                        .append("total_revenue", new Document("$sum", "$payments.amount"))),
                new Document("$project", new Document("_id", 0)
                        .append("country", "$_id")
                        .append("total_revenue", 1)));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query14(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("customer");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup", new Document()
                        .append("from", "address")
                        .append("localField", "address_id")
                        .append("foreignField", "address_id")
                        .append("as", "address")),
                new Document("$unwind", "$address"),
                new Document("$lookup", new Document()
                        .append("from", "city")
                        .append("localField", "address.city_id")
                        .append("foreignField", "city_id")
                        .append("as", "city")),
                new Document("$unwind", "$city"),
                new Document("$group", new Document()
                        .append("_id", "$customer_id")
                        .append("first_name", new Document("$first", "$first_name"))
                        .append("last_name", new Document("$first", "$last_name"))
                        .append("num_cities", new Document("$sum", 1))),
                new Document("$match", new Document("num_cities", new Document("$gt", 1))),
                new Document("$project", new Document()
                        .append("_id", 0)
                        .append("customer_id", "$_id")
                        .append("first_name", 1)
                        .append("last_name", 1)
                        .append("num_cities", 1)));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query15(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("customer");

        List<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document("$lookup", new Document()
                .append("from", "rental")
                .append("localField", "customer_id")
                .append("foreignField", "customer_id")
                .append("as", "rentals")));
        pipeline.add(new Document("$unwind", "$rentals"));
        pipeline.add(new Document("$lookup", new Document()
                .append("from", "inventory")
                .append("localField", "rentals.inventory_id")
                .append("foreignField", "inventory_id")
                .append("as", "inventory")));
        pipeline.add(new Document("$unwind", "$inventory"));
        pipeline.add(new Document("$lookup", new Document()
                .append("from", "store")
                .append("localField", "inventory.store_id")
                .append("foreignField", "store_id")
                .append("as", "store")));
        pipeline.add(new Document("$unwind", "$store"));
        pipeline.add(new Document("$group", new Document()
                .append("_id", "$customer_id")
                .append("customer_id", new Document("$first", "$customer_id"))
                .append("first_name", new Document("$first", "$first_name"))
                .append("last_name", new Document("$first", "$last_name"))
                .append("num_stores", new Document("$addToSet", "$store.store_id"))));
        pipeline.add(new Document("$project", new Document()
                .append("_id", 0)
                .append("customer_id", 1)
                .append("first_name", 1)
                .append("last_name", 1)
                .append("num_stores", new Document("$size", "$num_stores"))));
        pipeline.add(new Document("$match", new Document()
                .append("num_stores", new Document("$gt", 1))));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query16(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("city");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup", new Document()
                        .append("from", "country")
                        .append("localField", "country_id")
                        .append("foreignField", "country_id")
                        .append("as", "country")),
                new Document("$unwind", "$country"),
                new Document("$lookup", new Document()
                        .append("from", "address")
                        .append("localField", "city_id")
                        .append("foreignField", "city_id")
                        .append("as", "address")),
                new Document("$unwind", "$address"),
                new Document("$lookup", new Document()
                        .append("from", "customer")
                        .append("localField", "address.address_id")
                        .append("foreignField", "address_id")
                        .append("as", "customer")),
                new Document("$unwind", "$customer"),
                new Document("$lookup", new Document()
                        .append("from", "rental")
                        .append("localField", "customer.customer_id")
                        .append("foreignField", "customer_id")
                        .append("as", "rental")),
                new Document("$unwind", "$rental"),
                new Document("$lookup", new Document()
                        .append("from", "payment")
                        .append("localField", "rental.rental_id")
                        .append("foreignField", "rental_id")
                        .append("as", "payment")),
                new Document("$unwind", "$payment"),
                new Document("$group", new Document()
                        .append("_id", new Document()
                                .append("city_id", "$city_id")
                                .append("country", "$country.country"))
                        .append("total_revenue", new Document()
                                .append("$sum", "$payment.amount"))),
                new Document("$sort", new Document("total_revenue", -1)),
                new Document("$limit", 5),
                new Document("$project", new Document()
                        .append("_id", 0)
                        .append("city", "$_id.city_id")
                        .append("country", "$_id.country")
                        .append("total_revenue", 1)));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query17(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("customer");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup",
                        new Document("from", "address")
                                .append("localField", "address_id")
                                .append("foreignField", "address_id")
                                .append("as", "address")),
                new Document("$unwind", "$address"),
                new Document("$lookup",
                        new Document("from", "city")
                                .append("localField", "address.city_id")
                                .append("foreignField", "city_id")
                                .append("as", "city")),
                new Document("$unwind", "$city"),
                new Document("$lookup",
                        new Document("from", "rental")
                                .append("localField", "customer_id")
                                .append("foreignField", "customer_id")
                                .append("as", "rental")),
                new Document("$unwind", "$rental"),
                new Document("$group",
                        new Document("_id",
                                new Document("customer_id", "$customer_id")
                                        .append("city_id", "$city.city_id"))
                                .append("customer_id",
                                        new Document("$first", "$customer_id"))
                                .append("first_name",
                                        new Document("$first", "$first_name"))
                                .append("last_name",
                                        new Document("$first", "$last_name"))
                                .append("city",
                                        new Document("$first", "$city.city"))
                                .append("num_rentals",
                                        new Document("$sum", 1))),
                new Document("$sort",
                        new Document("num_rentals", -1)));

        collection.aggregate(pipeline);
        return 1;
    }

    public int query18(List<String> args) throws Exception {
        MongoCollection<Document> inventoryCollection = this.database.getCollection("inventory");
        MongoCollection<Document> filmCollection = this.database.getCollection("film");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup", new Document()
                        .append("from", "rental")
                        .append("localField", "inventory_id")
                        .append("foreignField", "inventory_id")
                        .append("as", "rentals")),
                new Document("$group", new Document()
                        .append("_id", "$film_id")
                        .append("rental_count", new Document()
                                .append("$sum", new Document()
                                        .append("$size", "$rentals")))),
                new Document("$match", new Document()
                        .append("rental_count", new Document()
                                .append("$gt", 20))),
                new Document("$project", new Document()
                        .append("_id", "0")
                        .append("film_id", "$_id")));

        for (Document doc : inventoryCollection.aggregate(pipeline)) {
            filmCollection.updateMany(
                    new Document("film_id", doc.getInteger("film_id")),
                    new Document("$set", new Document("replacement_cost", "0")));
        }

        return 1;
    }

    public int query19(List<String> args) throws Exception {
        MongoCollection<Document> collection = this.database.getCollection("film");

        collection.updateMany(
                new Document("release_year", "2006"),
                new Document("$mul", new Document("$toDouble: rental_rate", 1.5)));
        return 1;
    }

    public int query20(List<String> args) throws Exception {
        MongoCollection<Document> filmColl = this.database.getCollection("film");

        List<Document> pipeline = Arrays.asList(
                new Document("$lookup",
                        new Document("from", "inventory")
                                .append("localField", "film_id")
                                .append("foreignField", "film_id")
                                .append("as", "inventory")),
                new Document("$unwind", "$inventory"),
                new Document("$lookup",
                        new Document("from", "rental")
                                .append("localField", "inventory.inventory_id")
                                .append("foreignField", "inventory_id")
                                .append("as", "rental")),
                new Document("$unwind", "$rental"),
                new Document("$lookup",
                        new Document("from", "film_category")
                                .append("localField", "film_id")
                                .append("foreignField", "film_id")
                                .append("as", "film_category")),
                new Document("$unwind", "$film_category"),
                new Document("$lookup",
                        new Document("from", "category")
                                .append("localField", "film_category.category_id")
                                .append("foreignField", "category_id")
                                .append("as", "category")),
                new Document("$unwind", "$category"),
                new Document("$match",
                        new Document("category.name", "Action")),
                new Document("$group",
                        new Document("_id", "$film_id")
                                .append("count", new Document("$sum", 1))),
                new Document("$match",
                        new Document("count", new Document("$gte", 10))),
                new Document("$project",
                        new Document("_id", "0")
                                .append("film_id", "$_id")));

        MongoCursor<Document> cursor = filmColl.aggregate(pipeline).iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            int filmId = doc.getInteger("film_id");

            filmColl.updateOne(new Document("film_id", filmId),
                    new Document("$mul", new Document("$toDouble: rental_rate", 1.1)));
        }

        cursor.close();
        return 1;
    }

    public int query21(List<String> args) throws Exception {
        List<Document> zeroRentalFilms = new ArrayList<>();
        MongoCursor<Document> cursor = this.database.getCollection("inventory").aggregate(Arrays.asList(
                new Document("$lookup", new Document("from", "rental")
                        .append("localField", "inventory_id")
                        .append("foreignField", "inventory_id")
                        .append("as", "rentals")),
                new Document("$group", new Document("_id", "$film_id")
                        .append("count", new Document("$sum",
                                new Document("$size", "$rentals")))),
                new Document("$match", new Document("count", "0")),
                new Document("$project", new Document("_id", "1")))).iterator();
        while (cursor.hasNext()) {
            zeroRentalFilms.add(cursor.next());
        }
        cursor.close();

        List<Integer> filmIds = zeroRentalFilms.stream().map(d -> d.getInteger("_id"))
                .collect(Collectors.toList());
        this.database.getCollection("film").deleteMany(new Document("film_id", new Document("$nin", filmIds)));
        return 1;
    }
}