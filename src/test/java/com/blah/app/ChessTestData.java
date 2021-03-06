package com.blah.app;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.MatchResult;

import java.io.InputStream;

import com.blah.app.primitives.*;

public class ChessTestData {
    public int M;
    public int N;
    public int kings;
    public int queens;
    public int bishops;
    public int rooks;
    public int knights;
    public ArrayList<Board> boards;

    public ChessTestData(String file) {

        InputStream is = TestPiecePlacement.class.getResourceAsStream(file);
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
