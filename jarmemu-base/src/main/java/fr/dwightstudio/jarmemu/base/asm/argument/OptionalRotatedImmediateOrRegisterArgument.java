package fr.dwightstudio.jarmemu.base.asm.argument;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.RegisterOrImmediate;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.function.Supplier;

public class OptionalRotatedImmediateOrRegisterArgument extends ParsedArgument<RegisterOrImmediate> {

    private boolean immediate;
    private RotatedImmediateArgument immediateArgument;
    private RegisterArgument registerArgument;

    public OptionalRotatedImmediateOrRegisterArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        if (originalString != null) {
            immediate = originalString.startsWith("#") || originalString.startsWith("=") || originalString.startsWith("*");

            if (immediate) {
                immediateArgument = new RotatedImmediateArgument(originalString);
            } else {
                registerArgument = new RegisterArgument(originalString);
            }
        }
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        if (originalString != null) {
            if (immediate) {
                immediateArgument.contextualize(stateContainer);
            } else {
                registerArgument.contextualize(stateContainer);
            }
        }
    }

    @Override
    public RegisterOrImmediate getValue(StateContainer stateContainer) throws ExecutionASMException {
        if (originalString != null) {
            if (immediate) {
                stateContainer.setAddressRegisterUpdateValue(immediateArgument.getValue(stateContainer));
                return new RegisterOrImmediate(immediateArgument.getValue(stateContainer));
            } else {
                stateContainer.setAddressRegisterUpdateValue(registerArgument.getValue(stateContainer).getData());
                return new RegisterOrImmediate(registerArgument.getValue(stateContainer));
            }
        } else {
            return new RegisterOrImmediate(0);
        }
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        if (originalString != null) {
            if (immediate) {
                immediateArgument.verify(stateSupplier);
            } else {
                registerArgument.verify(stateSupplier);
            }

            super.verify(stateSupplier);
        }
    }
}
