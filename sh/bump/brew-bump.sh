#!/bin/bash
if ! [ -d "$(xcode-select -p)" ]; then echo "Xcode at path '$(xcode-select -p)' not found"; exit 1; fi
brew update && HOMEBREW_NO_INSTALL_CLEANUP=1 brew upgrade
brew cleanup
