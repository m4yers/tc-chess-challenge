package com.blah.app;

import java.util.LinkedList;
import java.util.HashMap;

/*
 * Thoughts on optimization:
 *  - The problem is generalization of 8Q problem thus we could use rotation and reflections
 * to produce more solutions(and failures) from a single input.
 *    - How do we drop used inputs?
 *    - how do we exit dead(used) solving branches?
 *    - Can we replicate input from the finished board?
 *  - Threads?
 */
public class Solver {

    public static LinkedList<Board> getAllBoards( Board board, HashMap<Piece, Integer> input) {
        LinkedList<Board> result = new LinkedList<>();

        int total = 0;
        for (Piece piece : input.keySet()) {
            total += input.get(piece);
        }
        LinkedList<LinkedList<Piece>> inputs = new LinkedList<>();
        permuteInput(input, total, new LinkedList<>(), inputs);
        for (LinkedList<Piece> i : inputs) {
            getAllBoards(board, i, board.getFreePositions(), result);
        }

        return result;
    }


    private static void permuteInput(
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

    public static void getAllBoards(
        Board board,
        LinkedList<Piece> input,
        LinkedList<Pair<Integer, Integer>> frees,
        LinkedList<Board> list) {

        if (input.size() == 0) {
            list.add(board);
            return;
        }

        Piece piece = input.removeFirst();
        while (frees.size() != 0) {
            Pair<Integer, Integer> pair = frees.removeFirst();
            Board clone = new Board(board);
            if (tryToPlace(clone, piece, pair.getFirst(), pair.getSecond())) {
                // TODO use queue here to break recursion like BFS
                // TODO to escape input and frees cloning, use indices
                getAllBoards(clone, new LinkedList<>(input), new LinkedList<>(frees), list);
            }
        }
    }

    public static boolean tryToPlace(Board board, Piece piece, int m, int n) {
        for (Piece.Move move : piece.getMoves()) {
            switch (move) {
            case Perpendicular: {
                if (!board.isAnyPieceOnRow(n) && !board.isAnyPieceOnColumn(m)) {
                    board.addPiece(piece, m, n);
                    board.addPerpendicularBlock(m, n);
                    return true;
                }
                return false;
            }
            case Diagonal: {
                break;
            }
            case Knight: {
                break;
            }
            case King: {
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
            }
        }
        return false;
    }
}
