package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class EquivalentExecutor implements DirectiveExecutor {
    /**
     * Application de la directive
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     */
    @Override
    public void apply(StateContainer stateContainer, String args, int currentPos) {
        String[] arg = args.split(",");

        if (arg.length == 2) {
            String symbol = arg[0];

            if (!symbol.matches("[A-Za-z_0-9]+")) {
                throw new SyntaxASMException("Invalid symbol name '" + symbol + "'");
            } else {
                int val = stateContainer.evalWithConsts(arg[1]);

                stateContainer.consts.put(symbol, val);
            }
        } else {
            throw new SyntaxASMException("Invalid arguments '" + args + "' for Equivalent directive");
        }
    }

    /**
     * Calcul de la taille prise en mémoire
     *
     * @param stateContainer Le conteneur d'état sur lequel calculer
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle
     * @return la taille des données
     */
    @Override
    public int computeDataLength(StateContainer stateContainer, String args, int currentPos) {
        return 0;
    }
}
