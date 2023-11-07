package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.Directive;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ParsedDirective extends ParsedObject {

    private final Directive directive;
    private final String args;
    private boolean generated;
    private String hash;

    public ParsedDirective(@NotNull Directive directive, @NotNull String args) {
        this.directive = directive;
        this.args = args;
    }

    @Override
    public AssemblyError verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer stateContainer = stateSupplier.get();

        try {
            apply(stateContainer, 0);
            return null;
        } catch (SyntaxASMException exception) {
            return new AssemblyError(line, exception, this);
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
}
