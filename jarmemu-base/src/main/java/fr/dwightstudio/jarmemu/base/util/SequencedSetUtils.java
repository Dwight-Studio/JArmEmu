package fr.dwightstudio.jarmemu.base.util;

import java.util.*;

public class SequencedSetUtils {

    @SafeVarargs
    public static <T> SequencedSet<T> of(T... elms) {
        return Collections.unmodifiableSequencedSet(new LinkedHashSet<>(Arrays.asList(elms)));
    }
}
