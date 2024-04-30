package fr.dwightstudio.jarmemu.base.util;

public record CaseIndependentEntry(String string) {

    @Override
    public boolean equals(Object o) {
        if (o instanceof String s) {
            return string.equalsIgnoreCase(s);
        } else if (o instanceof CaseIndependentEntry s) {
            return string.equalsIgnoreCase(s.string);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

    @Override
    public String toString() {
        return string;
    }
}
