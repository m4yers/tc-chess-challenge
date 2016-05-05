package com.blah.app.primitives;

import java.util.ArrayList;

/*
 * Context stores board and current input and free list indices, used with main cycle queue
 */
public class Context {
    public Board board;
    public int inputIndex;
    public int freeIndex;

    public Context(
        Board board,
        int inputIndex,
        int freeIndex) {

        this.board = board;
        this.inputIndex = inputIndex;
        this.freeIndex = freeIndex;
    }

    public Context(Context that) {
        this.board = new Board(that.board);
        this.inputIndex = that.inputIndex;
        this.freeIndex = that.freeIndex;
    }

    public static class Pool {

        public ArrayList<Context> pool = new ArrayList<>();

        public Pool(int S) {
            this.pool = new ArrayList<>(S);
            for (int i = 0; i < S; i++) {
                pool.add(new Context(null, -1, -1));
            }
        }

        public int size() {
            return pool.size();
        }

        public void put(Context context) {
            pool.add(context);
        }

        public Context get(Board board, int inputIndex, int freeIndex) {
            if (pool.size() == 0)  {
                return new Context(board, inputIndex, freeIndex);
            } else {
                Context context = pool.remove(pool.size() - 1);
                context.board = board;
                context.inputIndex = inputIndex;
                context.freeIndex = freeIndex;
                return context;
            }
        }
    }
}
