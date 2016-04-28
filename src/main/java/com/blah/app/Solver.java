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

    // TODO use it to break recursion
    private static class Context {
        public Board board;
        public LinkedList<Piece> input;
        public int inputIndex;
        public LinkedList<Board.Location> frees;
        public int freesIndex;

        public Context(
            Board board,
            LinkedList<Piece> input,
            int inputIndex,
            LinkedList<Board.Location> frees,
            int freesIndex) {

            this.board = board;
            this.input = input;
            this.inputIndex = inputIndex;
            this.frees = frees;
            this.freesIndex = freesIndex;
        }

        public Context(Context that) {
            this.board = new Board(that.board);
            this.input = new LinkedList<>(input);
            this.inputIndex = inputIndex;
            this.frees = new LinkedList<>(frees);
            this.freesIndex = freesIndex;
        }
    }

    public static LinkedList<Board> getAllBoards( Board board, HashMap<Piece, Integer> input) {
        LinkedList<Board> result = new LinkedList<>();

        int total = 0;
        for (Piece piece : input.keySet()) {
            total += input.get(piece);
        }
        LinkedList<LinkedList<Piece>> inputs = new LinkedList<>();
        permuteInput(input, total, new LinkedList<>(), inputs);
        for (LinkedList<Piece> i : inputs) {
            getAllBoards(board, i, board.getFreeLocations(), result);
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
        LinkedList<Board.Location> frees,
        LinkedList<Board> list) {

        if (input.size() == 0) {
            list.add(board);
            return;
        }

        Piece piece = input.removeFirst();
        while (frees.size() != 0) {
            // FIXME the free list is changed every time we add a thing, need to request it again
            Board.Location loc = frees.removeFirst();
            Board clone = new Board(board);
            if (tryToPlace(clone, piece, loc.m(), loc.n())) {
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
                if (!board.isBlocked(m, n) &&
                        !board.isAnyPieceOnRow(n) && !board.isAnyPieceOnColumn(m)) {
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
                if (!board.isBlocked(m, n)
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
                return false;
            }
            case King: {
                if (!board.isBlocked(m, n)
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
                return false;
            }
            default: {
                throw new IllegalArgumentException();
            }
            }
        }
        return false;
    }
}
