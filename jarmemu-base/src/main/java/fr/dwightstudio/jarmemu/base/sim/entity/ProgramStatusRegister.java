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

package fr.dwightstudio.jarmemu.base.sim.entity;

public class ProgramStatusRegister extends Register {

    public void setN(boolean value) {
        this.set(31, value);
    }

    public boolean getN() {
        return this.get(31);
    }

    public void setZ(boolean value) {
        this.set(30, value);
    }

    public boolean getZ() {
        return this.get(30);
    }

    public void setC(boolean value) {
        this.set(29, value);
    }

    public boolean getC() {
        return this.get(29);
    }

    public void setV(boolean value) {
        this.set(28, value);
    }

    public boolean getV() {
        return this.get(28);
    }

    public void setI(boolean value) {
        this.set(7, value);
    }

    public boolean getI() {
        return this.get(7);
    }

    public void setF(boolean value) {
        this.set(6, value);
    }

    public boolean getF() {
        return this.get(6);
    }

    public void setT(boolean value) {
        this.set(5, value);
    }

    public boolean getT() {
        return this.get(5);
    }

    @Override
    public String toString() {
        return (getN() ? "N" : " ") +
                (getZ() ? "Z" : " ") +
                (getC() ? "C" : " ") +
                (getV() ? "V" : " ") +
                (getI() ? "I" : " ") +
                (getF() ? "F" : " ") +
                (getT() ? "T" : " ");
    }

    @Override
    public boolean isPSR() {
        return true;
    }
}
