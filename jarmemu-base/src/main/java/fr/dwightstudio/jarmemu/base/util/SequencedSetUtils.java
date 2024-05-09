package fr.dwightstudio.jarmemu.base.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.SequencedSet;

public class SequencedSetUtils {

    @SafeVarargs
    public static <T> SequencedSet<T> of(T... elms) {
        return Collections.unmodifiableSequencedSet(new LinkedHashSet<>(Arrays.asList(elms)));
    }
}
