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

package fr.dwightstudio.jarmemu.base.util;

import fr.dwightstudio.jarmemu.base.asm.Instruction;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class InstructionSyntaxUtils {
    public static List<Text> getUsage(Instruction instruction) {
        ArrayList<Text> rtn = new ArrayList<>();

        rtn.add(getText(instruction.toString().toUpperCase(), "instruction"));

        for (Class<? extends Enum<? extends ModifierParameter>> mod : instruction.getModifierParameterClasses()) {
            rtn.add(new Text("<"));
            switch (mod.getSimpleName()) {
                case "Condition" -> rtn.add(getText("Cd", "modifier"));
                case "UpdateMode" -> rtn.add(getText("Um", "modifier"));
                default -> {
                    boolean first = true;
                    for (Enum<? extends ModifierParameter> param : mod.getEnumConstants()) {
                        if (first) {
                            first = false;
                        } else {
                            rtn.add(new Text("/"));
                        }
                        rtn.add(getText(param.toString(), "modifier"));
                    }
                }
            }
            rtn.add(new Text(">"));
        }

        rtn.add(new Text(" "));

        int opt = 0;
        for (int i = 0; i < 4; i++) {
            String type = instruction.getArgumentType(i);

            if (type.equals("NullArgument")) continue;

            if ((i == 0 && instruction.hasWorkingRegister() || type.startsWith("Optional") || type.startsWith("PostOffset") || type.startsWith("Shift"))) {
                if (i > 0) {
                    rtn.add(new Text(" <, "));
                    rtn.addAll(getArgumentUsage(type, i));
                    opt++;
                } else {
                    rtn.add(new Text("<"));
                    rtn.addAll(getArgumentUsage(type, i));
                    rtn.add(new Text(">"));
                }
            } else {
                if (i > 0) {
                    rtn.add(new Text(", "));
                    rtn.addAll(getArgumentUsage(type, i));
                } else {
                    rtn.addAll(getArgumentUsage(type, i));
                }
            }
        }

        for (int i = 0; i < opt; i++) {
            rtn.add(new Text(">"));
        }

        return rtn;
    }

    public static List<Text> getArgumentUsage(String type, Integer regNum) {
        return switch (type) {
            case "AddressArgument" -> List.of(getText("[adr]", "bracket"), new Text("<"), getText("!", "bracket"), new Text(">")); // List.of(getText("[", "bracket"), getText("reg", "register"), new Text("<,"), getText("#imm12", "immediate"), new Text("/<"), getText("+", "immediate"), new Text("/"), getText("-", "immediate"), new Text(">"), getText("reg", "register"), new Text("<,"), getText("shift", "shift"), new Text(">"), getText("]", "bracket"), new Text("<"), getText("!", "bracket"), new Text(">/"), getText("=var", "pseudo-instruction"));

            case "IgnoredArgument" -> List.of(getText("ign", "invalid-instruction"));

            case "ImmediateArgument" -> List.of(getText("#imm", "immediate"));

            case "OptionalImmediateOrRegisterArgument", "ImmediateOrRegisterArgument" -> List.of(getText("#imm", "immediate"), new Text("/"), getText("regv", "register"));

            case "LabelArgument" -> List.of(getText("lbl", "label-ref"), new Text("/"), getText("imm24", "immediate"));

            case "LabelOrRegisterArgument" -> List.of(getText("lbl", "label-ref"), new Text("/"), getText("imm24", "immediate"), new Text("/"), getText("regb", "register"));

            case "PostOffsetArgument" -> List.of(getText("#imm", "immediate"), new Text("/<"), getText("+", "immediate"), new Text("/"), getText("-", "immediate"), new Text(">"), getText("regi", "register"));

            case "RegisterAddressArgument" -> List.of(getText("rega", "register"));

            case "RegisterArgument" -> List.of(getText("reg" + regNum, "register"));

            case "RegisterArrayArgument" -> List.of(getText("{", "brace"), getText("regn", "register"), getText("}", "brace"));

            case "RegisterWithUpdateArgument" -> List.of(getText("reg" + regNum, "register"), new Text("<"), getText("!", "register"), new Text(">"));

            case "RotatedImmediateArgument" -> List.of(getText("#imm8", "immediate"));

            case "OptionalRotatedImmediateOrRegisterArgument", "RotatedImmediateOrRegisterArgument" -> List.of(getText("#imm8", "immediate"), new Text("/"), getText("regv", "register"));

            case "ShiftArgument" -> List.of(getText("sht ", "shift"), getText("#imm5", "immediate"), new Text("/"), getText("regs", "register"));

            default -> List.of(new Text());
        };
    }

    public static Text getText(String text, String clazz) {
        Text t = new Text(text);
        t.getStyleClass().addAll("text", clazz);
        return t;
    }
}