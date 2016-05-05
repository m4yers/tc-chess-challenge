package com.blah.app.primitives;

/*
 *
 *   ."".    ."",
 *   |  |   /  /
 *   |  |  /  /
 *   |  | /  /
 *   |  |/  ;-._
 *   }  ` _/  / ;
 *   |  /` ) /  /
 *   | /  /_/\_/\
 *   |/  /      |
 *   (  ' \ '-  |
 *    \    `.  /
 *     |      |
 *     |      | Peace Bro;)
 */
final public class Piece {

    private static Piece king = new Piece("K");
    private static Piece queen = new Piece("Q");
    private static Piece bishop = new Piece("B");
    private static Piece rook = new Piece("R");
    private static Piece knight = new Piece("N");

    private static Piece[] pieces = { king, queen, bishop, rook, knight };

    public static Piece getKing() {
        return king;
    }

    public static Piece getQueen() {
        return queen;
    }

    public static Piece getBishop() {
        return bishop;
    }

    public static Piece getRook() {
        return rook;
    }

    public static Piece getKnight() {
        return knight;
    }

    public static Piece bySymbol(String symbol) {
        for (Piece piece : pieces) {
            if (symbol.compareTo(piece.getSymbol()) == 0) {
                return piece;
            }
        }
        return null;
    }

    final String symbol;

    private Piece(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public String toString() {
        return symbol;
    }
}
