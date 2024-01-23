#!/bin/bash

#
#            ____           _       __    __     _____ __            ___
#           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
#          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
#         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
#        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
#                         /____/
#     Copyright (C) 2024 Dwight Studio
#
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     (at your option) any later version.
#
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
#
#     You should have received a copy of the GNU General Public License
#     along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

# Aller à la racine du dépôt
cd $(git rev-parse --show-toplevel) || exit 1
CWD=$(pwd)

# Constantes
VER=0.1.11
RELEASE=1

rpmdev-setuptree
TMP=$(mktemp -d -q)
CPF=$TMP/JArmEmu

# Copie
mkdir -p $CPF/java/JArmEmu/
cp ./package/linux/common/fr.dwightstudio.JArmEmu.desktop $CPF
cp ./package/linux/common/jarmemu $CPF/
cp -r ./package/linux/common/icons $CPF/
cp -r ./package/linux/common/mime $CPF/
cp -r ./package/linux/common/metainfo $CPF/
cp ./package/linux/flatpak/jarmemu-flatpak $CPF/
cp ./target/JArmEmu.jar $CPF/java/JArmEmu/
cp -r ./target/lib/ $CPF/java/JArmEmu/

# Compression
cd $TMP/ || exit 1
zip -r JArmEmu *

# Rendu et clean
mv JArmEmu.zip $CWD/target/JArmEmu-$VER-$RELEASE.flatpak-arch-source.zip
sha256sum $CWD/target/JArmEmu-$VER-$RELEASE.flatpak-arch-source.zip > $CWD/target/flatpak-arch-source.checksum
cd $CWD/ || exit 1
rm -r $TMP
