package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.sim.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

public class CodeParser implements ArgumentParser<Integer> {
    /**
     * Analyse le texte et renvoie la valeur de l'argument.
     *
     * @param stateContainer le conteneur d'état
     * @param string         le texte à analyser
     * @return la valeur de l'argument
     * @apiNote Ce n'est pas conçu pour être appelé plusieurs fois ! (Modifie stateContainer dans certaines situations)
     */
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        return stateContainer.evalWithConsts(string);
    }

    /**
     * @return la valeur par défaut si l'argument n'est pas présent
     */
    @Override
    public Integer none() {
        throw new BadArgumentsASMException("missing code");
    }
}
