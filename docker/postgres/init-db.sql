-- ==============================================================================
-- SCRIPT DE INICIALIZACIÓN DE BASES DE DATOS LOCALES (POSTGRESQL)
-- Este script se ejecuta automáticamente en el arranque inicial del contenedor.
-- ==============================================================================

-- Nota: Como el contenedor se levanta con POSTGRES_USER=neondb_owner (o el valor
-- definido en el .env), estas bases de datos serán propiedad de ese usuario por defecto.

CREATE DATABASE users_db;
CREATE DATABASE transactions_db;
CREATE DATABASE catalog_db;

-- Confirmación de creación en logs
\echo 'Bases de datos para Blockbuster (users_db, transactions_db, catalog_db) inicializadas exitosamente.';
