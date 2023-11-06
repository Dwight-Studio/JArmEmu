package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class ParsedLabel extends ParsedObject {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String name;
    private final int pos;
    private final boolean directiveLabel;

    public ParsedLabel(String name, int pos, boolean directiveLabel) {
        this.name = name;
        this.pos = pos;
        this.directiveLabel = directiveLabel;
    }

    public ParsedLabel(String name, int pos) {
        this.name = name;
        this.pos = pos;
        this.directiveLabel = false;
    }

    public String getName() {
        return name;
    }

    public int getPos() {
        return pos;
    }

    @Override
    public AssemblyError verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer container = stateSupplier.get();

        if (directiveLabel) {
            if (container.data.get(this.name) == null) {
                throw new IllegalStateException("Unable to verify directive label (incorrectly registered in the StateContainer)");
            } else if (container.data.get(this.name) != this.pos) {
                return new AssemblyError(line, new SyntaxASMException("Directive label '" + this.name + "' is already defined"));
            }
        } else {
            if (container.labels.get(this.name) == null) {
                throw new IllegalStateException("Unable to verify label (incorrectly registered in the StateContainer)");
            } else if (container.labels.get(this.name) != this.pos) {
                return new AssemblyError(line, new SyntaxASMException("Label '" + this.name + "' is already defined"));
            }
        }
        return null;
    }

    public void register(StateContainer stateContainer) {
        if (directiveLabel)  {
            stateContainer.data.put(name.strip().toUpperCase(), pos);
        } else {
            stateContainer.labels.put(name.strip().toUpperCase(), pos);
        }
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

        if (label.pos != this.pos) {
            if (VERBOSE) logger.info("Difference: Line");
            return false;
        }

        return true;
    }

    public boolean isDirectiveLabel() {
        return directiveLabel;
    }
}
