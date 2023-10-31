package fr.dwightstudio.jarmemu.sim.obj;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;

public class StateContainer {

    // ASM
    public final HashMap<String, Integer> consts; // HashMap des constantes
    public final HashMap<String, Integer> data; // HashMap des données
    public final HashMap<String, Integer> labels; // HashMap des labels

    // Registers
    public static final int REGISTER_NUMBER = 16;
    public final Register[] registers;
    public final PSR cpsr;
    public final PSR spsr;

    // Memory
    public final MemoryAccessor memory;

    public StateContainer() {

        // ASM
        labels = new HashMap<>();
        consts = new HashMap<>();
        data = new HashMap<>();

        // Initializing registers
        this.registers = new Register[REGISTER_NUMBER];
        clearRegisters();

        cpsr = new PSR();
        spsr = new PSR();

        // Initializing memory
        this.memory = new MemoryAccessor();
    }

    public void clearRegisters() {
        for (int i = 0; i < REGISTER_NUMBER; i++) {
            registers[i] = new Register();
        }
    }

    public int eval(String expString, HashMap<String, Integer> map) {
        try {
            Expression exp = new ExpressionBuilder(expString).variables(map.keySet()).build();
            for (String str : map.keySet()) {
                exp.setVariable(str, (double) map.get(str));
            }
            return (int) exp.evaluate();
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException("Malformed math expression '" + expString + "'");
        }
    }

}
