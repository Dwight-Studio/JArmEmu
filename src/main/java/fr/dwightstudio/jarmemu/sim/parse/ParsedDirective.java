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
    private final int currentPos;
    private final int nextPos;
    private boolean generated;

    public ParsedDirective(@NotNull Directive directive, @NotNull String args, int currentPos) {
        this.directive = directive;
        this.args = args;
        this.currentPos = currentPos;

        int tempNextPos;

        try {
            tempNextPos = currentPos + directive.computeDataLength(args, currentPos);
        } catch (SyntaxASMException e) {
            tempNextPos = currentPos;
        }

        this.nextPos = tempNextPos;
    }

    @Override
    public AssemblyError verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer stateContainer = stateSupplier.get();

        try {
            apply(stateContainer);
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
    public void apply(StateContainer stateContainer) {
        int symbolAddress = stateContainer.getSymbolsAddress();
        directive.apply(stateContainer, this.args, currentPos + symbolAddress);
    }

    public int getNextPos() {
        return nextPos;
    }

    public Directive getDirective() {
        return directive;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated() {
        this.generated = true;
    }
}
