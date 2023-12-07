/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
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

package fr.dwightstudio.jarmemu.sim.obj;

import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class StateContainer {

    public static final int DEFAULT_STACK_ADDRESS = 65536;
    public static final int DEFAULT_SYMBOLS_ADDRESS = 0;
    public static final int MAX_NESTING_COUNT = 1024;
    private static final Pattern SPECIAL_VALUE_PATTERN = Pattern.compile(
            "(?i)"
                    + "(?<VALUE>"
                    + "\\b0B\\w+"
                    + "|\\b0X\\w+"
                    + "|\\b00\\w+"
                    + "|'.'"
                    + ")" +
                    "(?-i)"
    );

    // ASM
    private final HashMap<String, Integer> consts; // HashMap des constantes
    private final HashMap<String, Integer> data; // HashMap des données ajoutées dans la mémoire par directive
    private final HashMap<String, Integer> pseudoData; // HashMap des données ajoutées dans la mémoire par pseudo-op
    private final HashMap<String, FileLine> labels; // HashMap des labels
    private final ArrayList<String> global; // Symbols globaux
    private int nestingCount;
    private int lastAddressROData;

    // Registers
    public static final int REGISTER_NUMBER = 16;
    private final Register[] registers;
    private final PSR cpsr;
    private final PSR spsr;

    // Memory
    private final MemoryAccessor memory;
    private final int stackAddress;
    private final int symbolsAddress;

    public StateContainer(int stackAddress, int symbolsAddress) {
        this.stackAddress = stackAddress;
        this.symbolsAddress = symbolsAddress;

        // ASM
        labels = new HashMap<>();
        consts = new HashMap<>();
        data = new HashMap<>();
        pseudoData = new HashMap<>();
        global = new ArrayList<>();
        nestingCount = 0;
        lastAddressROData = 0;

        // Initializing registers
        cpsr = new PSR();
        spsr = new PSR();

        this.registers = new Register[REGISTER_NUMBER];
        clearRegisters();

        // Initializing memory
        this.memory = new MemoryAccessor();
    }

    public StateContainer() {
        this(DEFAULT_STACK_ADDRESS,DEFAULT_SYMBOLS_ADDRESS);
    }

    public StateContainer(StateContainer stateContainer) {
        this(stateContainer.getStackAddress(), stateContainer.getSymbolsAddress());
        this.getConsts().putAll(stateContainer.consts);
        this.getData().putAll(stateContainer.data);
        this.getLabels().putAll(stateContainer.labels);
        this.getPseudoData().putAll(stateContainer.pseudoData);

        for (int i = 0; i < REGISTER_NUMBER; i++) {
            registers[i].setData(stateContainer.getRegister(i).getData());
        }

        cpsr.setData(stateContainer.getCPSR().getData());
        spsr.setData(stateContainer.spsr.getData());
    }

    public void clearRegisters() {
        for (int i = 0; i < REGISTER_NUMBER; i++) {
            if (registers[i] != null) {
                registers[i].setData(0);
            } else {
                registers[i] = new Register();
            }

            if (i == RegisterUtils.SP.getN()) {
                registers[i].setData(stackAddress);
            }
        }

        cpsr.setData(0);
        spsr.setData(0);
    }

    public int evalWithConsts(String expString) {
        try {
            Expression exp = new ExpressionBuilder(preEval(expString)).variables(consts.keySet()).build();

            for (String str : consts.keySet()) {
                exp.setVariable(str, (double) consts.get(str));
            }

            return (int) exp.evaluate();
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException("Malformed math expression '" + expString + "'");
        }
    }

    public int evalWithAll(String expString) {
        try {
            Expression exp = new ExpressionBuilder(preEval(expString)).variables(consts.keySet()).variables(data.keySet()).build();

            for (String str : consts.keySet()) {
                exp.setVariable(str, (double) consts.get(str));
            }
            for (String str : data.keySet()) {
                exp.setVariable(str, (double) data.get(str));
            }

            return (int) Math.floor(exp.evaluate());
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException("Malformed math expression '" + expString + "' (" + exception.getMessage() + ")");
        }
    }

    private String preEval(String s) {

        if (s.startsWith("=") || s.startsWith("#")) s = s.substring(1);

        return SPECIAL_VALUE_PATTERN.matcher(s).replaceAll(matchResult -> {
            String valueString = matchResult.group("VALUE").toUpperCase();
            try {
                if (valueString.startsWith("0B")) {
                    valueString = valueString.substring(2).strip();

                    return String.valueOf(Integer.parseUnsignedInt(valueString, 2));
                } else if (valueString.startsWith("0X")) {
                    valueString = valueString.substring(2).strip();

                    return String.valueOf(Integer.parseUnsignedInt(valueString, 16));
                } else if (valueString.startsWith("00")) {
                    valueString = valueString.substring(2).strip();

                    return String.valueOf(Integer.parseUnsignedInt(valueString, 8));
                } else if (valueString.startsWith("'")) {
                    return String.valueOf((int) matchResult.group("VALUE").charAt(1));
                }
            } catch (NumberFormatException exception) {
                throw new SyntaxASMException("Malformed math expression '" + valueString + "' (" + exception.getMessage() + ")");
            }
            return valueString;
        }).toUpperCase();

    }

    public int getStackAddress() {
        return stackAddress;
    }

    public int getSymbolsAddress() {
        return symbolsAddress;
    }

    public List<String> getGlobals() {
        return global;
    }

    public void setGlobal(String global) {
        this.global.add(global);
    }

    public void clearGlobals() {
        this.global.clear();
    }

    public Register[] getAllRegisters() {
        return new Register[] {
                registers[0],
                registers[1],
                registers[2],
                registers[3],
                registers[4],
                registers[5],
                registers[6],
                registers[7],
                registers[8],
                registers[9],
                registers[10],
                registers[11],
                registers[12],
                registers[13],
                registers[14],
                registers[15],
                cpsr,
                spsr
        };
    }

    /**
     * @return le nombre de branches actives
     */
    public int getNestingCount() {
        return nestingCount;
    }

    /**
     * Met à jour le compteur de branche en ajoutant 1
     */
    public void branch() {
        this.nestingCount++;
    }

    /**
     * Met à jour le compteur de branche en retirant 1
     */
    public void merge() {
        this.nestingCount--;
        if (this.nestingCount < 0) this.nestingCount = 0;
    }

    public int getLastAddressROData() {
        return lastAddressROData;
    }

    public void setLastAddressROData(int lastAddressROData) {
        this.lastAddressROData = lastAddressROData;
    }

    public HashMap<String, Integer> getConsts() {
        return consts;
    }

    public HashMap<String, Integer> getData() {
        return data;
    }

    public HashMap<String, Integer> getPseudoData() {
        return pseudoData;
    }

    public HashMap<String, FileLine> getLabels() {
        return labels;
    }

    public ArrayList<String> getGlobal() {
        return global;
    }

    public Register getRegister(int i) {
        return registers[i];
    }

    public Register getFP() {
        return registers[RegisterUtils.FP.getN()];
    }

    public Register getIP() {
        return registers[RegisterUtils.IP.getN()];
    }

    public Register getSP() {
        return registers[RegisterUtils.SP.getN()];
    }

    public Register getPC() {
        return registers[RegisterUtils.PC.getN()];
    }

    public Register getLR() {
        return registers[RegisterUtils.LR.getN()];
    }

    public PSR getCPSR() {
        return cpsr;
    }

    public PSR getSPSR() {
        return spsr;
    }

    public MemoryAccessor getMemory() {
        return memory;
    }

    public Register[] getRegisters() {
        return registers;
    }
}
