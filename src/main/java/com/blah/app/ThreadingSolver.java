package com.blah.app;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;

// TODO add threaded bruteforce, it seems bf works better than caching on hi-end machines
public class ThreadingSolver extends Solver {

    private int poolSize;
    private boolean useRotation;

    /*
     * @param M Columns
     * @param N Rows
     * @param freq HashMap of pieces and theirs numbers
     * @param settings Solver settings
     * @param poolSize Size of board pool
     */
    public ThreadingSolver(int M, int N, HashMap<Piece, Integer> freq, Settings settings, int poolSize) {
        super(M, N, freq, settings);
        this.poolSize = poolSize;
        this.useRotation = M == N;
        // this.useRotation = false;
    }

    public ThreadingSolver(int M, int N, HashMap<Piece, Integer> freq, Settings settings) {
        this(M, N, freq, settings, 1000);
    }

    public void solve() {

        /*
         * Here we precompute all possible inputs into the solver, it is simple permutation
         * computation allows us break from recursion in the main cycle.
         */
        LinkedList<LinkedList<Piece>> inputs = new LinkedList<>();
        Utils.permuteInput(this.freq, this.P, new LinkedList<>(), inputs);


        /*
         * As with classic 8Q problem there are a number of 'core' solutions, others are just
         * reflections and rotations. The very simple optimization here is just to drop all
         * reflection inputs and add 180 rotated boards as result making sure that input sequance
         * is not palindrome. Other rotations and reflections are bit more complicated to check
         * for uniqueness. Plus 90 and 270 rotations won't work for non-square boards.
         */
        // this.useRotation = false;
        if (this.useRotation)  {

            System.out.println("Before: " + inputs.size());

            HashMap<String, LinkedList<Piece>> set = new HashMap<>();

            for (LinkedList<Piece> pieces : inputs) {
                String key = Utils.getCacheKey(pieces, 0, pieces.size());
                String reversed = Utils.reverseString(key);
                if (!set.containsKey(key) && !set.containsKey(reversed)) {
                    set.put(key, pieces);
                }
            }

            inputs = new LinkedList<>(set.values());

            System.out.println("After: " + inputs.size());

            // return;
        }

        //TODO add daemon to print it if needed
        ConcurrentLinkedQueue<Board> results = null;
        if (this.settings.results != null)  {
            results = new ConcurrentLinkedQueue<>();
        }

        List<Callable<Integer>> callables = new LinkedList<>();

        /*
         * We split input based on the starting piece
         */
        HashMap<Piece, LinkedList<LinkedList<Piece>>> lists = new HashMap<>();
        lists.put(Piece.getKing(), new LinkedList<>());
        lists.put(Piece.getQueen(), new LinkedList<>());
        lists.put(Piece.getBishop(), new LinkedList<>());
        lists.put(Piece.getRook(), new LinkedList<>());
        lists.put(Piece.getKnight(), new LinkedList<>());

        for (LinkedList<Piece> current : inputs) {
            lists.get(current.get(0)).add(current);
        }

        for (LinkedList<LinkedList<Piece>> list : lists.values()) {
            if (list.size() == 0) {
                continue;
            }
            // making sure there is an order in input
            Collections.sort(list, (a, b) -> Utils.getCacheKey(a, 0, a.size()).compareTo(Utils.getCacheKey(b, 0, b.size())));
            callables.add(new Runner(this.M, this.N, this.P, this.poolSize, list, this.useRotation, results, this.settings.debug));
        }

        ExecutorService exe = Executors.newWorkStealingPool();

        try {
            exe.invokeAll(callables)
            .stream()
            .map(future -> {
                try {
                    return future.get();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            })
            .forEach(value -> { this.totalBoards += value; });
        } catch (InterruptedException e) {
            System.err.println(e);
            System.err.println("WUBALUBADUBDUB");
        }

        exe.shutdown();
        if (settings.results != null) {
            settings.results.addAll(results);
        }
    }

    private class Runner implements Callable<Integer> {

        private Board.Pool boardPool;
        private ContextPool contextPool;
        private HashMap<String, LinkedList<Context>> cache;
        private Iterable<LinkedList<Piece>> pieces;
        private ConcurrentLinkedQueue<Board> results;
        private boolean useRotation;
        private boolean debug;
        private int counter;

        public Runner(int M, int N, int P, int poolSize, Iterable<LinkedList<Piece>> pieces, boolean useRotation, ConcurrentLinkedQueue<Board> results, boolean debug) {

            this.pieces = pieces;
            this.results = results;
            this.debug = debug;
            this.boardPool = new Board.Pool(poolSize, M, N, P);
            this.contextPool = new ContextPool(poolSize);
            this.cache = new HashMap<>();
            this.useRotation = useRotation;
        }

        @Override
        public Integer call() throws Exception {
            LinkedList<Piece> prev = null;
            for (LinkedList<Piece> pieces : this.pieces) {

                /*
                 * Here we reclaiming the cache entries that won't be used again simply by checking
                 * piece sequance starting from the beginning, and if they stop matching we remove
                 * those entries and put boards and contexts back to pool. For certain inputs this is
                 * critical, because a input sequance can store in cache literally millions of
                 * almost complete boards
                 */
                if (prev != null) {
                    debug("pool.board   " + boardPool.size());
                    debug("pool.context " + contextPool.size());
                    for (int i = 0; i < prev.size(); i++) {

                        if (pieces.get(i) == prev.get(i)) {
                            continue;
                        }

                        // why +1 because getCacheKey second argument is 'till'
                        for (int j = i + 1; j < prev.size(); j++) {
                            String k = Utils.getCacheKey(prev, 0, j);
                            if (cache.containsKey(k)) {
                                int count = 0;
                                for (Context context : cache.remove(k)) {
                                    boardPool.put(context.board);
                                    contextPool.put(context);
                                    count++;
                                }
                                debug("cache.reclaime " + count + " from " + k);
                            }
                        }

                        break;
                    }

                    debug("pool.board   " + boardPool.size());
                    debug("pool.context " + contextPool.size());
                }


                /*
                 * Saving previous input to compare it with a new one on the next iteration
                 */
                prev = pieces;

                Board board = boardPool.get();

                getAllBoards( board, pieces);
            }

            return this.counter;
        }

        /*
         * Main solver cycle
         *
         * @param board Start board
         * @param inputList Piece sequance to be placed on the board
         */
        public void getAllBoards( Board board, LinkedList<Piece> inputList) {

        boolean rotateThisInput = this.useRotation && !Utils.isPalyndrome(Utils.getCacheKey(inputList));

            /*
             * This is sequance of all board positions starting from left to right, top to bottom.
             * When we place a piece on the board we advance freeList index to the next available
             * position
             */
            ArrayList<Board.Location> freeList = board.getFreeLocations();

            /*
             * Queue i use instead of recursion
             */
            LinkedList<Context> queue = new LinkedList<>();

            String key = Utils.getCacheKey(inputList, 0, inputList.size());

            /*
             * Cache record we gonna to fill for the current sub-key
             */
            LinkedList<Context> record = null;

            /*
             * Checking cache for already existing record
             */
            for (int i = key.length() - 1; i > 0; i--) {
                String sub = key.substring(0, i);
                if (cache.containsKey(sub)) {
                    key = sub;
                    record = queue = new LinkedList<>(cache.get(key));
                    break;
                }
            }

            /*
             * If no record this probably a first run an input sequance
             */
            if (queue.size() == 0) {
                debug("cache.miss " + key);
                key = "";
                queue.add(contextPool.get(board, 0, 0));
                record = new LinkedList<>();
            } else {
                debug("cache.hit " + key);
            }

            while (queue.size() != 0) {

                Context cntx = queue.removeFirst();

                int inputIndex = cntx.inputIndex;
                int freeIndex = cntx.freeIndex;
                board = cntx.board;

                /*
                 * Read next piece and advance input index, as with free-list we do not modify nor
                 * clone lists, we just store index
                 */
                Piece piece = inputList.get(inputIndex++);

                /*
                 * We moved to the next generation of boards and thus we will try to save current cache
                 * record
                 */
                if (key.length() != inputIndex)  {
                    if (!cache.containsKey(key) && record != null && record.size() != 0) {
                        debug("cache.save " + key + " " + record.size());
                        cache.put(key, record);
                    }

                    key = Utils.getCacheKey(inputList, 0, inputIndex);
                    /*
                     * If there is a cache record with this key we do not fill-in new cache record
                     * FIXME bug?
                     */
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
                /*
                 * We try to place a piece for every free board position thus creating new generation
                 * of boards. Potentially we could check for free-locs-left >= input-left but with
                 * this check cache wasn't performant enough, i could not find the reasong so i just
                 * dropped the check.
                 */
                for (; freeIndex < freeSize; freeIndex++) {

                    Board cloneBoard = boardPool.get(board);

                    Board.Location loc = freeList.get(freeIndex);

                    /*
                     * Trying to place a piece in the board, if we are successful the cloned board
                     * goes to the next generation, otherwise we place it back into the pool
                     */
                    if (Utils.tryToPlace(cloneBoard, piece, loc)) {

                        /*
                         * We have placed all the pieces
                         */
                        if (inputIndex == inputSize) {
                            /*
                             * If current sequance is not palindrome we can get board rotation
                             */
                            if (rotateThisInput) {
                                Board rotation = boardPool.get(cloneBoard);
                                rotation.rotate180();
                                gotBoard(rotation);
                                boardPool.put(rotation);
                            }
                            gotBoard(cloneBoard);
                            boardPool.put(cloneBoard);
                        } else {
                            /*
                             * Move to the next generation
                             */
                            queue.addLast(contextPool.get(cloneBoard, inputIndex, freeIndex));
                            /*
                             * If we are filling in a cache record store current context
                             */
                            if (record != null) {
                                record.addFirst(queue.getLast());
                            }
                        }
                    } else {
                        boardPool.put(cloneBoard);
                    }
                }
            }
        }

        private void debug(String message) {
            if (this.debug) {
                System.out.println(Thread.currentThread().getName() + ": " + message);
            }
        }

        private HashSet<String> hash = new HashSet<>();

        private void gotBoard(Board board) {
            if (this.hash.contains(board.toString())) {
                return;
            }

            this.hash.add(board.toString());

            this.counter++;
            if (this.results != null) {
                this.results.add(new Board(board));
            }
            // System.out.println(board);
            // if (this.counter % 1000000 == 0) {
            //     debug("--------------------------------------------------------" + this.counter);
            // }
        }
    }

    /*
     * Context stores board and current input and free list indices, used with main cycle queue
     */
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

    private static class ContextPool {

        public ArrayList<Context> pool = new ArrayList<>();

        public ContextPool(int S) {
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
