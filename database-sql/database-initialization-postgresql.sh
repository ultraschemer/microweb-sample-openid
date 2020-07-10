#!/usr/bin/env bash
psql --command "CREATE USER microwebsample WITH PASSWORD 'microwebsample';"
createdb -O microwebsample microwebsample
psql --command "GRANT ALL PRIVILEGES ON DATABASE microwebsample TO microwebsample"
psql --command 'CREATE EXTENSION "uuid-ossp"' microwebsample
psql --command 'CREATE EXTENSION "pgcrypto"' microwebsample