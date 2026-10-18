-- Valores passam a ser guardados direto em real, alimentados manualmente (apps, noticias, imprensa).
ALTER TABLE jogadores
    ADD COLUMN salario_mensal_reais BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN valor_mercado_reais  BIGINT;

COMMENT ON COLUMN jogadores.salario_mensal_reais IS 'estimativa (o clube nao divulga salarios); 0 = nao encontrado';

-- Quem nao esta no levantamento de 2026-10-18 mantem o valor anterior, convertido pela PTAX de 2026-10-16
UPDATE jogadores
SET salario_mensal_reais = round(salario_anual_estimado_usd * 5.0826 / 12),
    valor_mercado_reais  = round(valor_mercado_euros * 5.7088);

ALTER TABLE jogadores
    DROP COLUMN salario_anual_estimado_usd,
    DROP COLUMN valor_mercado_euros,
    DROP COLUMN valor_mercado_atualizado_em;

DROP TABLE cotacoes;

INSERT INTO jogadores (numero, nome, posicao, nacionalidade, atualizado_em)
VALUES (48, 'Gui Amorim', 'Meio-campista', 'BRA', '2026-10-18');

-- Levantamento manual de 2026-10-18: salario mensal | valor de mercado | fim de contrato (NULL = nao encontrado)
UPDATE jogadores j
SET salario_mensal_reais = v.salario,
    valor_mercado_reais  = v.mercado,
    fim_contrato         = v.contrato,
    atualizado_em        = '2026-10-18'
FROM (VALUES
    (1,  375000,  11000000, DATE '2028-12-31'),
    (40, 60000,   NULL,     NULL),
    (51, 60000,   NULL,     NULL),
    (2,  350000,  8000000,  DATE '2028-12-31'),
    (20, 150000,  1300000,  DATE '2029-12-31'),
    (21, 175000,  7000000,  DATE '2027-12-31'),
    (26, 250000,  NULL,     DATE '2026-12-31'),
    (46, 220000,  700000,   DATE '2026-12-31'),
    (55, 33000,   NULL,     NULL),
    (3,  550000,  955000,   DATE '2027-12-31'),
    (5,  450000,  1200000,  DATE '2026-12-31'),
    (13, 330000,  NULL,     DATE '2027-12-31'),
    (4,  60000,   NULL,     NULL),
    (8,  404500,  12000000, DATE '2028-12-31'),
    (77, 380000,  NULL,     DATE '2026-12-31'),
    (80, 350000,  NULL,     DATE '2027-12-31'),
    (14, 300000,  4000000,  DATE '2028-12-31'),
    (52, 240000,  NULL,     DATE '2026-12-31'),
    (35, 228000,  NULL,     NULL),
    (7,  204000,  22000000, DATE '2029-12-31'),
    (49, 70000,   16000000, DATE '2029-12-31'),
    (48, 65000,   NULL,     NULL),
    (29, 0,       3000000,  DATE '2026-12-31'),
    (23, 0,       1100000,  DATE '2026-12-31'),
    (9,  1900000, 23000000, DATE '2030-12-31'),
    (18, 385000,  3000000,  DATE '2028-12-31'),
    (19, 289750,  900000,   DATE '2026-12-31'),
    (56, 120000,  7000000,  NULL),
    (31, 50000,   NULL,     NULL),
    (61, 40000,   4000000,  NULL),
    (37, 0,       4500000,  DATE '2026-12-31')
) AS v (numero, salario, mercado, contrato)
WHERE j.numero = v.numero;
