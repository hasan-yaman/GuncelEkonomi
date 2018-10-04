package com.hasanyaman.guncelekonomi.Utilities;

public class ArrayUtilities {

    public static boolean isAllDone(boolean[] allDone) {
        for (boolean b : allDone) {
            if (!b) {
                return false;
            }
        }
        return true;
    }
}
