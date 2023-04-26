--  Total No. of Rentals Per Customer
SELECT customer.customer_id, COUNT(rental.rental_id) as total_rentals
FROM rental
        JOIN customer ON rental.customer_id = customer.customer_id
GROUP BY customer.customer_id
ORDER BY total_rentals DESC;

-- Total Revenue by Each Film
SELECT film.film_id,SUM(payment.amount::INTEGER) as total_revenue
FROM rental
        JOIN inventory ON rental.inventory_id = inventory.inventory_id
        JOIN film ON inventory.film_id = film.film_id
        JOIN payment ON rental.rental_id = payment.rental_id
GROUP BY film.film_id
ORDER BY total_revenue DESC;

-- Total Revenue by Each Store
SELECT store.store_id, SUM(payment.amount::INTEGER) as total_revenue
FROM rental
        JOIN inventory ON rental.inventory_id = inventory.inventory_id
        JOIN store ON inventory.store_id = store.store_id
        JOIN payment ON rental.rental_id = payment.rental_id
GROUP BY store.store_id
ORDER BY total_revenue DESC;

-- Total Revenue by Each Staff
SELECT staff.staff_id, SUM(payment.amount::INTEGER) as total_revenue
FROM rental
        JOIN inventory ON rental.inventory_id = inventory.inventory_id
        JOIN store ON inventory.store_id = store.store_id
        JOIN staff ON store.manager_staff_id = staff.staff_id
        JOIN payment ON rental.rental_id = payment.rental_id
GROUP BY staff.staff_id
ORDER BY total_revenue DESC;

-- Total Spent by Each Customer
SELECT customer.customer_id, SUM(payment.amount::INTEGER) as total_revenue
FROM rental
        JOIN customer ON rental.customer_id = customer.customer_id
        JOIN payment ON rental.rental_id = payment.rental_id
GROUP BY customer.customer_id
ORDER BY total_revenue DESC;

-- Total Spent by Each Customer in Each Store
SELECT customer.customer_id,  store.store_id, SUM(payment.amount::INTEGER) as total_revenue
FROM rental
        JOIN customer ON rental.customer_id = customer.customer_id
        JOIN inventory ON rental.inventory_id = inventory.inventory_id
        JOIN store ON inventory.store_id = store.store_id
        JOIN payment ON rental.rental_id = payment.rental_id
GROUP BY customer.customer_id, store.store_id
ORDER BY total_revenue DESC;

-- Average Rental Duration by Each Film Category
SELECT category.category_id, AVG(rental.return_date::DOUBLE PRECISION - rental.rental_date::DOUBLE PRECISION) AS avg_rental_duration
FROM category
        INNER JOIN film_category ON category.category_id = film_category.category_id
        INNER JOIN inventory ON film_category.film_id = inventory.film_id
        INNER JOIN rental ON inventory.inventory_id = rental.inventory_id
GROUP BY category.category_id
ORDER BY avg_rental_duration DESC;

-- Number of Rentals Per Film Category and Language
SELECT f.rating, f.language_id, COUNT(*) AS num_rentals
FROM film f
        JOIN inventory i ON f.film_id = i.film_id
        JOIN rental r ON i.inventory_id = r.inventory_id
GROUP BY f.rating, f.language_id;

-- Title of All Films rented by a customer who has rented more than n films
SELECT
    f.title
FROM
    film f
        JOIN inventory i ON f.film_id = i.film_id
        JOIN rental r ON i.inventory_id = r.inventory_id
        JOIN customer c ON r.customer_id = c.customer_id
WHERE
        c.customer_id IN (
        SELECT
            customer_id
        FROM
            rental
        GROUP BY
            customer_id
        HAVING
                COUNT(*) > 45
    );

-- Top 10 Customers who have spent the most money
SELECT
    c.customer_id,
    SUM(p.amount::INTEGER) AS total_spent
FROM
    customer c
        JOIN rental r ON c.customer_id = r.customer_id
        JOIN payment p ON r.rental_id = p.rental_id
GROUP BY
    c.customer_id
ORDER BY
    total_spent DESC
LIMIT 10;

-- Top 5 Categories with the Highest Revenue
SELECT
    c.category_id,
    SUM(p.amount::INTEGER) AS total_revenue
FROM
    category c
        JOIN film_category fc ON c.category_id = fc.category_id
        JOIN inventory i ON fc.film_id = i.film_id
        JOIN rental r ON i.inventory_id = r.inventory_id
        JOIN payment p ON r.rental_id = p.rental_id
GROUP BY
    c.category_id
ORDER BY
    total_revenue DESC
LIMIT 5;

-- Top 10 Categories with the Most Number of Films
SELECT
    c.category_id,
    COUNT(*) AS num_films
FROM
    category c
        JOIN film_category fc ON c.category_id = fc.category_id
GROUP BY
    c.category_id
ORDER BY
    num_films DESC
LIMIT 10;

