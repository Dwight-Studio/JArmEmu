package fr.dwightstudio.jarmemu.util;

import java.util.ArrayList;

public class EnumUtils {
    public static <T extends Enum<T>> String[] getFromEnum(T[] list, boolean addEmpty) {
        ArrayList<String> rtn = new ArrayList<>();

        for (T elmt : list) {
            rtn.add(elmt.toString().toUpperCase());
        }

        if (addEmpty) rtn.add("");

        return rtn.toArray(new String[0]);
    }
}
