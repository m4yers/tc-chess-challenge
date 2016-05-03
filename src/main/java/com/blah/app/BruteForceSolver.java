package com.blah.app;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;

public class BruteForceSolver extends Solver {

    public BruteForceSolver(int M, int N, HashMap<Piece, Integer> freq, Settings settings) {
        super(M, N, freq, settings);
    }

    public void solve() {
        Board board = new Board(this.M, this.N, this.P);

        LinkedList<LinkedList<Piece>> inputs = new LinkedList<>();
        permuteInput(this.freq, this.P, new LinkedList<>(), inputs);

        for (LinkedList<Piece> i : inputs) {
            getAllBoards(board, i, board.getFreeLocations());
        }
    }


    private void permuteInput(
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

    public void getAllBoards(
        Board board,
        LinkedList<Piece> inputList,
        ArrayList<Board.Location> freeList) {

        if (inputList.size() == 0) {
            gotBoard(board);
            return;
        }

        Piece piece = inputList.removeFirst();
        while (freeList.size() != 0) {
            // FIXME the free list is changed every time we add a thing, need to request it again
            // actually it is ok, since requesting new list is O(MxN) and scipping used is ~O(M)
            Board.Location loc = freeList.remove(0);
            Board clone = new Board(board);
            if (Utils.tryToPlace(clone, piece, loc)) {
                // TODO use queue here to break recursion like BFS
                // TODO to escape inputList and freeList cloning, use indices
                getAllBoards(clone, new LinkedList<>(inputList), new ArrayList<>(freeList));
            }
        }
    }
}
