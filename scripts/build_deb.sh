#!/bin/bash

#
#            ____           _       __    __     _____ __            ___
#           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
#          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
#         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
#        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
#                         /____/
#     Copyright (C) 2023 Dwight Studio
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

# Constantes
VER=0.1.5
RELEASE=BETA
BF=$HOME/debbuild/
CPF=$BF/jarmemu/
US=$CPF/usr/share/
UB=$CPF/usr/bin/

# Clean
rm -r $BF
rm ./target/JArmEmu-${VER}-${RELEASE}_all.deb

# Compression
mkdir -p $CPF/DEBIAN/
mkdir -p $US/applications/
mkdir -p $US/java/jarmemu/
cp ./package/linux/deb/control $CPF/DEBIAN/
cp ./package/linux/common/fr.dwightstudio.jarmemu.gui.JArmEmuApplication.desktop $US/applications/
cp -r ./package/linux/common/icons $US/
cp -r ./package/linux/common/mime $US/
cp ./package/linux/common/jarmemu $UB/
cp ./target/JArmEmu.jar $US/java/jarmemu/
cp -r ./target/lib/ $US/java/jarmemu/

# Build
dpkg-deb --root-owner-group --build $CPF || exit 1

# Clean et rendu
cp $BF/jarmemu.deb ./target/JArmEmu-${VER}-${RELEASE}_all.deb
rm -r $BF

