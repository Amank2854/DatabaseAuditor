-- \i /home/phyo/abaw5/DatabaseAuditor/postgresql/db_init.sql

-- alter user postgres with password 'postgres';
drop database if exists dvdrental;
create database dvdrental owner postgres;
\c dvdrental postgres

create type mpaa_rating as enum ('G', 'PG', 'PG-13', 'R', 'NC-17');
create domain year as integer constraint year_check check ((value >= 1901) AND (value <= 2155));

create table if not exists customer (
    customer_id integer not null,
    store_id smallint not null,
    first_name character varying(45) not null,
    last_name character varying(45) not null,
    email character varying(50),
    address_id smallint not null,
    activebool boolean default true not null,
    create_date date default ('now'::text)::date not null,
    last_update timestamp without time zone default now(),
    active integer
);

create table if not exists actor (
    actor_id integer not null,
    first_name character varying(45) not null,
    last_name character varying(45) not null,
    last_update timestamp without time zone default now() not null
);

create table if not exists category (
    category_id integer not null,
    name character varying(25) not null,
    last_update timestamp without time zone default now() not null
);

create table if not exists film (
    film_id integer not null,
    title character varying(255) not null,
    description text,
    release_year year,
    language_id smallint not null,
    rental_duration smallint default 3 not null,
    rental_rate numeric(4,2) default 4.99 not null,
    length smallint,
    replacement_cost numeric(5,2) default 19.99 not null,
    rating mpaa_rating default 'G'::mpaa_rating,
    last_update timestamp without time zone default now() not null,
    special_features text[],
    fulltext tsvector not null
);

create table if not exists film_actor (
    actor_id smallint not null,
    film_id smallint not null,
    last_update timestamp without time zone default now() not null
);

create table if not exists film_category (
    film_id smallint not null,
    category_id smallint not null,
    last_update timestamp without time zone default now() not null
);

create table if not exists address (
    address_id integer not null,
    address character varying(50) not null,
    address2 character varying(50),
    district character varying(20) not null,
    city_id smallint not null,
    postal_code character varying(10),
    phone character varying(20) not null,
    last_update timestamp without time zone default now() not null
);

create table if not exists city (
    city_id integer not null,
    city character varying(50) not null,
    country_id smallint not null,
    last_update timestamp without time zone default now() not null
);

create table if not exists country (
    country_id integer not null,
    country character varying(50) not null,
    last_update timestamp without time zone default now() not null
);

create table if not exists inventory (
    inventory_id integer not null,
    film_id smallint not null,
    store_id smallint not null,
    last_update timestamp without time zone default now() not null
);

create table if not exists language (
    language_id integer not null,
    name character(20) not null,
    last_update timestamp without time zone default now() not null
);

create table if not exists payment (
    payment_id integer not null,
    customer_id smallint not null,
    staff_id smallint not null,
    rental_id integer not null,
    amount numeric(5,2) not null,
    payment_date timestamp without time zone not null
);

create table if not exists rental (
    rental_id integer not null,
    rental_date timestamp without time zone not null,
    inventory_id integer not null,
    customer_id smallint not null,
    return_date timestamp without time zone,
    staff_id smallint not null,
    last_update timestamp without time zone default now() not null
);

create table if not exists staff (
    staff_id integer not null,
    first_name character varying(45) not null,
    last_name character varying(45) not null,
    address_id smallint not null,
    email character varying(50),
    store_id smallint not null,
    active boolean default true not null,
    username character varying(16) not null,
    password character varying(40),
    last_update timestamp without time zone default now() not null,
    picture bytea
);

create table if not exists store (
    store_id integer not null,
    manager_staff_id smallint not null,
    address_id smallint not null,
    last_update timestamp without time zone default now() not null
);

\copy actor (actor_id, first_name, last_name) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3057.dat';
\copy address (address_id, address, address2, district, city_id, postal_code, phone) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3065.dat';
\copy category (category_id, name) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3059.dat';
\copy city (city_id, city, country_id) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3067.dat';
\copy country (country_id, country) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3069.dat';
\copy customer (customer_id, store_id, first_name, last_name, email, address_id, activebool, active) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3055.dat';
\copy film (film_id, title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features, fulltext) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3061.dat';
\copy film_actor (actor_id, film_id) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3062.dat';
\copy film_category (film_id, category_id) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3063.dat';
\copy inventory (inventory_id, film_id, store_id) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3071.dat';
\copy language (language_id, name) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3073.dat';
\copy payment (payment_id, customer_id, staff_id, rental_id, amount, payment_date) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3075.dat';
\copy rental (rental_id, rental_date, inventory_id, customer_id, return_date, staff_id) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3077.dat';
\copy staff (staff_id, first_name, last_name, address_id, email, store_id, active, username, password, picture) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3079.dat';
\copy store (store_id, manager_staff_id, address_id) from '/home/phyo/abaw5/DatabaseAuditor/postgresql/data/3081.dat';