package fr.dwightstudio.jarmemu.sim.exceptions;

public class BreakpointASMException extends ExecutionASMException {

    private int value;

    public BreakpointASMException(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
