package com.blah.app;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.TreeMap;

public class Board {

    public static class Location extends Pair<Integer, Integer> {
        public Location(int m, int n) {
            super(m, n);
        }

        public int m() {
            return this.getFirst();
        }

        public int n() {
            return this.getSecond();
        }
    }

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
        if (!withinBounds(m, n)) {
            return;
        }

        int f = toFieldIndex(m, n);
        this.field[f] = true;
        // TODO use it to create AL instead of LL
        this.blocked++;
    }

    public void addPerpendicularBlock(int m, int n) {
        this.addPerpendicularBlock(m, n, Math.max(M, N));
    }

    public void addPerpendicularBlock(int m, int n, int l) {
        while (l != 0) {
            if (this.withinBounds(m + l, n)) {
                this.addBlock(m + l, n);
            }
            if (this.withinBounds(m - l, n)) {
                this.addBlock(m - l, n);
            }
            if (this.withinBounds(m, n + l)) {
                this.addBlock(m, n + l);
            }
            if (this.withinBounds(m, n - l)) {
                this.addBlock(m, n - l);
            }

            --l;
        }
    }

    public void addDiagonalBlock(int m, int n) {
        this.addDiagonalBlock(m, n, M + N);
    }

    public void addDiagonalBlock(int m, int n, int l) {
        for (int e = 1; true && l > 0; e++, l--) {
            boolean exit = true;
            if (this.withinBounds(m + e, n + e)) {
                this.addBlock(m + e, n + e);
                exit = false;
            }

            if (this.withinBounds(m + e, n - e)) {
                this.addBlock(m + e, n - e);
                exit = false;
            }

            if (this.withinBounds(m - e, n + e)) {
                this.addBlock(m - e, n + e);
                exit = false;
            }

            if (this.withinBounds(m - e, n - e)) {
                this.addBlock(m - e, n - e);
                exit = false;
            }

            if (exit) {
                break;
            }
        }
    }

    public void addPiece(Piece piece, int m, int n) {
        assert withinBounds(m, n) : "WUBALUBADUBDUB";
        this.addBlock(m, n);
        this.pieces.add(piece);
        this.map.put(toFieldIndex(m, n), piece);
    }

    public boolean isBlocked(int m, int n) {
        assert withinBounds(m, n) : "WUBALUBADUBDUB";
        return this.field[toFieldIndex(m, n)];
    }

    public boolean isAnyPieceAt(int m, int n) {
        for (int f : this.map.keySet()) {
            Location loc = toLocation(f);
            if (loc.m() == m && loc.n() == n) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnyPieceOnRow(int n) {
        for (int f : this.map.keySet()) {
            if (toLocation(f).n() == n) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnyPieceOnColumn(int m) {
        for (int f : this.map.keySet()) {
            if (toLocation(f).m() == m) {
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

    public LinkedList<Location> getFreeLocations() {
        LinkedList<Location> result = new LinkedList<>();
        for (int f = 0; f < this.size; f++) {
            if (!this.field[f]) {
                result.add(toLocation(f));
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

    private Location toLocation(int f) {
        return new Location(f % M, f / N);
    }
}
