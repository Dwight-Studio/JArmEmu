package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.Contextualized;
import fr.dwightstudio.jarmemu.asm.ParsedObject;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.function.Supplier;

public abstract class ParsedArgument<T> extends ParsedObject implements Contextualized {

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
     * Contextualise l'argument dans le conteneur d'état initial, après définition des constantes.
     *
     * @param stateContainer le conteneur d'état initial
     */
    public abstract void contextualize(StateContainer stateContainer) throws ASMException;

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
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        getValue(stateSupplier.get());
    }
}
