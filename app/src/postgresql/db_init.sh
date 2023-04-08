#! /usr/bin/bash

sudo -u postgres -H -- psql -d postgres -U postgres -c "\i app/src/postgresql/db_init.sql"