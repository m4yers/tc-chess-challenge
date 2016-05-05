package com.blah.app.solvers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.blah.app.primitives.*;
import com.blah.app.utils.*;

public class CachingSolver extends Solver {

    private Board.Pool boardPool;
    private Context.Pool contextPool;
    private HashMap<String, LinkedList<Context>> cache;
    private boolean doRotation;

    /*
     * @param M Columns
     * @param N Rows
     * @param freq HashMap of pieces and theirs numbers
     * @param settings Solver settings
     * @param poolSize Size of board pool
     */
    public CachingSolver(int M, int N, HashMap<Piece, Integer> freq, Settings settings, int poolSize) {
        super(M, N, freq, settings);

        this.boardPool = new Board.Pool(poolSize, this.M, this.N, this.P);
        this.contextPool = new Context.Pool(poolSize);
        this.doRotation = M == N;

        /*
         * This cache stores half-solved boards, assuming all input comes sorted the first sequance
         * caches all half(or almost) solved boards along the way, then next sequance comes and it
         * does not have to recalculate all the boards again it can start from next to last sequance
         * element state, basically this is memoization, e.g:
         * 1: KKRRNN sequance stores all almost solved boards sequance for K, KK, KK, KKR, KKRR,
         *    KKRRN.
         * 2: KKRRNQ comes next and it does not have to recalculate K, KK etc again it will start
         *    from KKRRN.
         *
         *  Why? because for certain program parameters it gets up to half a Million boards at the
         *  end stage so having it precalculated is kinda handy.
         *
         */
        this.cache = new HashMap<>();
    }

    public CachingSolver(int M, int N, HashMap<Piece, Integer> freq, Settings settings) {
        this(M, N, freq, settings, 1000);
    }

    public void solve() {

        /*
         * Here we precompute all possible inputs into the solver, it is simple permutation
         * computation allows us break from recursion in the main cycle.
         */
        LinkedList<LinkedList<Piece>> inputs = new LinkedList<>();
        Utils.permuteInput(this.freq, this.P, new LinkedList<>(), inputs);

        if (this.doRotation) {
            /*
             * As with classic 8Q problem there are a number of 'core' solutions, others are just
             * reflections and rotations. The very simple optimization here is just to drop all
             * reflection inputs and add 180 rotated boards as result making sure that input sequance
             * is not palindrome. Other rotations and reflections are bit more complicated to check
             * for uniqueness. Plus 90 and 270 rotations won't work for non-square boards.
             */
            HashMap<String, LinkedList<Piece>> set = new HashMap<>();
            for (LinkedList<Piece> pieces : inputs) {
                String key = Utils.getCacheKey(pieces, 0, pieces.size());
                String reversed = Utils.reverseString(key);
                if (!set.containsKey(reversed)) {
                    set.put(key, pieces);
                }
            }

            inputs = new LinkedList<>(set.values());
        }

        // making sure there is an order in inputs list
        Collections.sort(inputs, (a, b) -> Utils.getCacheKey(a, 0, this.P).compareTo(Utils.getCacheKey(b, 0, this.P)));

        LinkedList<Piece> prev = null;
        for (LinkedList<Piece> pieces : inputs) {

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

            // TODO i can easily split inputs between N threads, where N is number of unique pieces
            getAllBoards( board, pieces);
        }
    }

    /*
     * Main solver cycle
     *
     * @param board Start board
     * @param inputList Piece sequance to be placed on the board
     */
    public void getAllBoards( Board board, LinkedList<Piece> inputList) {

        boolean rotateThisInput = this.doRotation && !Utils.isPalyndrome(Utils.getCacheKey(inputList));

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

}
