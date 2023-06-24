#!/bin/bash
if ! [ -d "$HOME/.nvm" ]; then echo "NVM at path '$HOME/.nvm' not found"; exit 1; fi
. $HOME/.nvm/nvm.sh
versions="$(nvm ls --no-colors)"
lts=$(echo "$versions" | sed -nE '/^lts\/[[:alpha:]]+/{
  s/^lts\/[[:alpha:]]+ -> v([[:digit:].]+) (\*|\(-> N\/A\))$/\1/
  p
}')
inst=$(echo "$versions" | sed -nE '/^(->)? +/{
  s/^(->)? +v([[:digit:].]+) \*$/\2/
  p
}')
for v in $lts; do
  if [[ ! "$inst" =~ "$v" ]]; then
    nvm install "$v" --latest-npm
  fi
done
for v in $inst; do
  if [[ ! "$lts" =~ "$v" ]]; then
    nvm uninstall "$v"
  fi
done
nvm use default
