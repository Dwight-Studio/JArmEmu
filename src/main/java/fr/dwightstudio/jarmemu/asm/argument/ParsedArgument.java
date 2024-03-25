package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.ParsedObject;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;

public abstract class ParsedArgument<T> extends ParsedObject {

    protected final String originalString;

    /**
     * Analyse la chaîne de caractères en entrée pour définir les paramètres internes de l'argument.
     *
     * @param originalString la chaîne de caractères
     */
    public ParsedArgument(String originalString) {
        this.originalString = originalString;
    }

    /**
     * @param stateContainer le conteneur d'état courant
     * @return la valeur associée à l'argument pour le conteneur d'état
     */
    public abstract T getValue(StateContainer stateContainer) throws ExecutionASMException;

    /**
     * @return la valeur nulle par défaut
     */
    public abstract T getNullValue() throws BadArgumentASMException;

    public final String getOriginalString() {
        return originalString;
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier, int currentLine) throws ASMException {
        getValue(stateSupplier.get());
    }
}
