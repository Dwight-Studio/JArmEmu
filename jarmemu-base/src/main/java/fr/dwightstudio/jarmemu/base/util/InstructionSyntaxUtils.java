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

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import fr.dwightstudio.jarmemu.base.asm.Instruction;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.factory.StylizedStringTableCell;
import fr.dwightstudio.jarmemu.base.gui.factory.SyntaxHighlightedTableCell;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.util.*;

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

        int preOpt = 0;
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
                    rtn.add(new Text(",> "));
                    preOpt++;
                }
            } else {
                if (i > 0 && preOpt != i) {
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

            case "ImmediateArgument" -> List.of(getText("#imm12", "immediate"));

            case "SmallImmediateArgument" -> List.of(getText("#imm8", "immediate"));

            case "LongImmediateArgument" -> List.of(getText("#imm16", "immediate"));

            case "OptionalImmediateOrRegisterArgument", "ImmediateOrRegisterArgument" -> List.of(getText("#imm12", "immediate"), new Text("/"), getText("regv", "register"));

            case "OptionalLongImmediateOrRegisterArgument", "LongImmediateOrRegisterArgument" -> List.of(getText("#imm16", "immediate"), new Text("/"), getText("regv", "register"));

            case "LabelArgument" -> List.of(getText("lbl", "label-ref"), new Text("/"), getText("imm24", "immediate"));

            case "LabelOrRegisterArgument" -> List.of(getText("lbl", "label-ref"), new Text("/"), getText("imm24", "immediate"), new Text("/"), getText("regb", "register"));

            case "PostOffsetArgument" -> List.of(getText("#imm9", "immediate"), new Text("/<"), getText("+", "immediate"), new Text("/"), getText("-", "immediate"), new Text(">"), getText("regi", "register"));

            case "RegisterAddressArgument" -> List.of(getText("[rega]", "register"));

            case "RegisterArgument" -> List.of(getText("reg" + regNum, "register"));

            case "RegisterArrayArgument" -> List.of(getText("{", "brace"), getText("regn", "register"), getText("}", "brace"));

            case "RegisterWithUpdateArgument" -> List.of(getText("reg" + regNum, "register"), new Text("<"), getText("!", "register"), new Text(">"));

            case "RotatedImmediateArgument" -> List.of(getText("#rimm8", "immediate"));

            case "OptionalRotatedImmediateOrRegisterArgument", "RotatedImmediateOrRegisterArgument" -> List.of(getText("#rimm8", "immediate"), new Text("/"), getText("regv", "register"));

            case "ShiftArgument" -> List.of(getText("sht ", "shift"), getText("#imm5", "immediate"), new Text("/"), getText("regs", "register"));

            default -> List.of(new Text());
        };
    }

    @SuppressWarnings("unchecked")
    public static TableView<Condition> getConditionTable() {
        TableColumn<Condition, String> col0 = new TableColumn<>("Cd");
        setup(col0, true, false);
        col0.setMaxWidth(50);
        col0.setMinWidth(50);
        col0.setCellFactory(StylizedStringTableCell.factory("text", "usage", "modifier"));
        col0.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().toString()));

        TableColumn<Condition, String> col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%instructionList.table.flag"));
        col1.setGraphic(new FontIcon(Material2OutlinedAL.FLAG));
        setup(col1, false, false);
        col1.setMaxWidth(150);
        col1.setMinWidth(150);
        col1.setCellFactory(StylizedStringTableCell.factory("usage"));
        col1.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col1.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDescription()));

        TableColumn<Condition, String> col2 = new TableColumn<>(JArmEmuApplication.formatMessage("%instructionList.table.description"));
        col2.setGraphic(new FontIcon(Material2OutlinedAL.DESCRIPTION));
        setup(col2, false, true);
        col2.setCellFactory(SyntaxHighlightedTableCell.factory());
        col2.setCellValueFactory(c -> new ReadOnlyStringWrapper(JArmEmuApplication.formatMessage("%instructionList.description." + c.getValue().toString().toLowerCase())));

        TableColumn<Condition, Condition> masterCol = getMasterColumn("%instructionList.detail.condition");
        masterCol.getColumns().addAll(col0, col1, col2);

        TableView<Condition> tableView = new TableView<>();
        tableView.getColumns().add(masterCol);
        tableView.getItems().setAll(Condition.values());
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().addAll(Styles.STRIPED, Tweaks.ALIGN_CENTER);
        tableView.setEditable(false);
        tableView.setMaxHeight(Double.POSITIVE_INFINITY);
        tableView.setMinHeight(200);

        tableView.setSkin(new TableViewUtils.ResizableTableViewSkin<>(tableView));

        tableView.getStylesheets().add(JArmEmuApplication.getResource("editor-style.css").toExternalForm());

        return tableView;
    }

    @SuppressWarnings("unchecked")
    public static TableView<Condition> getValueTable() {
        TableColumn<Condition, String> col0 = new TableColumn<>();
        col0.setGraphic(new FontIcon(Material2OutlinedAL.LABEL));
        setup(col0, true, false);
        col0.setMaxWidth(50);
        col0.setMinWidth(50);
        col0.setCellFactory(SyntaxHighlightedTableCell.factory());
        col0.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().toString()));

        TableColumn<Condition, String> col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%instructionList.table.description"));
        col1.setGraphic(new FontIcon(Material2OutlinedAL.DESCRIPTION));
        setup(col1, false, true);
        col1.setCellFactory(SyntaxHighlightedTableCell.factory());
        col1.setCellValueFactory(c -> new ReadOnlyStringWrapper(JArmEmuApplication.formatMessage("%instructionList.description." + c.getValue().toString().toLowerCase())));

        TableColumn<Condition, Condition> masterCol = getMasterColumn("%instructionList.detail.value");
        masterCol.getColumns().addAll(col0, col1);

        TableView<Condition> tableView = new TableView<>();
        tableView.getColumns().setAll(masterCol);
        tableView.getItems().setAll(Condition.values());
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().addAll(Styles.STRIPED, Tweaks.ALIGN_CENTER);
        tableView.setEditable(false);
        tableView.setMaxHeight(Double.POSITIVE_INFINITY);
        tableView.setMinHeight(200);

        tableView.setSkin(new TableViewUtils.ResizableTableViewSkin<>(tableView));

        tableView.getStylesheets().add(JArmEmuApplication.getResource("editor-style.css").toExternalForm());

        return tableView;
    }

    private static void setup(TableColumn<?, ?> column, boolean sortable, boolean resizable) {
        column.setSortable(sortable);
        column.setResizable(resizable);
        if (resizable) column.setMinWidth(Region.USE_PREF_SIZE);
        column.setEditable(false);
        column.setReorderable(false);
    }

    private static <T, S> TableColumn<T, S> getMasterColumn(String title) {
        TableColumn<T, S> masterCol = new TableColumn<>(JArmEmuApplication.formatMessage(title));
        setup(masterCol, false, true);
        masterCol.setMinWidth(Region.USE_COMPUTED_SIZE);
        return masterCol;
    }

    public static List<Text> replacePlaceholder(String text) {
        HashMap<String, String> map = new HashMap<>();

        map.put("imm8", "immediate");
        map.put("rimm8", "immediate");
        map.put("imm9", "immediate");
        map.put("imm12", "immediate");
        map.put("imm16", "immediate");
        map.put("imm24", "immediate");

        map.put("reg0", "register");
        map.put("reg1", "register");
        map.put("reg2", "register");
        map.put("reg3", "register");
        map.put("regb", "register");
        map.put("regi\\b", "register");
        map.put("rega\\b", "register");
        map.put("regs", "register");
        map.put("regv", "register");

        map.put("PC", "register");
        map.put("LR", "register");

        map.put("NOP", "instruction");

        map.put("lbl", "label-ref");

        return replacePlaceholderRecursive(text, map);
    }

    private static List<Text> replacePlaceholderRecursive(String text, Map<String, String> dict) {
        if (dict.isEmpty()) return List.of(new Text(text));

        ArrayList<Text> rtn = new ArrayList<>();

        Map.Entry<String, String> entry = dict.entrySet().iterator().next();
        dict.remove(entry.getKey());

        boolean start = true;
        String replacement = entry.getKey().replace("\\b", "");
        for (String textPart : text.split(entry.getKey())) {
            if (start) {
                start = false;
            } else {
                Text r = getText(replacement, entry.getValue());
                r.getStyleClass().add("usage");
                rtn.add(r);
            }
            rtn.addAll(replacePlaceholderRecursive(textPart, new HashMap<>(dict)));
        }

        return rtn;
    }

    public static Text getText(String text, String clazz) {
        Text t = new Text(text);
        t.getStyleClass().addAll("text", clazz);
        return t;
    }
}
