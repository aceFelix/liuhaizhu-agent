#!/bin/sh
set -e

BACKEND_HOST=${BACKEND_HOST:-host.docker.internal}

sed -i "s|\${BACKEND_HOST}|${BACKEND_HOST}|g" /etc/nginx/conf.d/default.conf

exec "$@"
