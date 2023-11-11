package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class ASCIIExecutor implements DirectiveExecutor {
    /**
     * Application de la directive
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     */
    @Override
    public void apply(StateContainer stateContainer, String args, int currentPos) {
        if ((args.startsWith("\"") && args.endsWith("\"")) || (args.startsWith("'") && args.endsWith("'"))) {
            String del = String.valueOf(args.charAt(0));
            String str = args.substring(1, args.length()-1);
            if (str.contains(del)) throw new SyntaxASMException("Invalid argument '" + args + "' for ASCII directive");
            for (char c : str.toCharArray()) {
                DirectiveExecutors.BYTE.apply(stateContainer, String.valueOf((int) c), currentPos);
                currentPos++;
            }
        } else {
            throw new SyntaxASMException("Invalid argument '" + args + "' for ASCII directive");
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
        String str = args.substring(1, args.length() - 1);
        return str.length();
    }
}
