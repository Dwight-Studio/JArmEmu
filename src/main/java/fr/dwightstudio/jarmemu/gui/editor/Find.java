package fr.dwightstudio.jarmemu.gui.editor;

public record Find(int start, int end) {
    public Find offset(int offset) {
        int s = start - offset;
        int e = end - offset;

        if (e <= 0) {
            return null;
        } else if (s < 0) {
            return new Find(0, e);
        } else {
            return new Find(s, e);
        }
    }
}
