package com.blah.app;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;

public class BruteForceSolver {

    public static LinkedList<Board> getAllBoards( Board board, HashMap<Piece, Integer> inputList) {
        LinkedList<Board> result = new LinkedList<>();

        int total = 0;
        for (Piece piece : inputList.keySet()) {
            total += inputList.get(piece);
        }

        LinkedList<LinkedList<Piece>> inputs = new LinkedList<>();
        permuteInput(inputList, total, new LinkedList<>(), inputs);

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

        // Permute: we can drop reflected inputs and substitute them with reflected boards

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
        LinkedList<Piece> inputList,
        ArrayList<Board.Location> freeList,
        LinkedList<Board> list) {

        if (inputList.size() == 0) {
            list.add(board);
            return;
        }

        Piece piece = inputList.removeFirst();
        while (freeList.size() != 0) {
            // FIXME the free list is changed every time we add a thing, need to request it again
            // actually it is ok, since requesting new list is O(MxN) and scipping used is ~O(M)
            Board.Location loc = freeList.remove(0);
            Board clone = new Board(board);
            if (tryToPlace(clone, piece, loc.m(), loc.n())) {
                // TODO use queue here to break recursion like BFS
                // TODO to escape inputList and freeList cloning, use indices
                getAllBoards(clone, new LinkedList<>(inputList), new ArrayList<>(freeList), list);
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
                if (!board.isBlocked(m, n) && !board.isAnyPieceOnDiagonals(m, n)) {
                    board.addPiece(piece, m, n);
                    board.addDiagonalBlock(m, n);
                    return true;
                }
                return false;
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
