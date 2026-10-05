-- Schema inicial: estoque (Kardex), custo medio ponderado, vendas com precos congelados.
-- Todas as datas em TIMESTAMPTZ (gravadas em UTC pela aplicacao).

CREATE TABLE categoria (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMPTZ  NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);
CREATE UNIQUE INDEX ux_categoria_nome ON categoria (lower(nome));

CREATE TABLE produto (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(150)  NOT NULL,
    sku             VARCHAR(60)   NOT NULL,
    categoria_id    BIGINT        REFERENCES categoria (id),
    custo_medio     NUMERIC(12,4) NOT NULL DEFAULT 0,
    preco_venda     NUMERIC(12,2) NOT NULL,
    estoque_minimo  INTEGER       NOT NULL DEFAULT 0,
    ativo           BOOLEAN       NOT NULL DEFAULT TRUE,
    version         BIGINT        NOT NULL DEFAULT 0,
    criado_em       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    atualizado_em   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT ck_produto_custo   CHECK (custo_medio >= 0),
    CONSTRAINT ck_produto_preco   CHECK (preco_venda >= 0),
    CONSTRAINT ck_produto_minimo  CHECK (estoque_minimo >= 0)
);
CREATE UNIQUE INDEX ux_produto_sku ON produto (upper(sku));
CREATE INDEX ix_produto_categoria ON produto (categoria_id);
CREATE INDEX ix_produto_ativo ON produto (ativo);

CREATE TABLE venda (
    id              BIGSERIAL PRIMARY KEY,
    criado_em       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    subtotal        NUMERIC(12,2) NOT NULL,
    desconto_total  NUMERIC(12,2) NOT NULL DEFAULT 0,
    total           NUMERIC(12,2) NOT NULL,
    status          VARCHAR(20)   NOT NULL DEFAULT 'CONCLUIDA',
    CONSTRAINT ck_venda_valores CHECK (subtotal >= 0 AND desconto_total >= 0 AND total >= 0),
    CONSTRAINT ck_venda_status  CHECK (status IN ('CONCLUIDA', 'CANCELADA'))
);
CREATE INDEX ix_venda_criado_em ON venda (criado_em);

-- Itens: precos e custo CONGELADOS no instante da venda.
CREATE TABLE item_venda (
    id                        BIGSERIAL PRIMARY KEY,
    venda_id                  BIGINT        NOT NULL REFERENCES venda (id),
    produto_id                BIGINT        NOT NULL REFERENCES produto (id),
    quantidade                INTEGER       NOT NULL,
    preco_venda_praticado     NUMERIC(12,2) NOT NULL,   -- preco unitario
    custo_medio_snapshot      NUMERIC(12,4) NOT NULL,   -- custo medio unitario no instante
    desconto_item             NUMERIC(12,2) NOT NULL DEFAULT 0,  -- desconto TOTAL da linha em R$
    tipo_desconto             VARCHAR(10)   NOT NULL DEFAULT 'VALOR',
    valor_desconto_informado  NUMERIC(12,2) NOT NULL DEFAULT 0,  -- R$ ou % como digitado
    CONSTRAINT ck_item_qtd       CHECK (quantidade > 0),
    CONSTRAINT ck_item_preco     CHECK (preco_venda_praticado >= 0),
    CONSTRAINT ck_item_custo     CHECK (custo_medio_snapshot >= 0),
    CONSTRAINT ck_item_desconto  CHECK (desconto_item >= 0
                                        AND desconto_item <= quantidade * preco_venda_praticado),
    CONSTRAINT ck_item_tipo_desc CHECK (tipo_desconto IN ('VALOR', 'PERCENTUAL'))
);
CREATE INDEX ix_item_venda_venda   ON item_venda (venda_id);
CREATE INDEX ix_item_venda_produto ON item_venda (produto_id);

-- Kardex: append-only. quantidade COM SINAL (entrada > 0, saida < 0, ajuste +/-).
CREATE TABLE movimentacao_estoque (
    id                BIGSERIAL PRIMARY KEY,
    produto_id        BIGINT        NOT NULL REFERENCES produto (id),
    tipo              VARCHAR(10)   NOT NULL,
    origem            VARCHAR(30)   NOT NULL,
    quantidade        INTEGER       NOT NULL,
    custo_unitario    NUMERIC(12,4) NOT NULL,
    custo_medio_apos  NUMERIC(12,4) NOT NULL,
    saldo_apos        INTEGER       NOT NULL,
    venda_id          BIGINT        REFERENCES venda (id),
    motivo            VARCHAR(255),
    criado_em         TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT ck_mov_tipo    CHECK (tipo IN ('ENTRADA', 'SAIDA', 'AJUSTE')),
    CONSTRAINT ck_mov_origem  CHECK (origem IN ('COMPRA', 'VENDA', 'AJUSTE_INVENTARIO')),
    CONSTRAINT ck_mov_qtd     CHECK (quantidade <> 0),
    CONSTRAINT ck_mov_sinal   CHECK (
        (tipo = 'ENTRADA' AND quantidade > 0) OR
        (tipo = 'SAIDA'   AND quantidade < 0) OR
        (tipo = 'AJUSTE')),
    CONSTRAINT ck_mov_saldo   CHECK (saldo_apos >= 0)   -- nunca estoque negativo
);
CREATE INDEX ix_mov_produto_data ON movimentacao_estoque (produto_id, criado_em);
CREATE INDEX ix_mov_venda        ON movimentacao_estoque (venda_id);

-- Imutabilidade: Kardex e itens de venda nao podem ser alterados nem apagados.
CREATE FUNCTION fn_bloquear_alteracao() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'Tabela % e imutavel (operacao % nao permitida)', TG_TABLE_NAME, TG_OP;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_mov_imutavel
    BEFORE UPDATE OR DELETE ON movimentacao_estoque
    FOR EACH ROW EXECUTE FUNCTION fn_bloquear_alteracao();

CREATE TRIGGER trg_item_venda_imutavel
    BEFORE UPDATE OR DELETE ON item_venda
    FOR EACH ROW EXECUTE FUNCTION fn_bloquear_alteracao();
