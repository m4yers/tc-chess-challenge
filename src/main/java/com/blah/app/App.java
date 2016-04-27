package com.blah.app;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        Board board = new Board(3, 3);
        Solver.tryToPlace(board, Piece.getQueen());
        System.out.println( "Hello Blah!" );
    }
}
