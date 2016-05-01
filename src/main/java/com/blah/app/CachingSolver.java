package com.blah.app;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CachingSolver {

    private static class ContextPool {

        public ArrayList<Context> pool = new ArrayList<>();

        public ContextPool(int S) {
            this.pool = new ArrayList<>(S);
            for (int i = 0; i < S; i++) {
                pool.add(new Context(null, -1, -1));
            }
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

    private static class Context {
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
    }

    public static LinkedList<Board> getAllBoards( Board board, HashMap<Piece, Integer> inputList) {
        LinkedList<Board> result = new LinkedList<>();

        int total = 0;
        for (Piece piece : inputList.keySet()) {
            total += inputList.get(piece);
        }

        LinkedList<LinkedList<Piece>> inputs = new LinkedList<>();
        permuteInput(inputList, total, new LinkedList<>(), inputs);

        HashMap<String, LinkedList<Context>> cache = new HashMap<>();
        int poolSize = 1500;
        Board.Pool boardPool = new Board.Pool(poolSize, board.M(), board.N(), board.P());
        ContextPool contextPool = new ContextPool(poolSize);

        // System.out.println("Inputs: " + inputs.size());

        HashMap<String, LinkedList<Piece>> set = new HashMap<>();
        for (LinkedList<Piece> pieces : inputs) {
            String key = getCacheKey(pieces, 0, pieces.size());
            String reversed = new StringBuilder(key).reverse().toString();
            if (!set.containsKey(reversed)) {
                set.put(key, pieces);
            }
        }

        // System.out.println("Inputs: " + set.keySet().size());

        // for (LinkedList<Piece> pieces : set.values()) {
        LinkedList<Piece> prev = null;
        for (LinkedList<Piece> pieces : inputs) {

            // reclaiming the cache
            // assuming there is an order in inputs
            if (prev != null) {
                for (int i = 0; i < prev.size(); i++) {

                    if (pieces.get(i) == prev.get(i)) {
                        continue;
                    }

                    // why +1?
                    for (int j = i + 1; j < prev.size(); j++) {
                        String k = getCacheKey(prev, 0, j);
                        if (cache.containsKey(k)) {
                            int count = 0;
                            for (Context context : cache.remove(k)) {
                                boardPool.put(context.board);
                                contextPool.put(context);
                                count++;
                            }
                            System.out.println("reclaime " + count + " from " + k);
                        } else {
                            System.out.println("no reclaime ffor " + k);
                        }
                    }

                    System.out.println("pool: " + boardPool.size());

                    break;
                }
            }


            prev = pieces;

            getAllBoards(
                board,
                pieces,
                board.getFreeLocations(),
                boardPool,
                contextPool,
                cache,
                result);
        }

        return result;
    }

    public static void getAllBoards(
        Board board,
        LinkedList<Piece> inputList,
        ArrayList<Board.Location> freeList,
        Board.Pool boardPool,
        ContextPool contextPool,
        HashMap<String, LinkedList<Context>> cache,
        LinkedList<Board> result) {

        LinkedList<Context> queue = new LinkedList<>();

        String key = getCacheKey(inputList, 0, inputList.size());
        LinkedList<Context> record = null;

        // System.out.println("input " + inputList);

        for (int i = key.length() - 1; i > 0; i--) {
            String sub = key.substring(0, i);
            // System.out.println("try " + sub);
            if (cache.containsKey(sub)) {
                key = sub;
                record = queue = new LinkedList<>(cache.get(key));
                break;
            }
        }

        if (queue.size() == 0) {
            System.out.println("cache miss " + key);
            key = "";
            queue.add(contextPool.get(board, 0, 0));
            record = new LinkedList<>();
        } else {
            System.out.println("cache hit " + key);
        }

        while (queue.size() != 0) {

            Context cntx = queue.removeFirst();

            int inputIndex = cntx.inputIndex;
            int freeIndex = cntx.freeIndex;
            board = cntx.board;

            Piece piece = inputList.get(inputIndex++);

            // we moved to next generation
            if (key.length() != inputIndex)  {
                // System.out.println("save " + key + ", " + record.size() + " records");
                // System.out.println("current " + current);
                if (!cache.containsKey(key) && record != null && record.size() != 0) {
                    System.out.println("cache save " + key + " " + record.size());
                    cache.put(key, record);
                }
                String newKey = getCacheKey(inputList, 0, inputIndex);

                key = newKey;
                if (cache.containsKey(key)) {
                    record = null;
                } else {
                    record = new LinkedList<>();
                }
            }

            int freeSize = freeList.size();
            int freeLeft = freeSize - freeIndex;
            int inputSize = inputList.size();
            int inputLeft = inputSize - inputIndex;
            for (; freeIndex < freeSize; freeIndex++) {

                Board cloneBoard = boardPool.get(board);

                Board.Location loc = freeList.get(freeIndex);

                if (tryToPlace(cloneBoard, piece, loc)) {

                    if (inputIndex == inputSize) {
                        count++;
                        if (count % 1000000 == 0) {
                            System.out.println("------------------results: " + count);
                        }
                        // cloneBoard.toString(builder);
                        // boardPool.put(cloneBoard);
                        result.add(cloneBoard);
                    } else {
                        queue.addLast(contextPool.get(cloneBoard, inputIndex, freeIndex));
                        if (record != null) {
                            record.addFirst(queue.getLast());
                        }
                    }
                } else {
                    boardPool.put(cloneBoard);
                }
            }

            // if (inputIndex == inputSize)  {
            //     boardPool.put(board);
            // }
        }
    }

    public static int count = 0;
    // public static StringBuilder builder = new StringBuilder(new Board(0, 0, 0).toString().length() * 20000000);

    public static boolean tryToPlace(Board board, Piece piece, Board.Location loc) {
        int m = loc.m();
        int n = loc.n();

        for (Piece.Move move : piece.getMoves()) {
            switch (move) {
            case Perpendicular: {
                if (
                    !board.isBlocked(loc) &&
                    !board.isAnyPieceOnRow(n) &&
                    !board.isAnyPieceOnColumn(m)) {

                    board.addPiece(piece, m, n);
                    board.addPerpendicularBlock(m, n);
                    return true;
                }
                return false;
            }
            case Diagonal: {
                if (!board.isBlocked(loc) &&
                        !board.isAnyPieceOnDiagonals(m, n)) {

                    board.addPiece(piece, m, n);
                    board.addDiagonalBlock(m, n);
                    return true;
                }
                return false;
            }
            case Knight: {
                if (!board.isBlocked(loc)
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
                if (!board.isBlocked(loc)
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

    private static String getCacheKey(List<Piece> pieces, int start, int end) {
        String key = "";

        for (int i = start; i < end; i++) {
            key += pieces.get(i).getSymbol();
        }

        return key;
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

}
