MATCH (n)
DETACH DELETE n;

DROP CONSTRAINT actor_id IF EXISTS;
DROP CONSTRAINT address_id IF EXISTS;
DROP CONSTRAINT category_id IF EXISTS;
DROP CONSTRAINT city_id IF EXISTS;
DROP CONSTRAINT country_id IF EXISTS;
DROP CONSTRAINT customer_id IF EXISTS;
DROP CONSTRAINT film_id IF EXISTS;
DROP CONSTRAINT inventory_id IF EXISTS;
DROP CONSTRAINT language_id IF EXISTS;
DROP CONSTRAINT payment_id IF EXISTS;
DROP CONSTRAINT rental_id IF EXISTS;
DROP CONSTRAINT staff_id IF EXISTS;
DROP CONSTRAINT store_id IF EXISTS;

LOAD CSV WITH HEADERS FROM 'file:///payment.csv' AS row
MERGE (payment:Payment {payment_id: row.payment_id})
  ON CREATE SET	payment.customer_id = row.customer_id,
			payment.staff_id = row.staff_id,
			payment.rental_id = row.rental_id,
			payment.amount = row.amount,
			payment.payment_date = row.payment_date;

LOAD CSV WITH HEADERS FROM 'file:///inventory.csv' AS row
MERGE (inventory:Inventory {inventory_id: row.inventory_id})
  ON CREATE SET	inventory.film_id = row.film_id,
			inventory.store_id = row.store_id,
			inventory.last_update = row.last_update;

LOAD CSV WITH HEADERS FROM 'file:///film.csv' AS row
MERGE (film:Film {film_id: row.film_id})
  ON CREATE SET	film.title = row.title,
			film.description = row.description,
			film.release_year = row.release_year,
			film.language_id = row.language_id,
			film.rental_duration = row.rental_duration,
			film.rental_rate = row.rental_rate,
			film.length = row.length,
			film.replacement_cost = row.replacement_cost,
			film.rating = row.rating,
			film.last_update = row.last_update,
			film.special_features = row.special_features,
			film.fulltext = row.fulltext;

LOAD CSV WITH HEADERS FROM 'file:///city.csv' AS row
MERGE (city:City {city_id: row.city_id})
  ON CREATE SET	city.city = row.city,
			city.country_id = row.country_id,
			city.last_update = row.last_update;

LOAD CSV WITH HEADERS FROM 'file:///category.csv' AS row
MERGE (category: Category {category_id: row.category_id})
  ON CREATE SET	category.name = row.name,
			category.last_update = row.last_update;


LOAD CSV WITH HEADERS FROM 'file:///actor.csv' AS row
MERGE (actor:Actor {actor_id: row.actor_id})
  ON CREATE SET	actor.first_name = row.first_name,
			actor.last_name = row.last_name,
			actor.last_update = row.last_update;

LOAD CSV WITH HEADERS FROM 'file:///rental.csv' AS row
MERGE (rental:Rental {rental_id: row.rental_id})
  ON CREATE SET rental.rental_date = row.rental_date,
			rental.inventory_id = row.inventory_id,
			rental.customer_id = row.customer_id,
			rental.return_date = row.return_date,
			rental.staff_id = row.staff_id,
			rental.last_update = row.last_update;

LOAD CSV WITH HEADERS FROM 'file:///store.csv' AS row
MERGE (store:Store {store_id: row.store_id})
  ON CREATE SET store.manager_staff_id = row.manager_staff_id,
			store.address_id = row.address_id,
			store.last_update = row.last_update;

LOAD CSV WITH HEADERS FROM 'file:///staff.csv' AS row
MERGE (staff:Staff {staff_id: row.staff_id})
  ON CREATE SET staff.first_name = row.first_name,
			staff.last_name = row.last_name,
			staff.address_id = row.address_id,
			staff.email = row.email,
			staff.store_id = row.store_id,
			staff.active = row.active,
			staff.username = row.username,
			staff.password = row.password,
			staff.last_update = row.last_update,
			staff.picture = row.picture;

LOAD CSV WITH HEADERS FROM 'file:///language.csv' AS row
MERGE (language:Language {language_id: row.language_id})
  ON CREATE SET language.name = row.name,
			language.last_update = row.last_update;

