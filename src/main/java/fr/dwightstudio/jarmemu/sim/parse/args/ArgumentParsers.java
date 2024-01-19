/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.sim.parse.args;

public class ArgumentParsers {

    public static final NullParser NULL = new NullParser();
    public static final RegisterParser REGISTER = new RegisterParser();
    public static final RegisterArrayParser REGISTER_ARRAY = new RegisterArrayParser();
    public static final RegisterAddressParser REGISTER_ADDRESS = new RegisterAddressParser();
    public static final RegisterWithUpdateParser REGISTER_WITH_UPDATE = new RegisterWithUpdateParser();
    public static final AddressParser ADDRESS = new AddressParser();

    public static final ImmParser IMM = new ImmParser();
    public static final RotatedImmParser ROTATED_IMM = new RotatedImmParser();
    public static final CodeParser CODE = new CodeParser();

    public static final LabelParser LABEL = new LabelParser();
    public static final ImmOrRegisterParser IMM_OR_REGISTER = new ImmOrRegisterParser();
    public static final RotatedImmOrRegisterParser ROTATED_IMM_OR_REGISTER = new RotatedImmOrRegisterParser();
    public static final ShiftParser SHIFT = new ShiftParser();

}
