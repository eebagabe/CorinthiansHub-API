"""Gera scripts/data/detalhes.json com numeros oficiais (ogol.com.br) e mercado/selecao/titulos (Transfermarkt).

Rodar de dentro de backend/: python3 scripts/fetch_stats.py
Referencia para a automacao futura no backend: o seed V3__seed_jogadores.sql saiu destes JSONs.
"""
import html
import json
import re
import time
import urllib.request
from datetime import date

UA = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/130 Safari/537.36",
      "Accept-Language": "pt-BR"}
CORINTHIANS_OGOL = 2234

# numero da camisa: (id ogol, id transfermarkt, goleiro?)
JOGADORES = {
    1: (569633, 574901, True),
    40: (829356, 1005656, True),
    51: (790126, 1005653, True),
    2: (620330, 594226, False),
    3: (115106, 149498, False),
    4: (716454, 1005650, False),
    5: (183983, 175792, False),
    13: (217334, 208681, False),
    20: (610547, 657743, False),
    21: (687719, 670021, False),
    26: (366634, 268506, False),
    46: (543405, 730708, False),
    55: (1060560, 1497709, False),
}


def texto(url):
    time.sleep(2)  # sem pressa: evita bloqueio por excesso de requisicoes
    with urllib.request.urlopen(urllib.request.Request(url, headers=UA)) as r:
        s = r.read().decode("utf-8", "ignore")
    s = re.sub(r"<script.*?</script>|<style.*?</style>", "", s, flags=re.S)
    return re.sub(r"\s*\|[\s|]*", "|", html.unescape(re.sub(r"<[^>]+>", "|", s)))


def campo(t, rotulo):
    m = re.search(re.escape(rotulo) + r"\|([^|]+)", t)
    assert m, f"campo {rotulo!r} nao encontrado"
    return m.group(1)


def totais_corinthians(ogol_id, filtro=""):
    """(jogos, titular) em competicoes oficiais pelo Corinthians; filtro extra ex.: '&eve=sgols'."""
    t = texto(f"https://www.ogol.com.br/estatisticas/x/p{ogol_id}-t{CORINTHIANS_OGOL}?filter_match=in_with&grp=1{filtro}")
    resumo = t.split("Resumo da temporada", 1)[1].split("|[|J|", 1)[0] if "Resumo da temporada" in t else ""
    linha = resumo.rsplit("|detalhes|", 1)[-1].split("|")  # ultima linha = total: J V E D M T RU ...
    return (int(linha[0]), int(linha[5])) if resumo else (0, 0)


def transfermarkt(tm_id):
    p = texto(f"https://www.transfermarkt.com.br/x/profil/spieler/{tm_id}")
    valor = re.search(r"€([\d.,]+)\|(mi|mil)\b", p)
    alteracao = re.search(r"Última alteração: (\d{2})/(\d{2})/(\d{4})", p)
    contrato = re.search(r"Contrato até:\|(\d{2})/(\d{2})/(\d{4})", p)
    selecao = re.search(r"jogador de Seleção:\|([^|]+)\|Jogos de Seleção/Gols:\|(\d+)", p)

    e = texto(f"https://www.transfermarkt.com.br/x/erfolge/spieler/{tm_id}")
    tokens = e.split("Títulos & Temporadas|", 1)[1].split("|Links Rápidos", 1)[0].split("|") if "Títulos & Temporadas|" in e else []
    titulos, atual = [], None
    for tok in tokens:
        if re.match(r"\d+x ", tok):
            atual = re.sub(r"^\d+x ", "", tok)
        elif tok == "SC Corinthians" and atual and not atual.startswith("Participante"):
            titulos.append(atual)

    return {
        # Transfermarkt mostra "12.00 mi." ou "500 mil" em euros
        "valorMercadoEuros": round(float(valor.group(1).replace(",", "")) * (1_000_000 if valor.group(2) == "mi" else 1_000)) if valor else None,
        "valorMercadoAtualizadoEm": f"{alteracao.group(3)}-{alteracao.group(2)}-{alteracao.group(1)}" if alteracao else None,
        "fimContrato": f"{contrato.group(3)}-{contrato.group(2)}-{contrato.group(1)}" if contrato else None,
        # so conta selecao principal (ignora Brasil S20, U23...)
        "jogosSelecao": int(selecao.group(2)) if selecao and not re.search(r"[SU]\d\d", selecao.group(1)) else 0,
        "titulosCorinthians": titulos,
    }


detalhes = {}
for numero, (ogol_id, tm_id, goleiro) in JOGADORES.items():
    perfil = texto(f"https://www.ogol.com.br/jogador/x/{ogol_id}")
    jogos, titular = totais_corinthians(ogol_id)
    d = {
        "nomeCompleto": campo(perfil, "Nome"),
        "nascimento": campo(perfil, "Data de Nascimento").split(" ")[0],
        "jogos": jogos,
        "titular": titular,
    }
    if goleiro:
        d["jogosSemSofrerGols"] = totais_corinthians(ogol_id, "&eve=sgols")[0]
    d |= transfermarkt(tm_id)
    detalhes[str(numero)] = d
    print(numero, d)

with open("scripts/data/detalhes.json", "w", encoding="utf-8") as f:
    json.dump({"atualizadoEm": date.today().isoformat(), "jogadores": detalhes}, f, ensure_ascii=False, indent=2)
