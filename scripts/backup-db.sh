#!/usr/bin/env bash
# Dump diario do Postgres de producao (VPS). Uso via cron:
#   15 3 * * * ~/corinthianshub-api/scripts/backup-db.sh >> ~/corinthianshub-backups/backup.log 2>&1
# Backups ficam fora da pasta do deploy, que e recriada a cada atualizacao.
set -euo pipefail
cd "$(dirname "$0")/.."
set -a
. ./.env.production
set +a
destino=~/corinthianshub-backups
mkdir -p "$destino"
arquivo="$destino/corinthianshub-$(date +%Y%m%d-%H%M%S).sql.gz"
docker exec corinthianshub-db pg_dump -U "$DB_USER" -d db_corinthians_hub --no-owner | gzip > "$arquivo"
echo "$(date -Is) backup ok: $arquivo"
# Mantem os ultimos 14 dias
find "$destino" -name 'corinthianshub-*.sql.gz' -mtime +14 -print -delete
