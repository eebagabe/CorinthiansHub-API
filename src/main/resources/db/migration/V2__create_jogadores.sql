CREATE TABLE jogadores (
    id                            BIGSERIAL PRIMARY KEY,
    numero                        INT          NOT NULL UNIQUE,
    nome                          VARCHAR(100) NOT NULL,
    posicao                       VARCHAR(30)  NOT NULL,
    nacionalidade                 VARCHAR(3)   NOT NULL,
    foto_url                      TEXT,
    nome_completo                 VARCHAR(200),
    nascimento                    DATE,
    jogos                         INT,
    titular                       INT,
    jogos_sem_sofrer_gols         INT,
    jogos_selecao                 INT,
    valor_mercado_euros           BIGINT,
    valor_mercado_atualizado_em   DATE,
    fim_contrato                  DATE,
    -- estimativa (o clube nao divulga salarios); 0 = nao encontrado
    salario_anual_estimado_usd    BIGINT       NOT NULL DEFAULT 0,
    atualizado_em                 DATE         NOT NULL
);

CREATE TABLE jogador_titulos (
    jogador_id BIGINT       NOT NULL REFERENCES jogadores (id) ON DELETE CASCADE,
    ordem      INT          NOT NULL,
    titulo     VARCHAR(200) NOT NULL,
    PRIMARY KEY (jogador_id, ordem)
);
