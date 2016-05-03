package com.blah.app;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.Before;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

@RunWith(Parameterized.class)
@SuppressWarnings("serial")
public class TestSolvers {

    @Parameterized.Parameters
    public static Iterable<SolverChessTestData> cases() {
        LinkedList<SolverChessTestData> result = new LinkedList<>();
        // result.add(new SolverChessTestData(BruteForceSolver.class, "/solver-test-case-king.txt"));
        // result.add(new SolverChessTestData(BruteForceSolver.class, "/solver-test-case-queen.txt"));
        // result.add(new SolverChessTestData(BruteForceSolver.class, "/solver-test-case-bishop.txt"));
        // result.add(new SolverChessTestData(BruteForceSolver.class, "/solver-test-case-rook.txt"));
        // result.add(new SolverChessTestData(BruteForceSolver.class, "/solver-test-case-knight.txt"));
        // result.add(new SolverChessTestData(BruteForceSolver.class, "/solver-test-case-1.txt"));
        // result.add(new SolverChessTestData(BruteForceSolver.class, "/solver-test-case-2.txt"));
        // result.add(new SolverChessTestData(BruteForceSolver.class, "/solver-test-case-8-queens.txt"));
        //
        // result.add(new SolverChessTestData(CachingSolver.class, "/solver-test-case-king.txt"));
        // result.add(new SolverChessTestData(CachingSolver.class, "/solver-test-case-queen.txt"));
        // result.add(new SolverChessTestData(CachingSolver.class, "/solver-test-case-bishop.txt"));
        // result.add(new SolverChessTestData(CachingSolver.class, "/solver-test-case-rook.txt"));
        // result.add(new SolverChessTestData(CachingSolver.class, "/solver-test-case-knight.txt"));
        // result.add(new SolverChessTestData(CachingSolver.class, "/solver-test-case-1.txt"));
        // result.add(new SolverChessTestData(CachingSolver.class, "/solver-test-case-2.txt"));
        // result.add(new SolverChessTestData(CachingSolver.class, "/solver-test-case-8-queens.txt"));
        //
        //
        // result.add(new SolverChessTestData(ThreadingSolver.class, "/solver-test-case-king.txt"));
        // result.add(new SolverChessTestData(ThreadingSolver.class, "/solver-test-case-queen.txt"));
        // result.add(new SolverChessTestData(ThreadingSolver.class, "/solver-test-case-bishop.txt"));
        // result.add(new SolverChessTestData(ThreadingSolver.class, "/solver-test-case-rook.txt"));
        // result.add(new SolverChessTestData(ThreadingSolver.class, "/solver-test-case-knight.txt"));
        // result.add(new SolverChessTestData(ThreadingSolver.class, "/solver-test-case-1.txt"));
        // result.add(new SolverChessTestData(ThreadingSolver.class, "/solver-test-case-2.txt"));
        // result.add(new SolverChessTestData(ThreadingSolver.class, "/solver-test-case-8-queens.txt"));

        return result;
    }

    public SolverChessTestData data;

    public TestSolvers(SolverChessTestData data) {
        this.data = data;
    }

    @Test
    public void testSolver() {
        try {

            HashMap<Piece, Integer> freq = new HashMap<>();
            freq.put(Piece.getKing(),   data.kings);
            freq.put(Piece.getQueen(),  data.queens);
            freq.put(Piece.getBishop(), data.bishops);
            freq.put(Piece.getRook(),   data.rooks);
            freq.put(Piece.getKnight(), data.knights);

            LinkedList<Board> result = new LinkedList<>();
            Solver.Settings settings = new Solver.Settings(false, false, null, result);
            Solver solver = null;

            Constructor<?>[] ctors = this.data.solverClass.getDeclaredConstructors();
            for (Constructor<?> ctor : ctors) {
                if (ctor.getGenericParameterTypes().length == 4) {
                    solver = (Solver)ctor.newInstance(data.M, data.N, freq, settings);
                    break;
                }
            }

            if (solver == null) {
                throw new Exception("oh no");
            }

            solver.solve();

            Collections.sort(data.boards, (a, b) -> a.hashCode() < b.hashCode() ? -1 : 1);
            Collections.sort(result, (a, b) -> a.hashCode() < b.hashCode() ? -1 : 1);

            assertEquals(data.boards.size(), result.size());

            for (int i = 0; i < result.size(); i++) {
                assertEquals(result.get(i).hashCode(), data.boards.get(i).hashCode());
            }
        } catch (Exception e) {
        }
    }

    private static class SolverChessTestData extends ChessTestData {
        public Class<? extends Solver> solverClass;

        public SolverChessTestData(Class<? extends Solver> solverClass, String file) {
            super(file);
        }
    }
}
