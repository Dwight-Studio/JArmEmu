package fr.dwightstudio.jarmemu.base.util;

import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.asm.modifier.RequiredModifierParameter;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ModifierUtils {

    /**
     * Iterator used to iterate over all possible combination of modifier parameters
     */
    public static class PossibleModifierIterator implements Iterator<Modifier> {
        private final List<Class<? extends Enum<? extends ModifierParameter>>> classes;
        private final int[] indices;
        private final int[] finalIndices;
        private boolean finished;

        public PossibleModifierIterator(Set<Class<? extends Enum<? extends ModifierParameter>>> classes) {
            this.classes = classes.stream().toList();
            indices = new int[classes.size()];
            finalIndices = new int[classes.size()];
            finished = false;

            for (int i = 0; i < classes.size(); i++) {
                Class<?> clazz = this.classes.get(i);
                finalIndices[i] = clazz.getEnumConstants().length + (RequiredModifierParameter.class.isAssignableFrom(clazz) ? 0 : 1);
            }
        }

        @Override
        public boolean hasNext() {
            return !finished;
        }

        @Override
        public Modifier next() {
            Modifier modifier = new Modifier();

            for (int i = 0; i < classes.size(); i++) {
                try {
                    modifier = modifier.with((ModifierParameter) classes.get(i).getEnumConstants()[indices[i]]);
                } catch (ArrayIndexOutOfBoundsException ignored) {}
            }

            finished = true;
            for (int i = 0; i < classes.size(); i++) {
                if (indices[i] + 1 == finalIndices[i]) {
                    indices[i] = 0;
                } else {
                    indices[i]++;
                    finished = false;
                    break;
                }
            }
            return modifier;
        }
    }
}
