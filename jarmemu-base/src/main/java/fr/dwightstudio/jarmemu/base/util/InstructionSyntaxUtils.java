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
import fr.dwightstudio.jarmemu.base.asm.parser.regex.ASMParser;
import fr.dwightstudio.jarmemu.base.fx.ResizableTableViewSkin;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.factory.StylizedStringTableCell;
import fr.dwightstudio.jarmemu.base.gui.factory.SyntaxHighlightedTableCell;
import fr.dwightstudio.jarmemu.base.gui.view.SyntaxView;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstructionSyntaxUtils {

    public static String IMMEDIATE_REGEX = "=data|#|\\b(rimm[0-9]+|imm[0-9]+)\\b";
    public static String REGISTER_REGEX = "\\b(?i)(reg[0-9abisvn]|PC|LR)(?-i)\\b";
    public static String BRACE_REGEX = "\\{|\\}";
    public static String BRACKET_REGEX = "\\[adr\\]|\\[|\\]";
    public static String INSTRUCTION_REGEX = "\\b" + String.join("|", ASMParser.INSTRUCTIONS) + "\\b";
    public static String LABEL_REGEX = "\\blbl\\b";
    public static String SHIFT_REGEX = "\\b(?i)(sht|lsl|lsr|asr|ror|rrx)(?-i)\\b";
    public static String IGNORED_REGEX = "\\bign\\b";
    public static String MODIFIER_REGEX = "\\B[+\\-!]\\B|\\b(Cd|Um|S|h|b)\\b";

    public static Pattern SYNTAX_PATTERN = Pattern.compile(
            "(?<IMMEDIATE>" + IMMEDIATE_REGEX + ")|"
            + "(?<REGISTER>" + REGISTER_REGEX + ")|"
            + "(?<BRACE>" + BRACE_REGEX + ")|"
            + "(?<BRACKET>" + BRACKET_REGEX + ")|"
            + "(?<INSTRUCTION>" + INSTRUCTION_REGEX + ")|"
            + "(?<LABEL>" + LABEL_REGEX + ")|"
            + "(?<SHIFT>" + SHIFT_REGEX + ")|"
            + "(?<IGNORED>" + IGNORED_REGEX + ")|"
            + "(?<MODIFIER>" + MODIFIER_REGEX + ")"
    );

    public static String getUsage(Instruction instruction) {
        StringBuilder rtn = new StringBuilder();
        rtn.append(instruction.toString().toUpperCase());

        for (Class<? extends Enum<? extends ModifierParameter>> mod : instruction.getModifierParameterClasses()) {
            rtn.append("<");
            switch (mod.getSimpleName()) {
                case "Condition" -> rtn.append("Cd");
                case "UpdateMode" -> rtn.append("Um");
                case "DataMode" -> rtn.append("h/b");
                default -> {
                    boolean first = true;
                    for (Enum<? extends ModifierParameter> param : mod.getEnumConstants()) {
                        if (first) {
                            first = false;
                        } else {
                            rtn.append("/");
                        }
                        rtn.append(param.toString());
                    }
                }
            }
            rtn.append(">");
        }

        rtn.append(" ");

        int preOpt = 0;
        int opt = 0;
        for (int i = 0; i < 4; i++) {
            String type = instruction.getArgumentType(i);

            if (type.equals("NullArgument")) continue;

            if ((i == 0 && instruction.hasWorkingRegister() || type.startsWith("Optional") || type.startsWith("Shift"))) {
                if (i > 0) {
                    rtn.append(" <, ");
                    rtn.append(getArgumentUsage(type, i));
                    opt++;
                } else {
                    rtn.append("<");
                    rtn.append(getArgumentUsage(type, i));
                    rtn.append(",> ");
                    preOpt++;
                }
            } else {
                if (i > 0 && preOpt != i) {
                    rtn.append(", ");
                    rtn.append(getArgumentUsage(type, i));
                } else {
                    rtn.append(getArgumentUsage(type, i));
                }
            }

            if (type.equals("AddressArgument")) break;
        }

        for (int i = 0; i < opt; i++) {
            rtn.append(">");
        }

        return rtn.toString();
    }

    public static String getArgumentUsage(String type, Integer regNum) {
        return switch (type) {
            case "AddressArgument" -> "[adr]";
            case "IgnoredArgument" -> "ign";
            case "ImmediateArgument" -> "#imm12";
            case "SmallImmediateArgument" -> "#imm8";
            case "LongImmediateArgument" -> "#imm16";
            case "OptionalImmediateOrRegisterArgument", "ImmediateOrRegisterArgument" -> "#imm12/regv";
            case "OptionalLongImmediateOrRegisterArgument", "LongImmediateOrRegisterArgument" -> "#imm16/regv";
            case "LabelArgument" -> "lbl/imm24";
            case "LabelOrRegisterArgument" -> "lbl/imm24/regb";
            case "PostOffsetArgument" -> "#imm9/<+/->regi";
            case "RegisterAddressArgument" -> "[rega]";
            case "RegisterArgument" -> "reg" + regNum;
            case "RegisterArrayArgument" -> "{regn}";
            case "RegisterWithUpdateArgument" -> "reg" + regNum + "<!>";
            case "RotatedImmediateArgument" -> "#rimm8";
            case "OptionalRotatedImmediateOrRegisterArgument", "RotatedImmediateOrRegisterArgument" -> "#rimm8/regv";
            case "ShiftArgument" -> "sht #imm5/regs";
            default -> "";
        };
    }

    public static String textToString(Collection<Text> texts) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Text text : texts) {
            stringBuilder.append(text.getText());
        }

        return stringBuilder.toString();
    }

    @SuppressWarnings("unchecked")
    public static TableView<Condition> getConditionTable() {
        TableColumn<Condition, String> col0 = new TableColumn<>();
        col0.setGraphic(new FontIcon(Material2OutlinedAL.LABEL));
        setupColumn(col0, true, false);
        col0.setMaxWidth(50);
        col0.setMinWidth(50);
        col0.setCellFactory(StylizedStringTableCell.factory("text", "usage", "modifier"));
        col0.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().toString()));

        TableColumn<Condition, String> col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%instructionList.table.flag"));
        col1.setGraphic(new FontIcon(Material2OutlinedAL.FLAG));
        setupColumn(col1, false, false);
        col1.setMaxWidth(150);
        col1.setMinWidth(150);
        col1.setCellFactory(StylizedStringTableCell.factory("usage"));
        col1.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col1.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDescription()));

        TableColumn<Condition, String> col2 = new TableColumn<>(JArmEmuApplication.formatMessage("%instructionList.table.description"));
        col2.setGraphic(new FontIcon(Material2OutlinedAL.DESCRIPTION));
        setupColumn(col2, false, true);
        col2.setCellFactory(SyntaxHighlightedTableCell.factory(Pos.CENTER_LEFT));
        col2.setCellValueFactory(c -> new ReadOnlyStringWrapper(JArmEmuApplication.formatMessage("%instructionList.description." + c.getValue().toString().toLowerCase())));

        TableColumn<Condition, Condition> masterCol = getMasterColumn("%instructionList.detail.condition");
        masterCol.getColumns().addAll(col0, col1, col2);
        masterCol.setGraphic(getText("Cd", "modifier"));

        TableView<Condition> tableView = new TableView<>();
        tableView.getColumns().add(masterCol);
        tableView.getItems().setAll(Condition.values());
        setupTable(tableView);

        return tableView;
    }

    @SuppressWarnings("unchecked")
    public static TableView<SyntaxView> getValueTable(String usage) {
        TableColumn<SyntaxView, String> col0 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.symbols.title"));
        col0.setGraphic(new FontIcon(Material2OutlinedAL.LABEL));
        setupColumn(col0, true, false);
        col0.setMaxWidth(150);
        col0.setMinWidth(150);
        col0.setCellFactory(SyntaxHighlightedTableCell.factory(Pos.CENTER));
        col0.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().symbol()));

        TableColumn<SyntaxView, String> col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%instructionList.table.description"));
        col1.setGraphic(new FontIcon(Material2OutlinedAL.DESCRIPTION));
        setupColumn(col1, false, true);
        col1.setCellFactory(SyntaxHighlightedTableCell.factory(Pos.CENTER_LEFT));
        col1.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().description()));

        TableColumn<SyntaxView, SyntaxView> masterCol = getMasterColumn("%instructionList.detail.value");
        masterCol.getColumns().addAll(col0, col1);

        TableView<SyntaxView> tableView = new TableView<>();
        tableView.getColumns().setAll(masterCol);
        setupTable(tableView);

        ArrayList<SyntaxView> items = new ArrayList<>();

        items.add(new SyntaxView("imm5", JArmEmuApplication.formatMessage("%instructionList.description.imm", 5)));
        items.add(new SyntaxView("imm8", JArmEmuApplication.formatMessage("%instructionList.description.imm", 8)));
        items.add(new SyntaxView("rimm8", JArmEmuApplication.formatMessage("%instructionList.description.rimm", 8)));
        items.add(new SyntaxView("imm9", JArmEmuApplication.formatMessage("%instructionList.description.imm", 9)));
        items.add(new SyntaxView("imm12", JArmEmuApplication.formatMessage("%instructionList.description.imm", 12)));
        items.add(new SyntaxView("imm16", JArmEmuApplication.formatMessage("%instructionList.description.imm", 16)));
        items.add(new SyntaxView("imm24", JArmEmuApplication.formatMessage("%instructionList.description.imm", 24)));

        items.add(new SyntaxView("reg0", JArmEmuApplication.formatMessage("%instructionList.description.rego")));
        items.add(new SyntaxView("reg1", JArmEmuApplication.formatMessage("%instructionList.description.reg")));
        items.add(new SyntaxView("reg2", JArmEmuApplication.formatMessage("%instructionList.description.reg")));
        items.add(new SyntaxView("reg3", JArmEmuApplication.formatMessage("%instructionList.description.reg")));
        items.add(new SyntaxView("[rega]", JArmEmuApplication.formatMessage("%instructionList.description.reg")));
        items.add(new SyntaxView("regb", JArmEmuApplication.formatMessage("%instructionList.description.reg")));
        items.add(new SyntaxView("regi", JArmEmuApplication.formatMessage("%instructionList.description.reg")));
        items.add(new SyntaxView("regs", JArmEmuApplication.formatMessage("%instructionList.description.regs")));
        items.add(new SyntaxView("regv", JArmEmuApplication.formatMessage("%instructionList.description.regv")));
        items.add(new SyntaxView("{regn}", JArmEmuApplication.formatMessage("%instructionList.description.regn")));

        items.add(new SyntaxView("lbl", JArmEmuApplication.formatMessage("%instructionList.description.lbl")));

        final boolean adr = usage.contains("[adr]");
        boolean empty = true;

        for (SyntaxView sv : items) {
            if (usage.contains(sv.symbol())) {
                tableView.getItems().add(sv);
                empty = false;
            } else if (adr) {
                boolean add = switch (sv.symbol()) {
                    case "imm5", "imm12", "rega", "regi", "regs" -> true;
                    default -> false;
                };

                if (add) {
                    tableView.getItems().add(sv);
                    empty = false;
                }
            }
        }

        return empty ? null : tableView;
    }

    @SuppressWarnings("unchecked")
    public static TableView<SyntaxView> getShiftTable() {
        TableColumn<SyntaxView, String> col0 = new TableColumn<>();
        col0.setGraphic(new FontIcon(Material2OutlinedAL.LABEL));
        setupColumn(col0, true, false);
        col0.setMaxWidth(150);
        col0.setMinWidth(150);
        col0.setCellFactory(SyntaxHighlightedTableCell.factory(Pos.CENTER_LEFT));
        col0.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().symbol()));

        TableColumn<SyntaxView, String> col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%instructionList.table.description"));
        col1.setGraphic(new FontIcon(Material2OutlinedAL.DESCRIPTION));
        setupColumn(col1, false, true);
        col1.maxWidthProperty().bind(JArmEmuApplication.getStage().widthProperty().multiply(0.5));
        col1.setCellFactory(SyntaxHighlightedTableCell.factory(Pos.CENTER_LEFT));
        col1.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().description()));

        TableColumn<SyntaxView, SyntaxView> masterCol = getMasterColumn("%instructionList.detail.shift");
        masterCol.getColumns().addAll(col0, col1);
        masterCol.setGraphic(getText("sht", "shift"));

        TableView<SyntaxView> tableView = new TableView<>();
        tableView.getColumns().setAll(masterCol);
        tableView.setPrefHeight(310);
        setupTable(tableView);

        ObservableList<SyntaxView> items = tableView.getItems();

        items.add(new SyntaxView("LSL #imm5/regs", JArmEmuApplication.formatMessage("%instructionList.description.lslShift")));
        items.add(new SyntaxView("LSR #imm5/regs", JArmEmuApplication.formatMessage("%instructionList.description.lsrShift")));
        items.add(new SyntaxView("ASR #imm5/regs", JArmEmuApplication.formatMessage("%instructionList.description.asrShift")));
        items.add(new SyntaxView("ROR #imm5/regs", JArmEmuApplication.formatMessage("%instructionList.description.rorShift")));
        items.add(new SyntaxView("RRX", JArmEmuApplication.formatMessage("%instructionList.description.rrxShift")));

        return tableView;
    }

    @SuppressWarnings("unchecked")
    public static TableView<SyntaxView> getAddressTable(Instruction instruction) {
        TableColumn<SyntaxView, String> col0 = new TableColumn<>();
        col0.setGraphic(new FontIcon(Material2OutlinedAL.LABEL));
        setupColumn(col0, true, false);
        col0.setMaxWidth(250);
        col0.setMinWidth(250);
        col0.setCellFactory(SyntaxHighlightedTableCell.factory(Pos.CENTER_LEFT));
        col0.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().symbol()));

        TableColumn<SyntaxView, String> col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%instructionList.table.description"));
        col1.setGraphic(new FontIcon(Material2OutlinedAL.DESCRIPTION));
        setupColumn(col1, false, true);
        col1.maxWidthProperty().bind(JArmEmuApplication.getStage().widthProperty().multiply(0.4));
        col1.setCellFactory(SyntaxHighlightedTableCell.factory(Pos.CENTER_LEFT));
        col1.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().description()));

        TableColumn<SyntaxView, SyntaxView> masterCol = getMasterColumn("%instructionList.detail.address");
        masterCol.getColumns().addAll(col0, col1);
        masterCol.setGraphic(getText("[adr]", "bracket"));

        TableView<SyntaxView> tableView = new TableView<>();
        tableView.getColumns().setAll(masterCol);
        tableView.setPrefHeight(310);
        setupTable(tableView);

        ObservableList<SyntaxView> items = tableView.getItems();

        items.add(new SyntaxView("[rega]", JArmEmuApplication.formatMessage("%instructionList.description.adrReg")));
        items.add(new SyntaxView("[rega, #imm12]", JArmEmuApplication.formatMessage("%instructionList.description.adrRegImm")));
        items.add(new SyntaxView("[rega, <+/->regi]", JArmEmuApplication.formatMessage("%instructionList.description.adrRegReg")));
        items.add(new SyntaxView("[rega, <+/->regi, sht #imm5/regs]", JArmEmuApplication.formatMessage("%instructionList.description.adrRegRegSht")));
        items.add(new SyntaxView("[rega, #imm12]!", JArmEmuApplication.formatMessage("%instructionList.description.adrRegImmU")));
        items.add(new SyntaxView("[rega, <+/->regi]!", JArmEmuApplication.formatMessage("%instructionList.description.adrRegRegU")));
        items.add(new SyntaxView("[rega, <+/->regi, sht #imm5/regs]!", JArmEmuApplication.formatMessage("%instructionList.description.adrRegRegShtU")));
        items.add(new SyntaxView("[rega], #imm12", JArmEmuApplication.formatMessage("%instructionList.description.adrRegImmP")));
        items.add(new SyntaxView("[rega], <+/->regi", JArmEmuApplication.formatMessage("%instructionList.description.adrRegRegP")));
        items.add(new SyntaxView("[rega], <+/->regi, sht #imm5/regs", JArmEmuApplication.formatMessage("%instructionList.description.adrRegRegShtP")));
        if (instruction == Instruction.LDR) items.add(new SyntaxView("=data", JArmEmuApplication.formatMessage("%instructionList.description.adrPseudo")));

        return tableView;
    }

    private static void setupColumn(TableColumn<?, ?> column, boolean sortable, boolean resizable) {
        column.setSortable(sortable);
        column.setResizable(resizable);
        if (resizable) column.setMinWidth(Region.USE_PREF_SIZE);
        column.setEditable(false);
        column.setReorderable(false);
    }

    private static void setupTable(TableView<?> tableView) {
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().addAll(Styles.STRIPED, Tweaks.ALIGN_CENTER);
        tableView.setEditable(false);

        tableView.setSkin(new ResizableTableViewSkin<>(tableView));

        tableView.getStylesheets().add(JArmEmuApplication.getResource("editor-style.css").toExternalForm());
    }

    private static <T, S> TableColumn<T, S> getMasterColumn(String title) {
        TableColumn<T, S> masterCol = new TableColumn<>(JArmEmuApplication.formatMessage(title));
        setupColumn(masterCol, false, true);
        masterCol.setMinWidth(Region.USE_COMPUTED_SIZE);
        return masterCol;
    }

    public static List<Text> getFormatted(String text) {
        ArrayList<Text> rtn = new ArrayList<>();
        Matcher matcher = SYNTAX_PATTERN.matcher(text);

        int lastKwEnd = 0;
        while (matcher.find()) {
            String styleClass =matcher.group("IMMEDIATE") != null ? "immediate"
                    : matcher.group("REGISTER") != null ? "register"
                    : matcher.group("BRACE") != null ? "brace"
                    : matcher.group("BRACKET") != null ? "bracket"
                    : matcher.group("INSTRUCTION") != null ? "instruction"
                    : matcher.group("LABEL") != null ? "label-ref"
                    : matcher.group("SHIFT") != null ? "shift"
                    : matcher.group("IGNORED") != null ? "invalid-instruction"
                    : matcher.group("MODIFIER") != null ? "modifier"
                    : null;

            if (styleClass != null) {
                if (lastKwEnd != matcher.start()) {
                    rtn.add(new Text(text.substring(lastKwEnd, matcher.start())));
                }

                rtn.add(getText(matcher.group(), styleClass));
                lastKwEnd = matcher.end();
            }
        }

        if (lastKwEnd != text.length()) {
            rtn.add(new Text(text.substring(lastKwEnd)));
        }

        return rtn;
    }

    public static Text getText(String text, String clazz) {
        Text t = new Text(text);
        t.getStyleClass().addAll("text", clazz);
        return t;
    }
}
