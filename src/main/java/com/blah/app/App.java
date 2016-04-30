package com.blah.app;

import java.util.LinkedList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello Blah!" );
        // TODO fix non square sizes
        // LinkedList<Board> result = BruteForceSolver.getAllBoards(new Board(6, 6), new HashMap<Piece, Integer>() {
        //     {
        //         put(Piece.getKing(),   2);
        //         put(Piece.getQueen(),  2);
        //         put(Piece.getBishop(), 2);
        //         put(Piece.getKnight(), 1);
        //     }
        // });

        LinkedList<Board> result = BruteForceSolver.getAllBoards(new Board(3, 3), new HashMap<Piece, Integer>() {
            {
                put(Piece.getKnight(), 1);
            }
        });

        for (Board board : result) {
            System.out.println(board);
        }

        System.out.println("Total: " + result.size());
    }
}
