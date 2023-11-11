package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.nio.ByteBuffer;

public class FillExecutor implements DirectiveExecutor {
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

        switch (arg.length) {
            case 1 -> apply(stateContainer, args + ", 0, 1", currentPos);

            case 2 -> apply(stateContainer, args + ", 1", currentPos);

            case 3 -> {
                int totalNum = stateContainer.evalWithConsts(arg[0]);
                int value = stateContainer.evalWithConsts(arg[1]);
                int valueSize = stateContainer.evalWithConsts(arg[2]);

                if (valueSize <= 0) throw new SyntaxASMException("Invalid value size '" + valueSize + "' (must be positive)");

                byte[] bytes = new byte[valueSize];

                switch (valueSize) {
                    case 1 -> ByteBuffer.wrap(bytes).put((byte) (value & 0xFF));

                    case 2 -> ByteBuffer.wrap(bytes).putShort((short) (value & 0xFFFF));

                    case 3 -> ByteBuffer.wrap(bytes).put((byte) ((value >> 16) & 0xFF)).putShort((short) (value & 0xFFFF));

                    default -> {
                        ByteBuffer buffer = ByteBuffer.wrap(bytes);

                        for (int i = 0 ; i < valueSize - 4 ; i++) {
                            buffer.put((byte) 0);
                        }

                        buffer.putInt(value);
                    }
                }

                for (int i = currentPos ; i < currentPos + totalNum ; i++) {
                    stateContainer.memory.putByte(currentPos + i, bytes[i % valueSize]);
                }
            }

            default -> throw new SyntaxASMException("Invalid arguments '" + args + "' for Fill directive");
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
        String[] arg = args.split(",");
        return stateContainer.evalWithConsts(arg[0]);
    }
}
