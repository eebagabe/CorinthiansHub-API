CREATE TABLE usuarios (
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(100) NOT NULL,
    papel      VARCHAR(20)  NOT NULL,
    criado_em  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
