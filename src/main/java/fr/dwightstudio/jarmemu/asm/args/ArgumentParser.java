package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.StateContainer;
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
    public abstract T parse(@NotNull StateContainer stateContainer, @NotNull String string);

}
