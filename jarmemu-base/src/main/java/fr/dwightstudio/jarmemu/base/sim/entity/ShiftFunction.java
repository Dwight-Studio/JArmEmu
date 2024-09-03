package fr.dwightstudio.jarmemu.base.sim.entity;

import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;

import java.util.function.BiFunction;

public class ShiftFunction {

    private final boolean identity;
    private final StateContainer stateContainer;
    private final BiFunction<StateContainer, Integer, Integer> shift;
    private boolean called;

    public ShiftFunction(StateContainer stateContainer, BiFunction<StateContainer, Integer, Integer> shift) {
        this.identity = false;
        this.stateContainer = stateContainer;
        this.shift = shift;
        this.called = false;
    }

    public ShiftFunction(StateContainer stateContainer) {
        this.identity = true;
        this.stateContainer = stateContainer;
        this.shift = null;
        this.called = false;
    }

    /**
     * Checks if the RegisterOrImmediate can be shifted
     */
    public void check(RegisterOrImmediate i) throws SyntaxASMException {
        if (!identity && !i.isRegister())
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.registerShift"));
    }

    public final int apply(RegisterOrImmediate i) {
        if (called) throw new IllegalStateException("ShiftFunctions are single-use functions");
        int rtn = i.intValue();
        if (!identity) {
            if (!i.isRegister()) throw new IllegalStateException("Immediate can't be shifted");
            rtn = this.shift.apply(stateContainer, rtn);
        }
        called = true;
        return rtn;
    }

    public final int apply(int i) {
        if (called) throw new IllegalStateException("ShiftFunctions are single-use functions");
        int rtn = i;
        if (!identity) {
            rtn = this.shift.apply(stateContainer, rtn);
        }
        called = true;
        return rtn;
    }
}
