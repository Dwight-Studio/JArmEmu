#!/bin/bash

# Aller à la racine du dépôt
cd $(git rev-parse --show-toplevel) || exit 1

# Constantes
VER=0.1.1
RELEASE=ALPHA
BF=$HOME/debbuild/
CPF=$BF/jarmemu/
US=$CPF/usr/share/

# Clean
rm -r $BF
#rm ./target/jarmemu-$VER-1.noarch.rpm

# Compression
mkdir -p $CPF/DEBIAN/
mkdir -p $US/applications/
mkdir -p $US/java/jarmemu/
cp ./package/linux/deb/control $CPF/DEBIAN/
cp ./package/linux/common/fr.dwightstudio.jarmemu.gui.JArmEmuApplication.desktop $US/applications/
cp -r ./package/linux/common/icons $US/
cp -r ./package/linux/common/mime $US/
cp ./target/JArmEmu.jar $US/java/jarmemu/
cp -r ./target/lib/ $US/java/jarmemu/

# Build
dpkg-deb --root-owner-group --build $CPF

# Clean et rendu
cp $BF/jarmemu.deb ./target/jarmemu-${VER}-${RELEASE}_all.deb
rm -r $BF

