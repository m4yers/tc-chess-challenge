package com.blah.app;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.TreeMap;

public class Board {

    private int M; // column
    private int N; // row
    private int size;
    private int blocked;
    private boolean[] field;
    private LinkedList<Piece> pieces;
    private TreeMap<Integer, Piece> map;

    public Board(int M, int N) {
        this.M = M;
        this.N = N;
        this.size = M * N;
        this.field = new boolean[this.size];
        this.pieces = new LinkedList<Piece>();
        this.map = new TreeMap<Integer, Piece>();
    }

    public Board(Board that) {
        this.M = that.M;
        this.N = that.N;
        this.size = that.size;
        this.blocked = 0;
        this.field = that.field.clone();
        this.pieces = new LinkedList<Piece>(that.pieces);
        this.map = new TreeMap<Integer, Piece>(that.map);
    }

    public boolean withinBounds(int m, int n) {
        return m >= 0 && n >= 0 && m < M && n < N;
    }

    public void addBlock(int m, int n) {
        assert withinBounds(m, n) : "WUBALUBADUBDUB";
        int f = toFieldIndex(m, n);
        this.field[f] = true;
        // TODO use it to create AL instead of LL
        this.blocked++;
    }

    public void addPerpendicularBlock(int m, int n) {
    }

    public void addDiagonalBlock(int m, int n) {
    }

    public void addPiece(Piece piece, int m, int n) {
        assert withinBounds(m, n) : "WUBALUBADUBDUB";
        this.addBlock(m, n);
        this.pieces.add(piece);
        this.map.put(toFieldIndex(m, n), piece);
    }

    public boolean isAnyPieceAt(int m, int n) {
        for (int f : this.map.keySet()) {
            Pair<Integer, Integer> pair = toPair(f);
            if (pair.getFirst() == m && pair.getSecond() == n) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnyPieceOnRow(int n) {
        for (int f : this.map.keySet()) {
            if (toPair(f).getSecond() == n) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnyPieceOnColumn(int m) {
        for (int f : this.map.keySet()) {
            if (toPair(f).getFirst() == m) {
                return true;
            }
        }
        return false;
    }

    // public Iterable<Boolean> getRow(int n) {
    //     ArrayList<Boolean> result = new ArrayList<>(this.N);
    //     for (int f = n * N; f < n * N + M; f++) {
    //         result.add(this.field[f]);
    //     }
    //     return result;
    // }
    //
    // public Iterable<Boolean> getColumn(int m) {
    //     ArrayList<Boolean> result = new ArrayList<>(this.M);
    //     for (int f = m; f < N * M; f+=N) {
    //         result.add(this.field[f]);
    //     }
    //     return result;
    // }
    //
    // public Iterable<Boolean> getDiagonals(int d) {
    //     ArrayList<Boolean> result = new ArrayList<>(this.M + this.N); // TODO calculate
    //     return result;
    // }

    public LinkedList<Pair<Integer, Integer>> getFreePositions() {
        LinkedList<Pair<Integer, Integer>> result = new LinkedList<>();
        for (int f = 0; f < this.size; f++) {
            if (!this.field[f]) {
                result.add(toPair(f));
            }
        }
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int n = 0; n < N; n++) {
            for (int m = 0; m < M; m++) {
                int f = toFieldIndex(m, n);
                Piece piece = this.map.get(f);
                if (piece != null) {
                    builder.append(piece.getSymbol());
                } else if (this.field[f]) {
                    builder.append("x");
                } else {
                    builder.append(".");
                }
                builder.append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private int toFieldIndex(int m, int n) {
        return n * N + m;
    }

    private Pair<Integer, Integer> toPair(int f) {
        return new Pair<Integer, Integer>(f % M, f / N);
    }
}
