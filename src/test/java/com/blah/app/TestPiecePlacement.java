package com.blah.app;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import java.util.regex.MatchResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.Before;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

import com.blah.app.primitives.*;
import com.blah.app.solvers.*;
import com.blah.app.utils.*;

@RunWith(Parameterized.class)
@SuppressWarnings("serial")
public class TestPiecePlacement {

    @Parameterized.Parameters
    public static Iterable<ChessTestData> cases() {
        LinkedList<ChessTestData> result = new LinkedList<>();
        result.add(new ChessTestData("/solver-test-case-king.txt"));
        result.add(new ChessTestData("/solver-test-case-queen.txt"));
        result.add(new ChessTestData("/solver-test-case-bishop.txt"));
        result.add(new ChessTestData("/solver-test-case-rook.txt"));
        result.add(new ChessTestData("/solver-test-case-knight.txt"));
        result.add(new ChessTestData("/solver-test-case-1.txt"));
        result.add(new ChessTestData("/solver-test-case-2.txt"));
        result.add(new ChessTestData("/solver-test-case-8-queens.txt"));

        return result;
    }

    public ChessTestData data;

    public TestPiecePlacement(ChessTestData data) {
        this.data = data;
    }

    @Test
    public void testSolver() {
        for (Board test : this.data.boards) {
            Board board = new Board(test.M(), test.N(), test.P());

            /*
             * We are making sure that we can produce the same board following test board input
             */
            for (Map.Entry<Board.Location, Piece> entry : test.pieces().entrySet()) {
                assertTrue(Utils.tryToPlace(board, entry.getValue(), entry.getKey() ));
            }

            assertEquals(test.toString(), board.toString());

            /*
             * We cannot place a piece on blocked location
             */
            for (Board.Location loc : board.getLocations()) {
                if (board.isBlocked(loc)) {
                    assertFalse(Utils.tryToPlace(board, Piece.getKing(), loc));
                    assertFalse(Utils.tryToPlace(board, Piece.getQueen(), loc));
                    assertFalse(Utils.tryToPlace(board, Piece.getBishop(), loc));
                    assertFalse(Utils.tryToPlace(board, Piece.getRook(), loc));
                    assertFalse(Utils.tryToPlace(board, Piece.getKnight(), loc));
                }
            }
        }
    }
}
