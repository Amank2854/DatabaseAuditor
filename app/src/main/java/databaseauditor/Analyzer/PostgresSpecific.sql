--  Total No. of Rentals Per Customer
SELECT customer.customer_id, COUNT(rental.rental_id) as total_rentals
FROM rental
        JOIN customer ON rental.customer_id = customer.customer_id
GROUP BY customer.customer_id
ORDER BY total_rentals DESC;

-- Total Revenue by Each Film
SELECT film.film_id, film.title, SUM(payment.amount) as total_revenue
FROM rental
        JOIN inventory ON rental.inventory_id = inventory.inventory_id
        JOIN film ON inventory.film_id = film.film_id
        JOIN payment ON rental.rental_id = payment.rental_id
GROUP BY film.film_id
ORDER BY total_revenue DESC;

-- Total Revenue by Each Store
SELECT store.store_id, SUM(payment.amount) as total_revenue
FROM rental
        JOIN inventory ON rental.inventory_id = inventory.inventory_id
        JOIN store ON inventory.store_id = store.store_id
        JOIN payment ON rental.rental_id = payment.rental_id
GROUP BY store.store_id
ORDER BY total_revenue DESC;

-- Total Revenue by Each Staff
SELECT staff.staff_id, SUM(payment.amount) as total_revenue
FROM rental
        JOIN inventory ON rental.inventory_id = inventory.inventory_id
        JOIN store ON inventory.store_id = store.store_id
        JOIN staff ON store.manager_staff_id = staff.staff_id
        JOIN payment ON rental.rental_id = payment.rental_id
GROUP BY staff.staff_id
ORDER BY total_revenue DESC;

-- Total Spent by Each Customer
SELECT customer.customer_id, customer.first_name, customer.last_name, SUM(payment.amount) as total_revenue
FROM rental
        JOIN customer ON rental.customer_id = customer.customer_id
        JOIN payment ON rental.rental_id = payment.rental_id
GROUP BY customer.customer_id
ORDER BY total_revenue DESC;

-- Total Spent by Each Customer in Each Store
SELECT customer.customer_id, customer.first_name, customer.last_name, store.store_id, SUM(payment.amount) as total_revenue
FROM rental
        JOIN customer ON rental.customer_id = customer.customer_id
        JOIN inventory ON rental.inventory_id = inventory.inventory_id
        JOIN store ON inventory.store_id = store.store_id
        JOIN payment ON rental.rental_id = payment.rental_id
GROUP BY customer.customer_id, store.store_id
ORDER BY total_revenue DESC;

-- Average Rental Duration by Each Film Category
SELECT category.name, AVG(rental.return_date - rental.rental_date) AS avg_rental_duration
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
    c.first_name,
    c.last_name,
    SUM(p.amount) AS total_spent
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
    c.name,
    SUM(p.amount) AS total_revenue
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
    c.name,
    COUNT(*) AS num_films
FROM
    category c
        JOIN film_category fc ON c.category_id = fc.category_id
GROUP BY
    c.category_id
ORDER BY
    num_films DESC
LIMIT 10;