package fr.dwightstudio.jarmemu.asm.directive;

import fr.dwightstudio.jarmemu.asm.ParsedObject;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;

public class ParsedDirectiveLabel extends ParsedObject {
    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {

    }
}
