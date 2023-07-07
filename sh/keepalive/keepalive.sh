#!/bin/bash

if [ $# -eq 0 ]; then echo "usage: keepalive [command]"; exit 1; fi
if ! type "$1" >/dev/null 2>&1; then echo "keepalive: $1: command not found"; exit 1; fi

while true; do
  "$@"
  code=$?
  echo "keepalive: $1 exited with code $code at $(date "+%FT%T%z")"
  if [ $code -eq 0 ]; then
    break
  fi
done
