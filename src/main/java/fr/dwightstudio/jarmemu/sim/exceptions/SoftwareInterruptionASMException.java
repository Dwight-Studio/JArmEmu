package fr.dwightstudio.jarmemu.sim.exceptions;

public class SoftwareInterruptionASMException extends ExecutionASMException{

    private final int code;

    public SoftwareInterruptionASMException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
