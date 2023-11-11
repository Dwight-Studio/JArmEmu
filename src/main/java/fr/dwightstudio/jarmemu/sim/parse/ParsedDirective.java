package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.Directive;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.AddressParser;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class ParsedDirective extends ParsedObject {

    private final Directive directive;
    private final String args;
    private boolean generated;
    private String hash;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public ParsedDirective(@NotNull Directive directive, @NotNull String args) {
        this.directive = directive;
        this.args = args;
    }

    @Override
    public SyntaxASMException verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer stateContainer = stateSupplier.get();

        try {
            apply(stateContainer, 0);
            return null;
        } catch (SyntaxASMException exception) {
            return exception.with(this);
        } finally {
            AddressParser.reset(stateContainer);
        }
    }

    /**
     * Application de la directive
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     */
    public int apply(StateContainer stateContainer, int currentPos) {
        int symbolAddress = stateContainer.getSymbolsAddress();
        directive.apply(stateContainer, this.args, currentPos + symbolAddress);

        if (generated) {
            stateContainer.pseudoData.put(hash, currentPos);
        }

        return directive.computeDataLength(stateContainer, args, currentPos) + currentPos;
    }

    public Directive getDirective() {
        return directive;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(String hash) {
        this.hash = hash;
        this.generated = true;
    }

    @Override
    public String toString() {
        return directive.name();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedDirective dir)) return false;

        if (!(dir.args.equalsIgnoreCase(this.args))) {
            if (VERBOSE) logger.info("Difference: Args");
            return false;
        }

        if (dir.directive != this.directive) {
            if (VERBOSE) logger.info("Difference: Directive");
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

        return true;
    }
}
