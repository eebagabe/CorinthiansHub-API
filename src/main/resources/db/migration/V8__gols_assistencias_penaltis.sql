-- gols e assistencias: jogos oficiais pelo Corinthians (ogol.com.br, 18/10/2026)
-- penaltis defendidos: so goleiros, pelo Corinthians, em jogo (Transfermarkt); NULL = nao informado
ALTER TABLE jogadores
    ADD COLUMN gols                INTEGER,
    ADD COLUMN assistencias        INTEGER,
    ADD COLUMN penaltis_defendidos INTEGER;

UPDATE jogadores j
SET gols = v.gols, assistencias = v.assistencias, penaltis_defendidos = v.penaltis
FROM (VALUES
    (1, 0, 0, 7),
    (2, 6, 11, NULL),
    (3, 2, 0, NULL),
    (4, 2, 1, NULL),
    (5, 2, 1, NULL),
    (7, 6, 4, NULL),
    (8, 18, 33, NULL),
    (9, 84, 24, NULL),
    (10, 20, 16, NULL),
    (11, 1, 6, NULL),
    (13, 14, 4, NULL),
    (14, 6, 2, NULL),
    (18, 6, 3, NULL),
    (19, 3, 6, NULL),
    (20, 0, 0, NULL),
    (21, 11, 10, NULL),
    (23, 0, 0, NULL),
    (26, 1, 2, NULL),
    (29, 0, 1, NULL),
    (31, 2, 1, NULL),
    (35, 2, 1, NULL),
    (37, 3, 3, NULL),
    (40, 0, 0, 0),
    (46, 0, 6, NULL),
    (48, NULL, NULL, NULL),
    (49, 8, 2, NULL),
    (51, 0, 0, 0),
    (52, 2, 2, NULL),
    (54, 0, 0, NULL),
    (55, 0, 0, NULL),
    (56, 7, 2, NULL),
    (61, 2, 2, NULL),
    (77, 2, 1, NULL),
    (80, 2, 0, NULL)
) AS v(numero, gols, assistencias, penaltis)
WHERE j.numero = v.numero;
