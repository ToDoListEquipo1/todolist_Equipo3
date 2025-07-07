#!/bin/bash

# Script para realizar un backup completo de la base de datos PostgreSQL "mads"
# desde el contenedor Docker "postgres-db".
# El archivo de backup se guardará en el directorio compartido /mi-host dentro del contenedor,
# que debe estar mapeado a una ruta del host.

# Uso:
#   bash backup_postgres.sh

CONTAINER=postgres-db
DB_NAME=mads
DB_USER=mads
DB_PASSWORD=mads
BACKUP_DIR=/mi-host
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE=backup-$DATE.sql

# Ejecutar el backup usando pg_dump dentro del contenedor
docker exec -e PGPASSWORD=$DB_PASSWORD $CONTAINER \
  pg_dump -U $DB_USER -F p $DB_NAME > $BACKUP_DIR/$BACKUP_FILE

echo "Backup realizado: $BACKUP_DIR/$BACKUP_FILE"