#! /usr/bin/bash

sudo -u postgres -H -- psql -d postgres -U postgres -c "\i /home/phyo/abaw5/DatabaseAuditor/app/src/postgresql/data/db_init.sql"