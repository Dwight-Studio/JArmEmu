package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;

public class ImmediateOrRegisterArgument extends ParsedArgument<Integer> {

    private boolean immediate;
    private ImmediateArgument immediateArgument;
    private RegisterArgument registerArgument;

    public ImmediateOrRegisterArgument(String originalString) {
        super(originalString);
    }

    @Override
    protected void parse(String originalString) throws SyntaxASMException {
        immediate = originalString.startsWith("#") || originalString.startsWith("=") || originalString.startsWith("*");

        if (immediate) {
            immediateArgument = new ImmediateArgument(originalString);
        } else {
            registerArgument = new RegisterArgument(originalString);
        }
    }

    @Override
    public Integer getValue(StateContainer stateContainer) throws ExecutionASMException {
        if (immediate) {
            return immediateArgument.getValue(stateContainer);
        } else {
            return registerArgument.getValue(stateContainer).getData();
        }
    }

    @Override
    public Integer getNullValue() throws BadArgumentASMException {
        return 0; // FIXME: Pas sûr de ça, est-ce que cela pose un problème si il n'y a pas d'argument?
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier, int currentLine) throws ASMException {
        if (immediate) {
            immediateArgument.verify(stateSupplier, currentLine);
        } else {
            registerArgument.verify(stateSupplier, currentLine);
        }
    }
}
