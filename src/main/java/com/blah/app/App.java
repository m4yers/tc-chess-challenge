package com.blah.app;

import java.util.LinkedList;
import java.util.HashMap;

public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello Blah!" );
        LinkedList<Board> result = Solver.getAllBoards(new Board(3, 3), new HashMap<Piece, Integer>() {
            {
                put(Piece.getKing(), 2);
                put(Piece.getRook(), 1);
            }
        });
        for (Board board : result) {
            System.out.println(board);
        }
        System.out.println("Total: " + result.size());
        // Board board = new Board(4, 4);
        // board.addPiece(Piece.getQueen(), 1, 1);
        // board.addPiece(Piece.getKing(), 3, 2);
        // board.addBlock(0, 0);
        // board.addBlock(1, 2);
        // Solver.tryToPlace(board, Piece.getQueen());
        // System.out.println(board);
        // System.out.println(board.getFreePositions());
    }
}
