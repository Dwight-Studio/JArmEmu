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
    private final ArrayList<HashMap<String, Integer>> consts; // Indice de fichier -> Nom -> Constantes
    private final ArrayList<HashMap<String, Integer>> data; // Indice de fichier -> Nom -> Données ajoutées dans la mémoire par directive
    private final ArrayList<HashMap<String, Integer>> labels; // Indice de fichier -> Label -> Position dans la mémoire
    private final HashMap<String, Integer> globals; // Symbols globaux -> Indice de fichier
    private int nestingCount;
    private int lastAddressROData;
    private int firstAddressPseudoInstruction;
    private FilePos currentfilePos; // Position dans la mémoire et fichier en cours de lecture
    private Integer addressRegisterUpdateValue;

    // Registers
    public static final int REGISTER_NUMBER = 16;
    private final Register[] registers;
    private final ProgramStatusRegister cpsr;
    private final ProgramStatusRegister spsr;

    // Memory
    private final MemoryAccessor memory;
    private final int stackAddress;
    private final int symbolsAddress;

    public StateContainer(int stackAddress, int symbolsAddress) {
        this.stackAddress = stackAddress;
        this.symbolsAddress = symbolsAddress;

        // ASM
        labels = new ArrayList<>();
        consts = new ArrayList<>();
        data = new ArrayList<>();
        globals = new HashMap<>();

        nestingCount = 0;
        lastAddressROData = 0;
        firstAddressPseudoInstruction = 0;
        addressRegisterUpdateValue = null;
        currentfilePos = new FilePos(0, symbolsAddress);

        // Initializing registers
        cpsr = new ProgramStatusRegister();
        spsr = new ProgramStatusRegister();

        this.registers = new Register[REGISTER_NUMBER];
        clearRegisters();
        clearAndInitFiles(1);

        // Initializing memory
        this.memory = new MemoryAccessor();
    }

    public StateContainer() {
        this(DEFAULT_STACK_ADDRESS, DEFAULT_SYMBOLS_ADDRESS);
    }

    public StateContainer(StateContainer stateContainer) {
        this(stateContainer.getStackAddress(), stateContainer.getSymbolsAddress());

        clearAndInitFiles(0);
        
        stateContainer.labels.forEach(map -> this.labels.add(new HashMap<>(map)));
        stateContainer.consts.forEach(map -> this.consts.add(new HashMap<>(map)));
        stateContainer.data.forEach(map -> this.data.add(new HashMap<>(map)));
        this.globals.putAll(stateContainer.globals);

        this.nestingCount = stateContainer.nestingCount;
        this.lastAddressROData = stateContainer.lastAddressROData;
        this.firstAddressPseudoInstruction = stateContainer.firstAddressPseudoInstruction;
        this.addressRegisterUpdateValue = stateContainer.addressRegisterUpdateValue;
        this.currentfilePos = new FilePos(stateContainer.currentfilePos);

        for (int i = 0; i < REGISTER_NUMBER; i++) {
            registers[i].setData(stateContainer.getRegister(i).getData());
        }

        this.cpsr.setData(stateContainer.getCPSR().getData());
        this.spsr.setData(stateContainer.spsr.getData());

        this.memory.putAll(stateContainer.memory);
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

    public int evalWithAccessibleConsts(String expString) throws SyntaxASMException {
        try {
            ExpressionBuilder builder = new ExpressionBuilder(preEval(expString));

            consts.forEach(map -> builder.variables(map.keySet()));

            Expression exp = builder.build();

            for (Map.Entry<String, Integer> entry : consts.get(currentfilePos.getFileIndex()).entrySet()) {
                exp.setVariable(entry.getKey(), (double) entry.getValue());
            }

            return (int) exp.evaluate();
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.math.malformed", expString, exception.getMessage()));
        }
    }

    public int evalWithAll(String expString) throws SyntaxASMException {
        try {
            ExpressionBuilder builder = new ExpressionBuilder(preEval(expString));

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

    public int evalWithAccessible(String expString) throws SyntaxASMException {
        try {
            ExpressionBuilder builder = new ExpressionBuilder(preEval(expString));

            builder.variables(getAccessibleConsts().keySet());
            builder.variables(getAccessibleData().keySet());

            Expression exp = builder.build();

            for (Map.Entry<String, Integer> entry : getAccessibleConsts().entrySet()) {
                exp.setVariable(entry.getKey(), (double) entry.getValue());
            }

            for (Map.Entry<String, Integer> entry : getAccessibleData().entrySet()) {
                exp.setVariable(entry.getKey(), (double) entry.getValue());
            }

            return (int) Math.floor(exp.evaluate());
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.math.malformed", expString, exception.getMessage()));
        }
    }

    private String preEval(String s) {

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

    public int getSymbolsAddress() {
        return symbolsAddress;
    }

    /**
     * @return une liste non modifiable des variables globales.
     */
    public List<String> getGlobals() {
        return globals.keySet().stream().toList();
    }

    /**
     * Ajoute une variable globale.
     *
     * @param global la variable globale
     * @param fileIndex l'indice du fichier de la variable globale
     */
    public void addGlobal(String global, int fileIndex) {
        this.globals.put(global, fileIndex);
    }

    /**
     * @param global le nom de la variable globale
     * @return l'indice du fichier de la variable globale
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

    public int getLastAddressRORange() {
        return lastAddressROData;
    }

    /**
     * Fixe la dernière adresse de la plage de lecture seule à partir de la position courante
     */
    public void closeReadOnlyRange() {
        this.lastAddressROData = currentfilePos.getPos();
    }

    public int getFirstAddressPIRange() {
        return firstAddressPseudoInstruction;
    }

    /**
     * Fixe la première adresse de la plage d'allocation pour les pseudo-instructions à partir de la position courante
     */
    public void startPseudoInstructionRange() {
        this.firstAddressPseudoInstruction = currentfilePos.getPos();
    }

    /**
     * @return les constantes accessibles dans le fichier actuel
     */
    public AccessibleValueMap getAccessibleConsts() {
        return new AccessibleValueMap(consts, globals, currentfilePos.getFileIndex());
    }

    /**
     * @return les données accessibles dans le fichier actuel
     */
    public AccessibleValueMap getAccessibleData() {
        return new AccessibleValueMap(data, globals, currentfilePos.getFileIndex());
    }

    /**
     * @return les données définies dans le fichier actuel (exclusion des globals)
     */
    public HashMap<String, Integer> getRestrainedData() {
        return data.get(currentfilePos.getFileIndex());
    }

    /**
     * @return les labels accessibles dans le fichier actuel
     */
    public AccessibleValueMap getAccessibleLabels() {
        return new AccessibleValueMap(labels, globals, currentfilePos.getFileIndex());
    }

    /**
     * @return les labels définis dans le fichier actuel (exclusion des globals)
     */
    public HashMap<String, Integer> getRestrainedLabels() {
        return labels.get(currentfilePos.getFileIndex());
    }

    /**
     * @return les labels répartis dans les fichiers
     */
    public ArrayList<HashMap<String, Integer>> getLabelsInFiles() {
        return labels;
    }

    /**
     * @return tous les labels (tous fichiers confondus)
     */
    public MultiValuedMap<String, FilePos> getAllLabels() {
        return new AllLabelsMap(labels);
    }

    /**
     * Initialise les variables pour un nombre de fichiers spécifique.
     *
     * @param size le nombre de fichiers
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
     * Retourne la position courante (indice de fichier / adresse mémoire), utilisé lors de la construction du conteneur d'état
     *
     * @return la position courante
     */
    public FilePos getCurrentFilePos() {
        return currentfilePos;
    }

    /**
     * Réinitialise la position courante (indice de fichier / adresse mémoire), utilisé lors de la construction du conteneur d'état
     *
     * @return la position courante
     */
    public FilePos resetFilePos() {
        currentfilePos.setPos(getSymbolsAddress());
        return currentfilePos;
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

    public Integer getAddressRegisterUpdateValue() {
        int rtn = addressRegisterUpdateValue;
        addressRegisterUpdateValue = null;
        return rtn;
    }

    public void setAddressRegisterUpdateValue(int addressRegisterUpdateValue) {
        this.addressRegisterUpdateValue = addressRegisterUpdateValue;
    }

    public void resetAddressRegisterUpdateValue() {
        addressRegisterUpdateValue = null;
    }

    public boolean hasAddressRegisterUpdate() {
        return this.addressRegisterUpdateValue != null;
    }
}
