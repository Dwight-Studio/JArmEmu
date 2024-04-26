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

cd ./base/src/main/resources/fr/dwightstudio/jarmemu/base/medias || exit 1

for x in 16 32 64 128 256 512
do
	magick logo.png -resize ${x}x${x} favicon@${x}.png
done

# Aller à la racine du dépôt
cd $(git rev-parse --show-toplevel) || exit 1

cd ./launcher/src/main/resources/fr/dwightstudio/jarmemu/launcher/medias || exit 1

for x in 1 2
do
	width=$(( (x*1920)/3 ))
	height=$(( (x*1200)/3 ))
	if [[ $x -eq 1 ]]
	then
		magick splash@3x.png -resize ${width}x${height} splash.png
	else
		magick splash@3x.png -resize ${width}x${height} splash@${x}x.png
	fi
done
