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

import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.util.RegisterUtils;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class StateContainer {

    public static final int DEFAULT_STACK_ADDRESS = 65536;
    public static final int DEFAULT_PROGRAM_ADDRESS = 0;
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
    private final ArrayList<HashMap<String, Integer>> consts; // File index -> Name -> Constants
    private final ArrayList<HashMap<String, Integer>> data; // File index -> Name -> Memory data added by directives
    private final ArrayList<HashMap<String, Integer>> labels; // File index -> Label -> Memory pos offset
    private final HashMap<String, Integer> globals; // Global symbols -> File index
    private final HashMap<Integer, Integer> nestingMap; // Return pos -> counter
    private int nestingCount;
    private int symbolAddress;
    private int writableDataAddress;
    private int pseudoInstructionAddress;
    private FilePos memoryPos; // Position dans la mémoire et fichier en cours de lecture

    // Registers
    public static final int REGISTER_NUMBER = 16;
    private final Register[] registers;
    private final ProgramStatusRegister cpsr;
    private final ProgramStatusRegister spsr;

    // Memory
    private final MemoryAccessor memory;
    private final int stackAddress;
    private final int programAddress;

    public StateContainer(int stackAddress, int programAddress) {
        this.stackAddress = stackAddress;
        this.programAddress = programAddress;

        // ASM
        labels = new ArrayList<>();
        consts = new ArrayList<>();
        data = new ArrayList<>();
        globals = new HashMap<>();
        nestingMap = new HashMap<>();

        nestingCount = 0;
        symbolAddress = 0;
        writableDataAddress = 0;
        pseudoInstructionAddress = 0;
        memoryPos = new FilePos(0, programAddress);

        // Initializing registers
        cpsr = new ProgramStatusRegister();
        spsr = new ProgramStatusRegister();

        this.registers = new Register[REGISTER_NUMBER];
        clearRegisters();
        clearAndInitFiles(1);

        // Initializing memory
        this.memory = new MemoryAccessor(this);
    }

    public StateContainer() {
        this(DEFAULT_STACK_ADDRESS, DEFAULT_PROGRAM_ADDRESS);
    }

    public StateContainer(StateContainer stateContainer) {
        this(stateContainer.getStackAddress(), stateContainer.getProgramAddress());

        clearAndInitFiles(0);
        
        stateContainer.labels.forEach(map -> this.labels.add(new HashMap<>(map)));
        stateContainer.consts.forEach(map -> this.consts.add(new HashMap<>(map)));
        stateContainer.data.forEach(map -> this.data.add(new HashMap<>(map)));
        this.globals.putAll(stateContainer.globals);
        this.nestingMap.putAll(stateContainer.nestingMap);

        this.nestingCount = stateContainer.nestingCount;
        this.symbolAddress = stateContainer.symbolAddress;
        this.writableDataAddress = stateContainer.writableDataAddress;
        this.pseudoInstructionAddress = stateContainer.pseudoInstructionAddress;
        this.memoryPos = new FilePos(stateContainer.memoryPos);

        for (int i = 0; i < REGISTER_NUMBER; i++) {
            registers[i].setData(stateContainer.getRegister(i).getData());
        }

        this.cpsr.setData(stateContainer.getCPSR().getData());
        this.spsr.setData(stateContainer.spsr.getData());

        this.memory.putAll(stateContainer.memory);
    }

    /**
     * Clear all registers
     */
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

    /**
     * Evaluate math expression using the context of this state container restricted to accessible constants (in the current file)
     *
     * @param expString the math expression
     * @return the computed value
     * @throws SyntaxASMException when the math expression is malformed
     */
    public int evalWithAccessibleConsts(String expString) throws SyntaxASMException {
        try {
            ExpressionBuilder builder = new ExpressionBuilder(preEvaluationFormat(expString));

            consts.forEach(map -> builder.variables(map.keySet()));

            Expression exp = builder.build();

            for (Map.Entry<String, Integer> entry : consts.get(memoryPos.getFileIndex()).entrySet()) {
                exp.setVariable(entry.getKey(), (double) entry.getValue());
            }

            return (int) exp.evaluate();
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.math.malformed", expString, exception.getMessage()));
        }
    }

    public int evalBranch(String expString) throws SyntaxASMException {
        try {
            ExpressionBuilder builder = new ExpressionBuilder(preEvaluationFormat(expString));

            getAccessibleConsts().forEach((key, val) -> builder.variables(key));
            getAccessibleData().forEach((key, val) -> builder.variables(key));
            getAccessibleLabels().forEach((key, val) -> builder.variables(key));

            Expression exp = builder.build();

            getAccessibleConsts().forEach((key, val) -> exp.setVariable(key, (double) val));
            getAccessibleData().forEach((key, val) -> exp.setVariable(key, (double) val));;
            getAccessibleLabels().forEach((key, val) -> exp.setVariable(key, val - this.memoryPos.getPos()));

            return (int) exp.evaluate();
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.unknownLabel", expString, exception.getMessage()));
        }
    }

    /**
     * Evaluate math expression using the context of this state container
     *
     * @param expString the math expression
     * @return the computed value
     * @throws SyntaxASMException when the math expression is malformed
     */
    public int evalWithAll(String expString) throws SyntaxASMException {
        try {
            ExpressionBuilder builder = new ExpressionBuilder(preEvaluationFormat(expString));

            consts.forEach(map -> builder.variables(map.keySet()));
            data.forEach(map -> builder.variables(map.keySet()));

            Expression exp = builder.build();

            for (int i = 0 ; i < consts.size() ; i++) {
                for (Map.Entry<String, Integer> entry : consts.get(i).entrySet()) {
                    exp.setVariable(entry.getKey(), (double) entry.getValue());
                }

                for (Map.Entry<String, Integer> entry : data.get(i).entrySet()) {
                    exp.setVariable(entry.getKey(), (double) entry.getValue());
                }
            }

            return (int) Math.floor(exp.evaluate());
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.math.malformed", expString, exception.getMessage()));
        }
    }

    /**
     * Evaluate math expression using the context of this state container restricted to accessible variables (in the current file)
     *
     * @param expString the math expression
     * @return the computed value
     * @throws SyntaxASMException when the math expression is malformed
     */
    public int evalWithAccessible(String expString) throws SyntaxASMException {
        try {
            ExpressionBuilder builder = new ExpressionBuilder(preEvaluationFormat(expString));

            getAccessibleConsts().forEach((key, val) -> builder.variables(key));
            getAccessibleData().forEach((key, val) -> builder.variables(key));
            getAccessibleLabels().forEach((key, val) -> builder.variables(key));

            Expression exp = builder.build();

            getAccessibleConsts().forEach((key, val) -> exp.setVariable(key, (double) val));
            getAccessibleData().forEach((key, val) -> exp.setVariable(key, (double) val));;
            getAccessibleLabels().forEach((key, val) -> exp.setVariable(key, val - this.memoryPos.getPos()));

            return (int) Math.floor(exp.evaluate());
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.math.malformed", expString, exception.getMessage()));
        }
    }

    /**
     * Format math expression before evaluation
     * @param s the math expression
     * @return the formatted math expression
     */
    private String preEvaluationFormat(String s) {
        if (s.startsWith("=") || s.startsWith("#")) s = s.substring(1);

        return SPECIAL_VALUE_PATTERN.matcher(s).replaceAll(matchResult -> {
            String valueString = matchResult.group("VALUE").toUpperCase();
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
            return valueString;
        }).toUpperCase();

    }

    public int getStackAddress() {
        return stackAddress;
    }

    public int getProgramAddress() {
        return programAddress;
    }

    /**
     * @return an unmodifiable list of the global variables
     */
    public List<String> getGlobals() {
        return globals.keySet().stream().toList();
    }

    /**
     * Add a global variable
     *
     * @param global the name of the variable
     * @param fileIndex the file index of the global variable
     */
    public void addGlobal(String global, int fileIndex) {
        this.globals.put(global, fileIndex);
    }

    /**
     * @param global the name of the variable
     * @return the file index of the global variable
     */
    public int getGlobal(String global) {
        return globals.get(global);
    }

    public void clearGlobals() {
        this.globals.clear();
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
     * @return active branches with link count
     */
    public int getNestingCount() {
        return nestingCount;
    }

    /**
     * Update nesting counter when branching with link
     *
     * @param initialPos the position of the instruction (initial pos before branching)
     */
    public void branchWithLink(int initialPos) {
        this.nestingCount++;
        if (nestingMap.containsKey(initialPos + 4)) {
            nestingMap.put(initialPos + 4, nestingMap.get(initialPos + 4) + 1);
        } else {
            nestingMap.put(initialPos + 4, 1);
        }
    }

    /**
     * Update nesting counter when returning link
     */
    public void returnLink() {
        if (nestingMap.containsKey(getPC().getData())) {
            if (nestingMap.get(getPC().getData()) == 1) {
                nestingMap.remove(getPC().getData());
            } else {
                nestingMap.put(getPC().getData(), nestingMap.get(getPC().getData()) - 1);
            }

            this.nestingCount--;
            if (this.nestingCount < 0) this.nestingCount = 0;
        }
    }

    /**
     * @return the address of the symbol range in memory (end of the instruction range)
     */
    public int getSymbolAddress() {
        return symbolAddress;
    }

    /**
     * @return the address of the pseudo instruction range in memory (end of the RO range)
     */
    public int getPseudoInstructionAddress() {
        return pseudoInstructionAddress;
    }

    /**
     * @return the address of the writable data range in memory (end of RO/pseudo instruction range)
     */
    public int getWritableDataAddress() {
        return writableDataAddress;
    }

    /**
     * Set the first address of the symbol range in memory (end of the instruction range) using current position
     */
    public void startSymbolRange() {
        this.symbolAddress = memoryPos.getPos();
    }

    /**
     * Set the first address of the address of the pseudo instruction range in memory (end of the RO range) using current position
     */
    public void startPseudoInstructionRange() {
        this.pseudoInstructionAddress = memoryPos.getPos();
    }

    /**
     * Set the first address of the writable data range in memory (end of the RO/Pseudo-instruction range) using current position
     */
    public void startWritableData() {
        this.writableDataAddress = memoryPos.getPos();
    }

    /**
     * @return the constants accessible within the current file
     */
    public AccessibleValueMap getAccessibleConsts() {
        return new AccessibleValueMap(consts, globals, memoryPos.getFileIndex());
    }

    /**
     * @return the data accessible within the current file
     */
    public AccessibleValueMap getAccessibleData() {
        return new AccessibleValueMap(data, globals, memoryPos.getFileIndex());
    }

    /**
     * @return the data defined in the current file (excluding globals)
     */
    public HashMap<String, Integer> getRestrainedData() {
        return data.get(memoryPos.getFileIndex());
    }

    /**
     * @return the labels accessible in the current file
     */
    public AccessibleValueMap getAccessibleLabels() {
        return new AccessibleValueMap(labels, globals, memoryPos.getFileIndex());
    }

    /**
     * @return the labels defined in the current file (excluding globals)
     */
    public HashMap<String, Integer> getRestrainedLabels() {
        return labels.get(memoryPos.getFileIndex());
    }

    /**
     * @return all the labels with the index of the file in which they're defined
     */
    public ArrayList<HashMap<String, Integer>> getLabelsInFiles() {
        return labels;
    }

    /**
     * @return all the labels
     */
    public MultiValuedMap<String, FilePos> getAllLabels() {
        return new AllLabelsMap(labels);
    }

    /**
     * Initialize the variables for a specific number of files
     *
     * @param size the number of files
     */
    public void clearAndInitFiles(int size) {
        labels.clear();
        consts.clear();
        data.clear();

        for (int i = 0 ; i < size ; i++) {
            labels.add(new HashMap<>());
            consts.add(new HashMap<>());
            data.add(new HashMap<>());
        }
    }

    /**
     * Return the current position (file index / memory address) which used to setup memory during initialization
     *
     * @return current position
     */
    public FilePos getCurrentMemoryPos() {
        return memoryPos;
    }

    /**
     * Reinitialize the current position (file index / memory address) which used to setup memory during initialization
     *
     * @return current position
     */
    public FilePos resetMemoryPos() {
        memoryPos.setPos(getProgramAddress());
        return memoryPos;
    }

    public Register getRegister(int i) {
        return registers[i];
    }

    /**
     * Sets all register to non-null value
     *
     * @return this
     */
    public StateContainer withTestingRegister() {
        for (int i = 0; i < registers.length; i++) {
            registers[i].setData(i + 1);
        }
        return this;
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

    public ProgramStatusRegister getCPSR() {
        return cpsr;
    }

    public ProgramStatusRegister getSPSR() {
        return spsr;
    }

    public MemoryAccessor getMemory() {
        return memory;
    }

    public Register[] getRegisters() {
        return registers;
    }
}
