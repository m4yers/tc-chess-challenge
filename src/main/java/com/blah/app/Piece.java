package com.blah.app;

final public class Piece {

    public static enum Move {
        Perpendicular,
        Diagonal,
        Knight,
        King
    };

    private static Piece king = new Piece("K", Move.King);
    private static Piece queen = new Piece("Q", Move.Perpendicular, Move.Diagonal);
    private static Piece bishop = new Piece("B", Move.Diagonal);
    private static Piece rook = new Piece("R", Move.Perpendicular);
    private static Piece knight = new Piece("N", Move.Knight);

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

    // TODO make it immutable?
    final private Move[] moves;
    final String symbol;

    private Piece(String symbol, Move...moves) {
        this.symbol = symbol;
        this.moves = moves.clone();
    }

    public Move[] getMoves() {
        return this.moves.clone();
    }

    public String getSymbol() {
        return symbol;
    }

    public String toString() {
        return symbol;
    }
}
