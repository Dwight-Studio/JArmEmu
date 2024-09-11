package fr.dwightstudio.jarmemu.base.util;

import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.asm.modifier.RequiredModifierParameter;

import java.util.*;

public class ModifierUtils {

    /**
     * Iterator used to iterate over all possible combination of modifier parameters
     */
    public static class PossibleModifierIterator implements Iterator<Modifier> {
        private final ArrayList<Class<? extends Enum<? extends ModifierParameter>>> classes;
        private final int[] indices;
        private final int[] finalIndices;
        private boolean finished;

        public PossibleModifierIterator(List<Class<? extends Enum<? extends ModifierParameter>>> classes) {
            this.classes = new ArrayList<>(classes);
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

    public static Comparator<? super Class<? extends Enum<? extends ModifierParameter>>> getComparator() {
        return new Comparator<>() {
            private static int getValue(Class<? extends Enum<? extends ModifierParameter>> clazz) {
                return switch (clazz.getSimpleName()) {
                    case "Condition" -> 3;
                    case "UpdateMode" -> 2;
                    case "UpdateFlags" -> 1;
                    case "DataMode" -> 0;
                    default -> throw new IllegalStateException("Unexpected value: " + clazz.getSimpleName());
                };
            }

            @Override
            public int compare(Class<? extends Enum<? extends ModifierParameter>> t1, Class<? extends Enum<? extends ModifierParameter>> t2) {
                return getValue(t2) - getValue(t1);
            }

            @Override
            public boolean equals(Object o) {
                return super.equals(o);
            }
        };
    }
}
