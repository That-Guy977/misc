#!/bin/bash
if ! [ -d "$HOME/.nvm" ]; then echo "NVM at path '$HOME/.nvm' not found"; exit 1; fi
source "$HOME/.nvm/nvm.sh"
versions="$(nvm ls --no-colors --no-alias)"
major="$(echo "$versions" | sed -E 's/^(->)? +v([[:digit:]]+)\.[[:digit:]]+\.[[:digit:]]+ \*$/\2/' | uniq)"
for v in $major; do
  nvm install "$v" --latest-npm
  extra="$(nvm ls "$v" --no-colors | sed -E -e '$d' -e 's/^ *v([[:digit:].]*) \*$/\1/')"
  for e in $extra; do
    nvm uninstall "$e"
  done
done
nvm install "lts/*"
nvm cache clear
echo "To switch active version, run:"
echo "  nvm use node"
