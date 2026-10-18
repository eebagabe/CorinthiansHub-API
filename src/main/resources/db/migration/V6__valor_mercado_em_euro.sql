-- Valor de mercado e levantado em euro (padrao Transfermarkt); o site do cliente mostra em real pela PTAX.
ALTER TABLE jogadores RENAME COLUMN valor_mercado_reais TO valor_mercado_euros;

-- Anotacoes livres de onde veio cada dado (app, noticia, link), preenchidas no backoffice
ALTER TABLE jogadores ADD COLUMN fontes TEXT;

CREATE TABLE cotacoes (
    moeda     VARCHAR(3)     PRIMARY KEY,
    valor_brl NUMERIC(10, 4) NOT NULL,
    data      DATE           NOT NULL
);

-- PTAX de venda (fechamento) do Banco Central; atualizada automaticamente pela aplicacao
INSERT INTO cotacoes (moeda, valor_brl, data) VALUES ('EUR', 5.7088, '2026-10-16');
