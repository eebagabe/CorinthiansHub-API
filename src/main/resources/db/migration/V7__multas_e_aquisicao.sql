-- Valor de mercado sai; entram multa rescisoria (nacional em real, internacional em euro) e custo de aquisicao.
ALTER TABLE jogadores DROP COLUMN valor_mercado_euros;

ALTER TABLE jogadores
    ADD COLUMN multa_nacional_reais      BIGINT,
    ADD COLUMN multa_internacional_euros BIGINT,
    ADD COLUMN valor_aquisicao_reais     BIGINT;

COMMENT ON COLUMN jogadores.valor_aquisicao_reais IS '0 = chegou sem custo; NULL = nao informado';
