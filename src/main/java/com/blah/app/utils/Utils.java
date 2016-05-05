package com.blah.app.utils;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;

import com.blah.app.primitives.*;

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

    public static String getCacheKey(List<Piece> pieces) {
        String key = "";

        for (Piece piece : pieces) {
            key += piece.getSymbol();
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

    public static String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    public static boolean isPalyndrome (String str) {
        return str.compareTo(reverseString(str)) == 0;
    }

    public static boolean tryToPlace(Board board, Piece piece, Board.Location loc) {
        int m = loc.m();
        int n = loc.n();

        if (piece == Piece.getQueen()) {
            if (
                !board.isBlocked(loc) &&
                !board.isAnyPieceOnRow(n) &&
                !board.isAnyPieceOnColumn(m) &&
                !board.isAnyPieceOnDiagonals(m, n)) {

                board.addPiece(piece, m, n);
                board.addPerpendicularBlock(m, n);
                board.addDiagonalBlock(m, n);
                return true;
            }
        } else if (piece == Piece.getKing()) {
            if (!board.isBlocked(loc)
                    && !board.isAnyPieceAt(m, n)
                    && !board.isAnyPieceAt(m, n - 1)
                    && !board.isAnyPieceAt(m, n + 1)
                    && !board.isAnyPieceAt(m - 1, n)
                    && !board.isAnyPieceAt(m + 1, n)
                    && !board.isAnyPieceAt(m - 1, n - 1)
                    && !board.isAnyPieceAt(m + 1, n - 1)
                    && !board.isAnyPieceAt(m - 1, n + 1)
                    && !board.isAnyPieceAt(m + 1, n + 1)) {

                board.addPiece(piece, m, n);
                board.addPerpendicularBlock(m, n, 1);
                board.addDiagonalBlock(m, n, 1);
                return true;
            }
        } else if (piece == Piece.getBishop()) {
            if (!board.isBlocked(loc) &&
                    !board.isAnyPieceOnDiagonals(m, n)) {

                board.addPiece(piece, m, n);
                board.addDiagonalBlock(m, n);
                return true;
            }
        } else if (piece == Piece.getRook()) {
            if (
                !board.isBlocked(loc) &&
                !board.isAnyPieceOnRow(n) &&
                !board.isAnyPieceOnColumn(m)) {

                board.addPiece(piece, m, n);
                board.addPerpendicularBlock(m, n);
                return true;
            }
        } else if (piece == Piece.getKnight()) {
            if (!board.isBlocked(loc)
                    && !board.isAnyPieceAt(m - 2, n - 1)
                    && !board.isAnyPieceAt(m - 1, n - 2)
                    && !board.isAnyPieceAt(m + 2, n - 1)
                    && !board.isAnyPieceAt(m + 1, n - 2)
                    && !board.isAnyPieceAt(m - 2, n + 1)
                    && !board.isAnyPieceAt(m - 1, n + 2)
                    && !board.isAnyPieceAt(m + 2, n + 1)
                    && !board.isAnyPieceAt(m + 1, n + 2)
                    && !board.isAnyPieceAt(m, n)) {
                board.addPiece(piece, m, n);
                board.addBlock(m - 2, n - 1);
                board.addBlock(m - 1, n - 2);
                board.addBlock(m + 2, n - 1);
                board.addBlock(m + 1, n - 2);
                board.addBlock(m - 2, n + 1);
                board.addBlock(m - 1, n + 2);
                board.addBlock(m + 2, n + 1);
                board.addBlock(m + 1, n + 2);
                return true;
            }
        } else {
            return false;
        }
        return false;
    }
}
