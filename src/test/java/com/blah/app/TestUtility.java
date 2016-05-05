package com.blah.app;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.Collections;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import com.blah.app.primitives.*;
import com.blah.app.solvers.*;
import com.blah.app.utils.*;

//TODO extend with edge test cases
public class TestUtility {

    @Test
    public void testGetCacheKey() {
        List<Piece> list = new LinkedList<>();
        list.add(Piece.getKing());
        list.add(Piece.getKing());
        list.add(Piece.getQueen());
        list.add(Piece.getBishop());

        assertEquals("KKQB", Utils.getCacheKey(list));
    }

    @Test
    public void testPermuteInput() {

        String[] check = {
            "KKQB", "KKBQ", "KQKB", "KQBK", "KBKQ", "KBQK",
            "QKKB", "QKBK", "QBKK", "BKKQ", "BKQK", "BQKK"
        };

        HashMap<Piece, Integer> freq = new HashMap<>();
        freq.put(Piece.getKing(), 2);
        freq.put(Piece.getQueen(), 1);
        freq.put(Piece.getBishop(), 1);

        LinkedList<LinkedList<Piece>> result = new LinkedList<>();

        int length = 4;

        Utils.permuteInput(freq, length, new LinkedList<Piece>(), result);

        assertEquals(check.length, result.size());

        Arrays.sort(check, (a, b) -> a.compareTo(b));
        Collections.sort(result, (a, b) -> Utils.getCacheKey(a).compareTo(Utils.getCacheKey(b)));

        for (int i = 0; i < check.length; i++) {
            assertEquals(check[i], Utils.getCacheKey(result.get(i)));
        }
    }

    @Test
    public void testReverseString() {
        assertEquals("WUBALUBADUBDUB", Utils.reverseString("BUDBUDABULABUW"));
    }

    @Test
    public void testIsPayndrome() {
        assertTrue(Utils.isPalyndrome("AABBAA"));
        assertFalse(Utils.isPalyndrome("AABBA"));
    }
}
