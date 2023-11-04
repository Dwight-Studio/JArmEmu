package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.HashMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ParsedLabel extends ParsedObject {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String name;
    private final int line;

    public ParsedLabel(String name, int line) {
        this.name = name;
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }

    @Override
    public AssemblyError verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer container = stateSupplier.get();
        if (container.labels.get(this.name) == null) {
            throw new IllegalStateException("Unable to verify label (incorrectly registered in the StateContainer)");
        } else if (container.labels.get(this.name) != this.line) {
            return new AssemblyError(line, new SyntaxASMException("Label '" + this.name + "' is already defined"));
        }
        return null;
    }

    public void register(StateContainer stateContainer) {
        stateContainer.labels.put(name.strip().toUpperCase(), line);
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedLabel label)) return false;

        if (label.name == null) {
            if (this.name != null) {
                if (VERBOSE) logger.info("Difference: Name (Null)");
                return false;
            }
        } else {
            if (!(label.name.equalsIgnoreCase(this.name))) {
                if (VERBOSE) logger.info("Difference: Name");
                return false;
            }
        }

        if (label.line != this.line) {
            if (VERBOSE) logger.info("Difference: Line");
            return false;
        }

        return true;
    }
}
