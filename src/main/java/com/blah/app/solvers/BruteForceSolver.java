package com.blah.app.solvers;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;

import com.blah.app.primitives.*;
import com.blah.app.utils.*;

public class BruteForceSolver extends Solver {

    public BruteForceSolver(int M, int N, HashMap<Piece, Integer> freq, Settings settings) {
        super(M, N, freq, settings);
    }

    public void solve() {
        Board board = new Board(this.M, this.N, this.P);

        LinkedList<LinkedList<Piece>> inputs = new LinkedList<>();
        Utils.permuteInput(this.freq, this.P, new LinkedList<>(), inputs);

        for (LinkedList<Piece> i : inputs) {
            getAllBoards(board, i, board.getFreeLocations());
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
            Board.Location loc = freeList.remove(0);
            Board clone = new Board(board);
            if (Utils.tryToPlace(clone, piece, loc)) {
                getAllBoards(clone, new LinkedList<>(inputList), new ArrayList<>(freeList));
            }
        }
    }
}
