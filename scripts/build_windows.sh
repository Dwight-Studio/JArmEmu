#!/bin/bash

# Aller à la racine du dépôt
cd $(git rev-parse --show-toplevel) || exit 1

sudo docker run --rm -i -v "$PWD:/work" amake/innosetup ./package/windows/jarmsetup.iss
