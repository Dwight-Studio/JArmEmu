package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.Directive;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.PhantomReference;
import java.util.function.Supplier;

public class ParsedDirective extends ParsedObject {

    private final Directive directive;
    private final String args;
    private final int currentPos;

    public ParsedDirective(@NotNull Directive directive, @NotNull String args, int currentPos) {
        this.directive = directive;
        this.args = args;
        this.currentPos = currentPos;
    }

    /**
     * Calcul de la place en mémoire nécessaire pour cette directive
     */
    public int computeDataLength() {
        try {
            return directive.computeDataLength(args, currentPos);
        } catch (SyntaxASMException e) {
            return 0;
        }
    }

    @Override
    public AssemblyError verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer stateContainer = stateSupplier.get();

        try {
            apply(stateContainer);
            return null;
        } catch (SyntaxASMException exception) {
            return new AssemblyError(line, exception);
        } finally {
            AddressParser.reset(stateContainer);
            RegisterWithUpdateParser.reset(stateContainer);
        }
    }

    /**
     * Application de la directive
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     */
    public void apply(StateContainer stateContainer) {
        directive.apply(stateContainer, this.args, currentPos);
    }
}
