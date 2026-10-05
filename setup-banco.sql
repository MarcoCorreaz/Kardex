-- Execute como superusuario (postgres), conectado ao banco "postgres".
-- No psql:  psql -U postgres -f setup-banco.sql
-- (CREATE DATABASE nao roda dentro de transacao; no pgAdmin execute cada comando separadamente.)

CREATE ROLE estoque WITH LOGIN PASSWORD 'estoque';
CREATE DATABASE estoque OWNER estoque ENCODING 'UTF8';
ALTER DATABASE estoque SET timezone TO 'UTC';
