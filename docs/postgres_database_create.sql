-- postgres_database_create.sql
-- (this script is designed to be run under the 'postgres' database superuser)

-- ------------------------------------------------------------------------------------------------
-- Before executing this script, do the following:
-- 
-- cd /opt
-- mkdir pgsql17   (major number changes are incompatible - use new directory for new version)
-- chmod 700 pgsql17
-- cd pgsql17
-- mkdir pgarchive pgbackup pgcluster pglog pgschemas
-- chmod 700 pgarchive pgbackup pgcluster pglog pgschemas
--
-- /opt/postgresql/bin/initdb -D /opt/pgcluster     (this creates a new cluster)
--
-- /opt/postgresql/bin/pg_ctl -D /opt/pgcluster start
-- /opt/postgresql/bin/psql postgresql://localhost:5432/postgres
-- postgres=# alter role postgres with password '*';    (use the actual password)
--   (CTRL-D to exit)
-- /opt/postgresql/binpg_ctl -D /opt/pgcluster stop
--
-- cd /opt/pgsql17/pgcluster
-- vi pg_hba.conf
--   Change "method" on all lines from "trust" to "scram-sha-256"
--   Add these lines to allow connections from hosts with a need to access the database):
--   host    math            all             [ip-address]/32        scram-sha-256
--
-- cd /opt/pgsql17/pgcluster
-- vi postgresql.conf
--   log_directory = '/opt/pgsql17/pglog'
--   archive_command = 'cp %p /opt/pgsql17/pgarchive/%f'
-- ------------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------------
-- Creates the 'math' role and database and the tablespaces that will hold database objects

CREATE ROLE math WITH LOGIN CREATEDB CREATEROLE INHERIT PASSWORD '*';   (use the actual password)

CREATE DATABASE math WITH OWNER = math ENCODING = 'UTF8';
CREATE DATABASE math_dev WITH OWNER = math ENCODING = 'UTF8';
CREATE DATABASE math_test WITH OWNER = math ENCODING = 'UTF8';

DROP SCHEMA IF EXISTS math.public;
DROP SCHEMA IF EXISTS math_dev.public;
DROP SCHEMA IF EXISTS math_test.public;

CREATE TABLESPACE primary_ts OWNER math LOCATION '/opt/pgsql17/pgschemas/primary';
CREATE TABLESPACE analytics_ts OWNER math LOCATION '/opt/pgsql17/pgschemas/analytics';
CREATE TABLESPACE term_ts OWNER math LOCATION '/opt/pgsql17/pgschemas/term';

-- To connect as 'math':
-- /opt/postgresql/bin/psql -d math[_dev|_test] -U math
