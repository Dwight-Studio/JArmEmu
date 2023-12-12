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

if [ "${EUID}" -ne 0 ]; then
    sudo ./scripts/build_all.sh
    exit 0
fi

echo "Building RPM and DEB..."
source ./scripts/build_rpm_deb.sh || exit 1

echo "Building WIN..."
source ./scripts/build_win.sh || exit 1

echo "Building FLATPAK and ARCH..."
source ./scripts/build_flatpak_arch.sh || exit 1

echo "Building ZIP..."
source ./scripts/build_zip.sh || exit 1

