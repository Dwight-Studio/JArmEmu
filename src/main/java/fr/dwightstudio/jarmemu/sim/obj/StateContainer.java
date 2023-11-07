package fr.dwightstudio.jarmemu.sim.obj;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class StateContainer {

    public static final int DEFAULT_STACK_ADDRESS = 65536;
    public static final int DEFAULT_SYMBOLS_ADDRESS = 0;

    // ASM
    public final HashMap<String, Integer> consts; // HashMap des constantes
    public final HashMap<String, Integer> data; // HashMap des données ajoutées dans la mémoire par directive
    public final HashMap<String, Integer> labels; // HashMap des labels
    public final ArrayList<String> globals; // Labels globaux

    // Registers
    public static final int REGISTER_NUMBER = 16;
    public final Register[] registers;
    public final PSR cpsr;
    public final PSR spsr;

    // Memory
    public final MemoryAccessor memory;
    private final int stackAddress;
    private final int symbolsAddress;

    public StateContainer(int stackAddress, int symbolsAddress) {
        this.stackAddress = stackAddress;
        this.symbolsAddress = symbolsAddress;

        // ASM
        labels = new HashMap<>();
        consts = new HashMap<>();
        data = new HashMap<>();
        globals = new ArrayList<>();

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
        this.consts.putAll(stateContainer.consts);
        this.data.putAll(stateContainer.data);
        this.labels.putAll(stateContainer.labels);

        for (int i = 0; i < REGISTER_NUMBER; i++) {
            registers[i].setData(stateContainer.registers[i].getData());
        }

        cpsr.setData(stateContainer.cpsr.getData());
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
            Expression exp = new ExpressionBuilder(expString).variables(consts.keySet()).build();

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
            Expression exp = new ExpressionBuilder(expString).variables(consts.keySet()).variables(data.keySet()).build();

            for (String str : consts.keySet()) {
                exp.setVariable(str, (double) consts.get(str));
            }
            for (String str : data.keySet()) {
                exp.setVariable(str, (double) data.get(str));
            }

            return (int) exp.evaluate();
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException("Malformed math expression '" + expString + "'");
        }
    }

    public int getStackAddress() {
        return stackAddress;
    }

    public int getSymbolsAddress() {
        return symbolsAddress;
    }
}