--Number of Customers and Average Payment Amount by Country
SELECT country.country, COUNT(DISTINCT customer.customer_id) AS total_customers, AVG(payment.amount::DOUBLE PRECISION) AS avg_payment_amount
FROM customer
        JOIN address ON customer.address_id = address.address_id
        JOIN city ON address.city_id = city.city_id
        JOIN country ON city.country_id = country.country_id
        JOIN payment ON customer.customer_id = payment.customer_id
GROUP BY country.country;

-- Total Amount generated by Rentals in Each Country
SELECT country.country, SUM(payment.amount::INTEGER) AS total_revenue
FROM customer
        JOIN address ON customer.address_id = address.address_id
        JOIN city ON address.city_id = city.city_id
        JOIN country ON city.country_id = country.country_id
        JOIN payment ON customer.customer_id = payment.customer_id
GROUP BY country.country;

-- number of customers who have rented films from more than one city
SELECT
    c.customer_id,
    COUNT(DISTINCT ci.city_id) AS num_cities
FROM
    customer c
        JOIN address a ON c.address_id = a.address_id
        JOIN city ci ON a.city_id = ci.city_id
        JOIN rental r ON c.customer_id = r.customer_id
GROUP BY
    c.customer_id
HAVING
        COUNT(DISTINCT ci.city_id) > 1;

-- number of customers who have rented films from more than one store
SELECT
    c.customer_id,
    COUNT(DISTINCT s.store_id) AS num_stores
FROM
    customer c
        JOIN rental r ON c.customer_id = r.customer_id
        JOIN inventory i ON r.inventory_id = i.inventory_id
        JOIN store s ON i.store_id = s.store_id
GROUP BY
    c.customer_id
HAVING
        COUNT(DISTINCT s.store_id) > 1;

--  top 5 cities with the most rental revenue and their respective country names
SELECT
    c.city_id,
    co.country,
    SUM(p.amount::INTEGER) AS total_revenue
FROM
    city c
        JOIN country co ON c.country_id = co.country_id
        JOIN address a ON c.city_id = a.city_id
        JOIN customer cu ON a.address_id = cu.address_id
        JOIN rental r ON cu.customer_id = r.customer_id
        JOIN payment p ON r.rental_id = p.rental_id
GROUP BY
    c.city_id, co.country
ORDER BY
    total_revenue DESC
LIMIT 5;


-- customers who have rented the most number of films in each city
SELECT
    c.customer_id,
    ci.city_id,
    COUNT(*) AS num_rentals
FROM
    customer c
        JOIN address a ON c.address_id = a.address_id
        JOIN city ci ON a.city_id = ci.city_id
        JOIN rental r ON c.customer_id = r.customer_id
GROUP BY
    c.customer_id, ci.city_id
ORDER BY
    num_rentals DESC;

-- films that have been rented for the most number of times in each category
SELECT category.name, film.title, rental_count
FROM (
        SELECT film.film_id, COUNT(*) AS rental_count
        FROM rental
                JOIN inventory ON rental.inventory_id = inventory.inventory_id
                JOIN film ON inventory.film_id = film.film_id
        GROUP BY film.film_id
     ) AS rental_counts
        JOIN film_category ON rental_counts.film_id = film_category.film_id
        JOIN category ON film_category.category_id = category.category_id
        JOIN film ON rental_counts.film_id = film.film_id
        JOIN (
    SELECT category_id, MAX(rental_count::INTEGER) AS max_rental_count
    FROM (
            SELECT film.film_id, COUNT(*) AS rental_count
            FROM rental
                    JOIN inventory ON rental.inventory_id = inventory.inventory_id
                    JOIN film ON inventory.film_id = film.film_id
            GROUP BY film.film_id
         ) AS rental_counts
            JOIN film_category ON rental_counts.film_id = film_category.film_id
    GROUP BY category_id
) AS max_rental_counts
            ON category.category_id = max_rental_counts.category_id
                AND rental_count = max_rental_counts.max_rental_count
ORDER BY category.name;


-- Update Queries

--Set the replacement_cost to 0 for all DVDs that have been rented more than 20 times

UPDATE film SET replacement_cost = 0 WHERE film_id IN (
    SELECT inventory.film_id
    FROM inventory
        JOIN rental ON inventory.inventory_id = rental.inventory_id
    GROUP BY inventory.film_id
    HAVING COUNT(*) > 20
);

-- Increase rental rate of all Films released in 2006 by 50%
-- UPDATE film
-- SET rental_rate = rental_rate * 1.5
-- WHERE release_year = '2006';


-- -- Update rental rate equal to the average rental rate of all films in the same category
-- UPDATE film
-- SET rental_rate = (
--         SELECT AVG(rental_rate::INTEGER)
--         FROM film
--             INNER JOIN film_category
--                 ON film.film_id = film_category.film_id
--         WHERE film_category.category_id
--                   IN (SELECT category_id
--                       FROM film_category
--                       WHERE film_id = film.film_id))
-- WHERE EXISTS (SELECT *
--               FROM film_category
--               WHERE film_category.film_id = film.film_id);