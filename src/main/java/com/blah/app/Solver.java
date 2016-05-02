package com.blah.app;

import java.util.LinkedList;
import java.util.HashMap;

import java.io.Writer;
import java.io.IOException;

public abstract class Solver {

    public static class Settings {

        public boolean debug;
        public boolean printToScreen;
        public Writer out;
        public LinkedList<Board> results;


        public Settings(boolean debug, boolean printToScreen, Writer out, LinkedList<Board> results) {
            this.debug = debug;
            this.printToScreen = printToScreen;
            this.out = out;
            this.results = results;
        }
    }

    protected int M;
    protected int N;
    protected int P;
    protected HashMap<Piece, Integer> freq;

    private Settings settings;
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

    abstract void solve();

    protected void debug(String message) {
        if (this.settings.debug) {
            System.out.println(message);
        }
    }

    protected void gotBoard(Board board) {
        this.totalBoards++;

        if (this.totalBoards() % 1000000 == 0) {
            debug("-----------------------------------results: " + this.totalBoards());
        }

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
