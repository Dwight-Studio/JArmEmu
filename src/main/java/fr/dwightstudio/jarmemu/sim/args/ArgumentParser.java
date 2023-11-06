package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

public interface ArgumentParser<T> {

    /**
     * Analyse le texte et renvoie la valeur de l'argument.
     *
     * @apiNote Ce n'est pas conçu pour être appelé plusieurs fois ! (Modifie stateContainer dans certaines situations)
     * @param stateContainer le conteneur d'état
     * @param string le texte à analyser
     * @return la valeur de l'argument
     */
    public T parse(@NotNull StateContainer stateContainer, @NotNull String string);

    /**
     * @return la valeur par défaut si l'argument n'est pas présent
     */
    public T none();

    /**
     * Renvoie la valeur par défaut ou met en forme l'argument
     *
     * @param i le numéro de l'argument
     * @return la valeur par défaut si l'argument n'est pas présent
     */
    public default T none(int i) {
        try {
            return none();
        } catch (SyntaxASMException exception) {
            SyntaxASMException ne = new SyntaxASMException(exception.getMessage() + " (Arg #" + i + ")");
            ne.setStackTrace(exception.getStackTrace());
            throw ne;
        }
    }
}
