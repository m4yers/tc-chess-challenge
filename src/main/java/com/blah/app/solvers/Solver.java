package com.blah.app.solvers;

import java.util.HashMap;
import java.io.IOException;

import com.blah.app.primitives.*;

public abstract class Solver {

    protected int M;
    protected int N;
    protected int P;
    protected HashMap<Piece, Integer> freq;

    protected Settings settings;
    protected int totalBoards;

    public Solver(int M, int N, HashMap<Piece, Integer> freq, Settings settings) {
        this.M = M;
        this.N = N;
        this.freq = freq;
        this.settings = settings;

        for (Piece piece : freq.keySet()) {
            this.P += freq.get(piece);
        }
    }

    public abstract void solve();

    protected void debug(String message) {
        if (this.settings.debug) {
            System.out.println(message);
        }
    }

    protected void gotBoard(Board board) {
        this.totalBoards++;

        if (this.settings.printToScreen) {
            System.out.println(board);
        }

        if (this.settings.out != null) {
            try {
                this.settings.out.write(board.toString());
            } catch (IOException e) {
                System.err.println(e.toString());
            }
        }

        if (this.settings.results != null) {
            this.settings.results.add(new Board(board));
        }
    }

    public int totalBoards() {
        return this.totalBoards;
    }
}