LOAD CSV WITH HEADERS FROM 'file:///country.csv' AS row
MERGE (country:Country {country_id: row.country_id})
  ON CREATE SET country.country = row.country,
			country.last_update = row.last_update;

LOAD CSV WITH HEADERS FROM 'file:///customer.csv' AS row
MERGE (customer:Customer {customer_id: row.customer_id})
  ON CREATE SET customer.first_name = row.first_name,
			customer.last_name = row.last_name,
			customer.address_id = row.address_id,
			customer.email = row.email,
			customer.store_id = row.store_id,
			customer.activebool = row.activebool,
			customer.create_date = row.create_date,
			customer.last_update = row.last_update,
			customer.active = row.active;

LOAD CSV WITH HEADERS FROM 'file:///address.csv' AS row
MERGE (address:Address {address_id: row.address_id})
  ON CREATE SET address.address = row.address,
			address.address2 = row.address2,
			address.district = row.district,
			address.city_id = row.city_id,
			address.postal_code = row.postal_code,
			address.phone = row.phone,
			address.last_update = row.last_update;
LOAD CSV WITH HEADERS FROM 'file:///film_actor.csv' AS row
MATCH (actor:Actor {actor_id: row.actor_id})
MATCH (film:Film {film_id: row.film_id})
MERGE (actor)-[o:acted_in]->(film);

LOAD CSV WITH HEADERS FROM 'file:///film_category.csv' AS row
MATCH (film:Film {film_id: row.film_id})
MATCH (category:Category {category_id: row.category_id})
MERGE (film)-[:belongs_to]->(category);

LOAD CSV WITH HEADERS FROM 'file:///film.csv' AS row
MATCH (film:Film {film_id: row.film_id})
MATCH (language:Language {language_id: row.language_id})
MERGE (film)-[:has]->(language);

LOAD CSV WITH HEADERS FROM 'file:///inventory.csv' AS row
MATCH (film:Film {film_id: row.film_id})
MATCH (inventory:Inventory {inventory_id: row.inventory_id})
MERGE (inventory)-[:contains]->(film);

LOAD CSV WITH HEADERS FROM 'file:///rental.csv' AS row
MATCH (rental:Rental {rental_id: row.rental_id})
MATCH (inventory:Inventory {inventory_id: row.inventory_id})
MATCH (customer:Customer {customer_id: row.customer_id})
MATCH (staff:Staff {staff_id: row.staff_id})
MERGE (rental)-[:rented]->(inventory)
MERGE (rental)-[:rented_by]->(customer)
MERGE (rental)-[:handled_by]->(staff);

LOAD CSV WITH HEADERS FROM 'file:///payment.csv' AS row
MATCH (rental:Rental {rental_id: row.rental_id})
MATCH (customer:Customer {customer_id: row.customer_id})
MATCH (payment:Payment {payment_id: row.payment_id})
MATCH (staff:Staff {staff_id: row.staff_id})
MERGE (payment)-[:for]->(rental)
MERGE (payment)-[:received_by]->(staff)
MERGE (payment)-[:done_by]->(customer);

LOAD CSV WITH HEADERS FROM 'file:///staff.csv' AS row
MATCH (store:Store {store_id: row.store_id})
MATCH (address:Address {address_id: row.address_id})
MERGE (staff)-[:works_at]->(store)
MERGE (staff)-[:has_address]->(address);

LOAD CSV WITH HEADERS FROM 'file:///customer.csv' AS row
MATCH (customer:Customer {customer_id: row.customer_id})
MATCH (address:Address {address_id: row.address_id})
MERGE (customer)-[:has_address]->(address);

LOAD CSV WITH HEADERS FROM 'file:///store.csv' AS row
MATCH (store:Store {store_id: row.store_id})
MATCH (address:Address {address_id: row.address_id})
MERGE (store)-[:has_address]->(address);

LOAD CSV WITH HEADERS FROM 'file:///address.csv' AS row
MATCH (city:City {city_id: row.city_id})
MATCH (address:Address {address_id: row.address_id})
MERGE (address)-[:is_in_city]->(city);

LOAD CSV WITH HEADERS FROM 'file:///city.csv' AS row
MATCH (city:City {city_id: row.city_id})
MATCH (country:Country {country_id: row.country_id})
MERGE (city)-[:is_in_country]->(country);
:exit