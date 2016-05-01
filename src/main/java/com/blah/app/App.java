package com.blah.app;

import java.util.LinkedList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello Blah!" );
        long time = System.nanoTime();

        LinkedList<Board> result = CachingSolver.getAllBoards(new Board(7, 7, 7), new HashMap<Piece, Integer>() {
            {
                put(Piece.getKing(),   2);
                put(Piece.getQueen(),  2);
                put(Piece.getBishop(), 2);
                put(Piece.getKnight(), 1);
            }
        });

        // LinkedList<Board> result = CachingSolver.getAllBoards(new Board(6, 6, 7), new HashMap<Piece, Integer>() {
        //     {
        //         put(Piece.getKing(),   2);
        //         put(Piece.getQueen(),  2);
        //         put(Piece.getBishop(), 2);
        //         put(Piece.getKnight(), 1);
        //     }
        // });

        // LinkedList<Board> result = CachingSolver.getAllBoards(new Board(5, 5, 5), new HashMap<Piece, Integer>() {
        //     {
        //         put(Piece.getKing(),   2);
        //         // put(Piece.getQueen(),  2);
        //         put(Piece.getBishop(), 2);
        //         put(Piece.getKnight(), 1);
        //     }
        // });

        // LinkedList<Board> result = CachingSolver.getAllBoards(new Board(4, 4, 6), new HashMap<Piece, Integer>() {
        //     {
        //         put(Piece.getKnight(), 4);
        //         put(Piece.getRook(), 2);
        //     }
        // });

        time = System.nanoTime() - time;

        // for (Board board : result) {
        //     System.out.println(board);
        // }

        System.out.println("Total: " + result.size() + ", time: " + (time / 1000000) + "ms");
        
        // Board board = new Board(3, 3, 3);
        // board.addBlock(0, 0);
        // board.addPiece(Piece.getRook(), 1, 0);
        // board.addPiece(Piece.getRook(), 2, 0);
        // board.addPiece(Piece.getRook(), 2, 1);
        //
        // System.out.println(board);
        // System.out.println(board.getReflection());
    }
}
