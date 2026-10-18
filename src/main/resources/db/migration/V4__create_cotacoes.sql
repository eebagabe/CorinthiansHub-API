-- Valores das fontes ficam na moeda original (EUR/USD); a API converte para real com a cotacao daqui.
CREATE TABLE cotacoes (
    moeda     VARCHAR(3)     PRIMARY KEY,
    valor_brl NUMERIC(10, 4) NOT NULL,
    data      DATE           NOT NULL,
    fonte     VARCHAR(100)   NOT NULL
);

-- PTAX de venda (boletim de fechamento) do Banco Central
INSERT INTO cotacoes (moeda, valor_brl, data, fonte) VALUES
    ('USD', 5.0826, '2026-10-16', 'PTAX Banco Central'),
    ('EUR', 5.7088, '2026-10-16', 'PTAX Banco Central');
