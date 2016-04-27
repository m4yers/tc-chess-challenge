package com.blah.app;

import java.util.LinkedList;

public class Board {

    private int M;
    private int N;
    private boolean[] field;
    private LinkedList<Piece> pieces;

    public Board(int M, int N) {
        this.M = M;
        this.N = N;
        this.field = new boolean[M * N];
    }
}
