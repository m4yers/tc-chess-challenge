package com.blah.app;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

public class Board {

    public static class Pool {

        public ArrayList<Board> pool;
        public int M;
        public int N;
        public int P;

        public Pool(int S, int M, int N, int P) {
            this.M = M;
            this.N = N;
            this.P = P;

            this.pool = new ArrayList<>(S);
            for (int i = 0; i < S; i++) {
                pool.add(new Board(M, N, P));
            }
        }

        public int size() {
            return pool.size();
        }

        public void put(Board board) {
            pool.add(board);
        }

        public Board get() {
            if (pool.size() != 0) {
                Board board = pool.remove(pool.size() - 1);
                board.M = this.M;
                board.N = this.N;
                board.P = this.P;
                board.size = this.M * this.N;
                board.blocked = 0;
                Arrays.fill(board.field, false);
                Arrays.fill(board.pieces, null);
                return board;
            } else {
                return new Board(M, N, P);
            }
        }

        public Board get(Board that) {
            if (pool.size() != 0) {
                Board board = pool.remove(pool.size() - 1);
                board.M = that.M;
                board.N = that.N;
                board.P = that.P;
                board.blocked = that.blocked;
                board.size = that.size;
                for (int i = 0; i < that.size; i++) {
                    board.field[i] = that.field[i];
                    board.pieces[i] = that.pieces[i];
                }
                return board;
            } else {
                return new Board(that);
            }
        }
    }

    public static class Location extends Pair<Integer, Integer> {
        private int f;

        public Location(int m, int n, int f) {
            super(m, n);
            this.f = f;
        }

        public int m() {
            return this.getFirst();
        }

        public int n() {
            return this.getSecond();
        }

        public int f() {
            return this.f;
        }
    }

    private int M; // column
    private int N; // row
    private int P;
    private int size;
    private int blocked;
    //TODO make these sparse somehow
    private Boolean[] field;
    private Piece[] pieces;

    public Board(int M, int N, int P) {
        this.M = M;
        this.N = N;
        this.P = P;
        this.size = M * N;
        this.blocked = 0;
        this.field = new Boolean[this.size];
        Arrays.fill(this.field, false);
        this.pieces = new Piece[this.size];
    }

    public Board(Board that) {
        this.M = that.M;
        this.N = that.N;
        this.size = that.size;
        this.blocked = 0;
        this.field = that.field.clone();
        this.pieces = that.pieces.clone();
    }

    public Piece[] pieces() {
        return this.pieces;
    }

    public void addBlock(int m, int n) {
        if (!withinBounds(m, n)) {
            return;
        }

        int f = toFieldIndex(m, n);
        if (!this.field[f]) {
            this.field[f] = true;
            this.blocked++;
        }
    }

    public Board getReflection() {
        Board board = new Board(this);
        rotate180(board.field, this.M, this.N);
        rotate180(board.pieces, this.M, this.N);
        return board;
    }

    private <T> void rotate180(T[] array, int m, int n) {
        for (int layer = 0; layer < n / 2; layer++) {
            int first = layer;
            int last = n - 1 - layer;
            for (int i = first; i < last; i++) {
                int offset = i - first;
                T top = array[toFieldIndex(first, i)];
                array[toFieldIndex(first, i)] = array[toFieldIndex(last, last - offset)];
                array[toFieldIndex(last, last - offset)] = top;
                T leftBottom = array[toFieldIndex(last - offset, first)];
                array[toFieldIndex(last - offset, first)] = array[toFieldIndex(i, last)];
                array[toFieldIndex(i, last)] = leftBottom;
            }
        }
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
        this.pieces[toFieldIndex(m, n)] = piece;
    }

    public boolean isBlocked(int m, int n) {
        assert withinBounds(m, n) : "WUBALUBADUBDUB";
        return this.field[toFieldIndex(m, n)];
    }

    public boolean isBlocked(Location loc) {
        return this.field[loc.f()];
    }

    public boolean isAnyPieceAt(int m, int n) {
        if (!withinBounds(m, n)) {
            return false;
        }
        return this.pieces[toFieldIndex(m, n)] != null;
    }

    public boolean isAnyPieceOnRow(int n) {
        for (int f = n * M; f < n * M + M; f++) {
            if (this.pieces[f] != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnyPieceOnColumn(int m) {
        for (int f = m; f < M * N; f += M) {
            if (this.pieces[f] != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnyPieceOnDiagonals(int m, int n) {
        return isAnyPieceOnDiagonals(m, n, M + N);
    }

    public boolean isAnyPieceOnDiagonals(int m, int n, int l) {
        while (l != 0) {
            if (this.withinBounds(m + l, n + l) &&
                    this.pieces[toFieldIndex(m + l, n + l)] != null) {
                return true;
            }

            if (this.withinBounds(m + l, n - l) &&
                    this.pieces[toFieldIndex(m + l, n - l)] != null) {
                return true;
            }

            if (this.withinBounds(m - l, n + l) &&
                    this.pieces[toFieldIndex(m - l, n + l)] != null) {
                return true;
            }

            if (this.withinBounds(m - l, n - l) &&
                    this.pieces[toFieldIndex(m - l, n - l)] != null) {
                return true;
            }

            --l;
        }

        return false;
    }

    public ArrayList<Location> getFreeLocations() {
        ArrayList<Location> result = new ArrayList<>(this.size - this.blocked);
        for (int f = 0; f < this.size; f++) {
            if (!this.field[f]) {
                result.add(toLocation(f));
            }
        }
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        toString(builder);
        return builder.toString();
    }

    public void toString(StringBuilder builder) {
        for (int n = 0; n < N; n++) {
            for (int m = 0; m < M; m++) {
                int f = toFieldIndex(m, n);
                Piece piece = this.pieces[f];
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
    }

    public int M() {
        return this.M;
    }

    public int N() {
        return this.N;
    }

    public int P() {
        return this.P;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean withinBounds(int m, int n) {
        return m >= 0 && n >= 0 && m < M && n < N;
    }

    private int toFieldIndex(int m, int n) {
        return n * M + m;
    }

    private Location toLocation(int f) {
        return new Location(f % M, f / M, f);
    }
}
