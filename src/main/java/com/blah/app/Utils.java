package com.blah.app;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;

public class Utils {

    /*
     * Get string representation of piece sequance
     */
    public static String getCacheKey(List<Piece> pieces, int start, int end) {
        String key = "";

        for (int i = start; i < end; i++) {
            key += pieces.get(i).getSymbol();
        }

        return key;
    }

    /*
     * Get all input permutations
     */
    public static void permuteInput(
        HashMap<Piece, Integer> freq,
        int rest,
        LinkedList<Piece> current,
        LinkedList<LinkedList<Piece>> list) {
        if (rest == 0) {
            list.add(current);
            return;
        }

        for (Piece p : freq.keySet()) {
            int v = freq.get(p);
            if (v > 0) {
                freq.put(p, v - 1);
                LinkedList<Piece> clone = new LinkedList<>(current);
                clone.add(p);
                permuteInput(freq, rest - 1, clone, list);
                freq.put(p, v);
            }
        }
    }


        public static boolean isPalyndrome (String string) {
            return string.compareTo(new StringBuilder(string).reverse().toString()) == 0;
        }
}
