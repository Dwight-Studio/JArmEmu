package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Function;

public class RegisterArgument extends ParsedArgument<Register> {
    
    private Function<StateContainer, Register> registerReference;

    public RegisterArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        registerReference = switch (originalString) {
            case "R0" -> stateContainer -> stateContainer.getRegister(0);
            case "R1" -> stateContainer -> stateContainer.getRegister(1);
            case "R2" -> stateContainer -> stateContainer.getRegister(2);
            case "R3" -> stateContainer -> stateContainer.getRegister(3);
            case "R4" -> stateContainer -> stateContainer.getRegister(4);
            case "R5" -> stateContainer -> stateContainer.getRegister(5);
            case "R6" -> stateContainer -> stateContainer.getRegister(6);
            case "R7" -> stateContainer -> stateContainer.getRegister(7);
            case "R8" -> stateContainer -> stateContainer.getRegister(8);
            case "R9" -> stateContainer -> stateContainer.getRegister(9);
            case "R10" -> stateContainer -> stateContainer.getRegister(10);
            case "FP", "R11" -> stateContainer -> stateContainer.getRegister(11);
            case "IP", "R12" -> stateContainer -> stateContainer.getRegister(12);
            case "SP", "R13" -> stateContainer -> stateContainer.getRegister(13);
            case "LR", "R14" -> StateContainer::getLR;
            case "PC", "R15" -> StateContainer::getPC;
            case "CPSR" -> StateContainer::getCPSR;
            case "SPSR" -> StateContainer::getSPSR;
            default -> throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.unknownRegister", originalString));
        };
    }

    @Override
    public Register getValue(StateContainer stateContainer) throws ExecutionASMException {
        return registerReference.apply(stateContainer);
    }

    @Override
    public Register getNullValue() throws BadArgumentASMException {
        throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingRegister"));
    }
}
