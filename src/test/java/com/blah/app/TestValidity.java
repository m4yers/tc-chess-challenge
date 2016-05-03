package com.blah.app;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.MatchResult;

import java.lang.reflect.Constructor;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.Before;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

@RunWith(Parameterized.class)
@SuppressWarnings("serial")
public class TestValidity {

    @Parameterized.Parameters
    public static Iterable<ChessTestData> cases() {
        LinkedList<ChessTestData> result = new LinkedList<>();
        // result.add(new ChessTestData(BruteForceSolver.class, "/solver-test-case-king.txt"));
        // result.add(new ChessTestData(BruteForceSolver.class, "/solver-test-case-queen.txt"));
        // result.add(new ChessTestData(BruteForceSolver.class, "/solver-test-case-bishop.txt"));
        // result.add(new ChessTestData(BruteForceSolver.class, "/solver-test-case-rook.txt"));
        // result.add(new ChessTestData(BruteForceSolver.class, "/solver-test-case-knight.txt"));
        // result.add(new ChessTestData(BruteForceSolver.class, "/solver-test-case-1.txt"));
        // result.add(new ChessTestData(BruteForceSolver.class, "/solver-test-case-2.txt"));
        // result.add(new ChessTestData(BruteForceSolver.class, "/solver-test-case-8-queens.txt"));
        //
        // result.add(new ChessTestData(CachingSolver.class, "/solver-test-case-king.txt"));
        // result.add(new ChessTestData(CachingSolver.class, "/solver-test-case-queen.txt"));
        // result.add(new ChessTestData(CachingSolver.class, "/solver-test-case-bishop.txt"));
        // result.add(new ChessTestData(CachingSolver.class, "/solver-test-case-rook.txt"));
        // result.add(new ChessTestData(CachingSolver.class, "/solver-test-case-knight.txt"));
        // result.add(new ChessTestData(CachingSolver.class, "/solver-test-case-1.txt"));
        // result.add(new ChessTestData(CachingSolver.class, "/solver-test-case-2.txt"));
        // result.add(new ChessTestData(CachingSolver.class, "/solver-test-case-8-queens.txt"));
        //
        //
        // result.add(new ChessTestData(ThreadingSolver.class, "/solver-test-case-king.txt"));
        // result.add(new ChessTestData(ThreadingSolver.class, "/solver-test-case-queen.txt"));
        // result.add(new ChessTestData(ThreadingSolver.class, "/solver-test-case-bishop.txt"));
        // result.add(new ChessTestData(ThreadingSolver.class, "/solver-test-case-rook.txt"));
        // result.add(new ChessTestData(ThreadingSolver.class, "/solver-test-case-knight.txt"));
        // result.add(new ChessTestData(ThreadingSolver.class, "/solver-test-case-1.txt"));
        // result.add(new ChessTestData(ThreadingSolver.class, "/solver-test-case-2.txt"));
        // result.add(new ChessTestData(ThreadingSolver.class, "/solver-test-case-8-queens.txt"));

        return result;
    }

    public ChessTestData data;

    public TestValidity(ChessTestData data) {
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

    private static class ChessTestData {
        public int M;
        public int N;
        public int kings;
        public int queens;
        public int bishops;
        public int rooks;
        public int knights;
        public ArrayList<Board> boards;
        public Class<? extends Solver> solverClass;

        public ChessTestData(Class<? extends Solver> solverClass, String file) {

            this.solverClass = solverClass;

            InputStream is = TestValidity.class.getResourceAsStream(file);
            Scanner scanner = new Scanner(is);
            MatchResult match;

            scanner.findInLine("(\\d+):(\\d+)\\s?K:(\\d+)\\s?Q:(\\d+)\\s?B:(\\d+)\\s?R:(\\d+)\\s?N:(\\d+)");
            match = scanner.match();

            this.M = Integer.parseInt(match.group(1));
            this.N = Integer.parseInt(match.group(2));

            this.kings = Integer.parseInt(match.group(3));
            this.queens = Integer.parseInt(match.group(4));
            this.bishops = Integer.parseInt(match.group(5));
            this.rooks = Integer.parseInt(match.group(6));
            this.knights = Integer.parseInt(match.group(7));

            scanner.nextLine();
            scanner.findInLine("(\\d+)");
            match = scanner.match();

            int resultsNum = Integer.parseInt(match.group(1));

            this.boards = new ArrayList<>(resultsNum);

            for (int i = 0; i < resultsNum && scanner.hasNextLine(); i++) {
                scanner.nextLine();
                Board board = new Board(M, N, kings + queens + bishops + rooks + knights);
                for (int n = 0; n < N; n++) {
                    String line = scanner.nextLine();
                    String[] tokens = line.split("\\s+");
                    for (int m = 0; m < tokens.length; m++) {
                        String token = tokens[m];
                        switch (token) {
                        case "x": {
                            board.addBlock(m, n);
                            break;
                        }
                        case ".": {
                            break;
                        }
                        default: {
                            Piece piece = Piece.bySymbol(tokens[m]);
                            if (piece == null) {
                                // FIXME something more appropriate
                                throw new IllegalArgumentException("Unknown token '" + token + "'");
                            }
                            board.addPiece(piece, m, n);
                        }
                        }
                    }
                }

                this.boards.add(board);
            }
        }
    }
}
