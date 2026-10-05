-- Multi-loja (tenant) + usuarios.
-- Dados existentes migram para a "Loja Padrao" (id 1).
-- ADD COLUMN ... DEFAULT 1 preenche sem UPDATE (os triggers de imutabilidade bloqueariam um UPDATE).

CREATE TABLE loja (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(150) NOT NULL,
    nicho       VARCHAR(60)  NOT NULL DEFAULT 'OUTROS',
    documento   VARCHAR(20),
    telefone    VARCHAR(20),
    cidade      VARCHAR(80),
    uf          CHAR(2),
    ativo       BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE usuario (
    id          BIGSERIAL PRIMARY KEY,
    loja_id     BIGINT       NOT NULL REFERENCES loja (id),
    nome        VARCHAR(120) NOT NULL,
    email       VARCHAR(160) NOT NULL,
    senha_hash  VARCHAR(100) NOT NULL,
    papel       VARCHAR(20)  NOT NULL,
    ativo       BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT ck_usuario_papel CHECK (papel IN ('DONO', 'VENDEDOR'))
);
CREATE UNIQUE INDEX ux_usuario_email ON usuario (lower(email));
CREATE INDEX ix_usuario_loja ON usuario (loja_id);

INSERT INTO loja (id, nome, nicho) VALUES (1, 'Loja Padrao', 'OUTROS');
SELECT setval(pg_get_serial_sequence('loja', 'id'), 1);

ALTER TABLE categoria            ADD COLUMN loja_id BIGINT NOT NULL DEFAULT 1 REFERENCES loja (id);
ALTER TABLE produto              ADD COLUMN loja_id BIGINT NOT NULL DEFAULT 1 REFERENCES loja (id);
ALTER TABLE venda                ADD COLUMN loja_id BIGINT NOT NULL DEFAULT 1 REFERENCES loja (id);
ALTER TABLE movimentacao_estoque ADD COLUMN loja_id BIGINT NOT NULL DEFAULT 1 REFERENCES loja (id);

ALTER TABLE categoria            ALTER COLUMN loja_id DROP DEFAULT;
ALTER TABLE produto              ALTER COLUMN loja_id DROP DEFAULT;
ALTER TABLE venda                ALTER COLUMN loja_id DROP DEFAULT;
ALTER TABLE movimentacao_estoque ALTER COLUMN loja_id DROP DEFAULT;

-- Unicidade passa a ser por loja.
DROP INDEX ux_categoria_nome;
CREATE UNIQUE INDEX ux_categoria_nome ON categoria (loja_id, lower(nome));
DROP INDEX ux_produto_sku;
CREATE UNIQUE INDEX ux_produto_sku ON produto (loja_id, upper(sku));

CREATE INDEX ix_produto_loja ON produto (loja_id);
CREATE INDEX ix_venda_loja_data ON venda (loja_id, criado_em);
CREATE INDEX ix_mov_loja_data ON movimentacao_estoque (loja_id, criado_em);
