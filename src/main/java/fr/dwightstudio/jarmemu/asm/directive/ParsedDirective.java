package fr.dwightstudio.jarmemu.asm.directive;

import fr.dwightstudio.jarmemu.asm.Contextualized;
import fr.dwightstudio.jarmemu.asm.ParsedObject;
import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.instruction.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class ParsedDirective extends ParsedObject implements Contextualized {

    private static final Logger logger = Logger.getLogger(ParsedInstruction.class.getName());

    protected final Section section;
    protected final String args;
    private boolean generated;
    protected String hash;

    public ParsedDirective(Section section, String args) {
        this.args = args;
        this.section = section;
        generated = false;
    }

    /**
     * Contextualise la directive dans le conteneur d'état initial, après définition des constantes.
     *
     * @param stateContainer le conteneur d'état initial
     */
    public abstract void contextualize(StateContainer stateContainer) throws ASMException;

    /**
     * @param stateContainer le conteneur d'état sur lequel appliquer la directive
     */
    public abstract void execute(StateContainer stateContainer) throws ASMException;

    /**
     * Alloue la place nécessaire dans la mémoire, en fonction des données analysées.
     *
     * @param stateContainer le conteneur d'état sur lequel appliquer la directive
     */
    public abstract void offsetMemory(StateContainer stateContainer) throws ASMException;

    /**
     * @return vrai si la directive est responsable de la construction du contexte.
     */
    public abstract boolean isContextBuilder();

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        try {
            execute(stateSupplier.get());
        } catch (ASMException exception) {
            exception.with(this);
        }
    }

    public Section getSection() {
        return section;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedDirective dir)) return false;

        if (!(dir.args.equals(this.args))) {
            if (VERBOSE) logger.info("Difference: Args");
            return false;
        }

        if (dir.generated != this.generated) {
            if (VERBOSE) logger.info("Difference: Generated flag");
            return false;
        }

        if (dir.hash == null) {
            if (this.hash != null) {
                if (VERBOSE) logger.info("Difference: Hash (Null)");
                return false;
            }
        } else {
            if (!(dir.hash.equalsIgnoreCase(this.hash))) {
                if (VERBOSE) logger.info("Difference: Hash");
                return false;
            }
        }

        if (dir.section == null) {
            if (this.section != null) {
                if (VERBOSE) logger.info("Difference: Section (Null)");
                return false;
            }
        } else {
            if (!(dir.section.equals(this.section))) {
                if (VERBOSE) logger.info("Difference: Section");
                return false;
            }
        }

        return true;
    }

    public ParsedDirective withLineNumber(int lineNumber) {
        setLineNumber(lineNumber);
        return this;
    }
}
