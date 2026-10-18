"""Gera scripts/data/gols.json: gols e assistencias pelo Corinthians (ogol.com.br) de todo o elenco
e penaltis defendidos pelo Corinthians dos goleiros (Transfermarkt).

Rodar de dentro de backend/: python3 scripts/fetch_gols.py
O elenco vem da API publicada; o seed V8__gols_assistencias_penaltis.sql saiu deste JSON.
"""
import html
import json
import re
import time
import urllib.parse
import urllib.request
from datetime import date

API = "https://api-corinthians.ghalves.com/api/jogadores"
# goleiros: numero da camisa -> id transfermarkt
GOLEIROS_TM = {1: 574901, 40: 1005656, 51: 1005653}
UA = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/130 Safari/537.36",
      "Accept-Language": "pt-BR"}
CORINTHIANS_OGOL = 2234
# ids ja conferidos em fetch_stats.py; o resto sai da busca do ogol
OGOL = {1: 569633, 40: 829356, 51: 790126, 2: 620330, 3: 115106, 4: 716454, 5: 183983, 13: 217334, 20: 610547,
        21: 687719, 26: 366634, 46: 543405, 49: 978426, 55: 1060560}


def texto(url):  # igual ao de fetch_stats.py (que roda a coleta ao ser importado)
    time.sleep(2)
    with urllib.request.urlopen(urllib.request.Request(url, headers=UA)) as r:
        s = r.read().decode("utf-8", "ignore")
    s = re.sub(r"<script.*?</script>|<style.*?</style>", "", s, flags=re.S)
    return re.sub(r"\s*\|[\s|]*", "|", html.unescape(re.sub(r"<[^>]+>", "|", s)))


def ogol_id(nome):
    time.sleep(2)
    t = urllib.request.urlopen(urllib.request.Request(
        "https://www.ogol.com.br/pesquisa?search_txt=" + urllib.parse.quote_plus(nome), headers=UA)).read().decode()
    m = re.search(r'href="/jogador/[^/"]+/(\d+)\?search=1"', t)
    return int(m.group(1)) if m else None


def totais(ogol):
    """(jogos, gols, assistencias) oficiais pelo Corinthians; linha total: J V E D M T RU GM ASS ..."""
    t = texto(f"https://www.ogol.com.br/estatisticas/x/p{ogol}-t{CORINTHIANS_OGOL}?filter_match=in_with&grp=1")
    if "Resumo da temporada" not in t:
        return 0, 0, 0
    resumo = t.split("Resumo da temporada", 1)[1].split("|[|J|", 1)[0]
    linha = resumo.split("|")[-12:]  # ultima linha (total), mesmo sem linhas por competicao
    return int(linha[0]), int(linha[7]), int(linha[8])


def penaltis_defendidos(tm_id):
    with urllib.request.urlopen(urllib.request.Request(
            f"https://www.transfermarkt.com.br/x/elfmeterstatistik/spieler/{tm_id}", headers=UA)) as r:
        s = r.read().decode("utf-8", "ignore")
    if "Pênaltis defendidos" not in s:
        return 0
    defendidos = s.split("Pênaltis defendidos", 1)[1].split("Pênaltis não defendidos", 1)[0]
    # 3o title da linha = clube do goleiro naquele jogo
    return sum(1 for tr in re.findall(r"<tr[^>]*>(.*?)</tr>", defendidos, re.S)
               if re.findall(r'title="([^"]+)"', tr)[2:3] == ["SC Corinthians"])


saida = {}
for j in json.load(urllib.request.urlopen(urllib.request.Request(API, headers=UA))):
    oid = OGOL.get(j["numero"]) or ogol_id(j["nome"])
    jogos, gols, assist = totais(oid) if oid else (0, None, None)
    if abs(jogos - (j["jogos"] or 0)) > 5 and j["nomeCompleto"]:  # busca achou homonimo: tenta o nome completo
        oid = ogol_id(j["nomeCompleto"])
        jogos, gols, assist = totais(oid) if oid else (0, None, None)
    if jogos == 0 and j["jogos"]:  # ogol sem os jogos dele: melhor nao informar que informar zero
        gols = assist = None
    d = {"nome": j["nome"], "ogol": oid, "jogosOgol": jogos, "jogosApi": j["jogos"], "gols": gols, "assistencias": assist}
    if j["numero"] in GOLEIROS_TM:
        d["penaltisDefendidos"] = penaltis_defendidos(GOLEIROS_TM[j["numero"]])
    saida[str(j["numero"])] = d
    print(j["numero"], d)

with open("scripts/data/gols.json", "w", encoding="utf-8") as f:
    json.dump({"atualizadoEm": date.today().isoformat(), "jogadores": saida}, f, ensure_ascii=False, indent=2)
