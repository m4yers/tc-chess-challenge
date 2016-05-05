package com.blah.app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import java.util.Collections;
import java.util.Arrays;

import java.lang.reflect.Constructor;

import org.junit.Test;
import org.junit.Before;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.blah.app.primitives.*;
import com.blah.app.solvers.*;
import com.blah.app.utils.*;

@RunWith(Parameterized.class)
@SuppressWarnings({"serial", "unchecked"})
public class TestSolversValidity {

    @Parameterized.Parameters
    public static Iterable<TestData> cases() {
        LinkedList<TestData> result = new LinkedList<>();
        generateTestData(4, 4, 2, 1, 1, 2, 1, result); // 2332 test inputs
        // generateTestData(3, 3, 2, 1, 1, 2, 1, result); // 2332 test inputs
        // System.out.println(result.size());
        return result;
    }

    private static void generateTestData(int M, int N, int kings, int queens, int bishops, int rooks, int knights, LinkedList<TestData> result) {
        if (kings == 0 && queens == 0 && bishops == 0 && rooks == 0 && knights == 0) {
            return;
        }

        result.add(new TestData(M, N, kings, queens, bishops, rooks, knights));

        if (kings > 0) {
            generateTestData(M, N, kings - 1, queens, bishops, rooks, knights, result);
        }
        if (queens > 0) {
            generateTestData(M, N, kings, queens - 1, bishops, rooks, knights, result);
        }
        if (bishops > 0) {
            generateTestData(M, N, kings, queens, bishops - 1, rooks, knights, result);
        }
        if (rooks > 0) {
            generateTestData(M, N, kings, queens, bishops, rooks - 1, knights, result);
        }
        if (knights > 0) {
            generateTestData(M, N, kings, queens, bishops, rooks, knights - 1, result);
        }
    }

    private static Object[] solvers = { BruteForceSolver.class, CachingSolver.class, ThreadingSolver.class };
    private TestData data;

    public TestSolversValidity(TestData data) {
        this.data = data;
    }

    @Test
    public void testValidity() {
        HashMap<Piece, Integer> freq = new HashMap<>();
        freq.put(Piece.getKing(),   this.data.kings);
        freq.put(Piece.getQueen(),  this.data.queens);
        freq.put(Piece.getBishop(), this.data.bishops);
        freq.put(Piece.getRook(),   this.data.rooks);
        freq.put(Piece.getKnight(), this.data.knights);

        List<LinkedList<Board>> results = new LinkedList<>();

        for (Object solverClass : solvers) {
            Solver solver = null;

            LinkedList<Board> result = new LinkedList<>();
            Settings settings = new Settings.Builder()
            .debug(false)
            .printToScreen(false)
            .printToFile(null)
            .poolSize(500)
            .result(result)
            .build();

            Constructor<?>[] ctors = ((Class<? extends Solver>)solverClass).getDeclaredConstructors();
            for (Constructor<?> ctor : ctors) {
                if (ctor.getGenericParameterTypes().length == 4) {
                    try {
                        solver = (Solver)ctor.newInstance(data.M, data.N, freq, settings);
                    } catch (Exception e) {
                    }
                    break;
                }
            }

            assertNotNull(solver);

            solver.solve();

            /*
             * Verifying that solver produce unique list of boards
             */
            HashSet<String> hash = new HashSet<>();
            for (Board board : result) {
                assertFalse(hash.contains(board.toString()));
            }

            Collections.sort(result, (a, b) -> a.hashCode() < b.hashCode() ? -1 : 1);
            results.add(result);
        }

        /*
         * Making sure ALL solvers produce the same result
         */

        // camparing sizes of output
        if (results.stream().map(r -> r.size()).distinct().count() != 1) {
            System.out.println();
            System.out.println(this.data);
            for (int i = 0; i < solvers.length; i++) {
                System.out.println(solvers[i] + ": " + results.get(i).size());
                System.out.println();
                for (Board board : results.get(i)) {
                    System.out.println(board);
                }
            }

            fail();
        }
    }

    private static class TestData {

        private int M;
        private int N;
        private int kings;
        private int queens;
        private int bishops;
        private int rooks;
        private int knights;

        public TestData(int M, int N, int kings, int queens, int bishops, int rooks, int knights) {
            this.M = M;
            this.N = N;
            this.kings = kings;
            this.queens = queens;
            this.bishops = bishops;
            this.rooks = rooks;
            this.knights = knights;
        }

        public String toString() {
            return String.format(
                       "TestData M %d N %d kings %d queens %d bishops %d rooks %d knights %d",
                       this.M,
                       this.N,
                       this.kings,
                       this.queens,
                       this.bishops,
                       this.rooks,
                       this.knights);
        }
    }
}
