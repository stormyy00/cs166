#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
#cs166_createdb $USER"_project_phase_3_DB"
cs166_psql -p $PGPORT $USER"_project_phase_3_DB" < $DIR/../src/create_tables.sql
cs166_psql -p $PGPORT $USER"_project_phase_3_DB" < $DIR/../src/create_indexes.sql
cs166_psql -p $PGPORT $USER"_project_phase_3_DB" < $DIR/../src/create_triggers.sql
cs166_psql -p $PGPORT $USER"_project_phase_3_DB" < $DIR/../src/load_data.sql

# cs166_psql $USER'_project_phase_3_DB' < $DIR/../src/create_tables.sql
# # psql -h localhost -p $PGPORT $DB_NAME < $DIR/../src/create_tables.sql > /dev/null
# sleep 5

# echo "Query time without indexes"
# cat <(echo '\timing') java/scripts/compile.sh | psql -h localhost -p $PGPORT $USER'_project_phase_3_DB' | grep Time | awk -F "Time" '{print "Query" FNR $2;}'

# cs166_psql $USER'_project_phase_3_DB' < $DIR/../src/create_tables.sql

# psql -h localhost -p $PGPORT $USER'_project_phase_3_DB' < $DIR/../src/create_tables.sql> /dev/null

# echo "Query time with indexes"
# cat <(echo '\timing') java/scripts/compile.sh | psql -h localhost -p $PGPORT $USER'_project_phase_3_DB' | grep Time | awk -F "Time" '{print "Query" FNR $2;}'
