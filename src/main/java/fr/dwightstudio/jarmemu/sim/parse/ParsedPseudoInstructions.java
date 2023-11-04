package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;

public class ParsedPseudoInstructions extends ParsedObject {

    @Override
    public AssemblyError verify(int line, Supplier<StateContainer> stateSupplier) {
        return null;
    }
}
