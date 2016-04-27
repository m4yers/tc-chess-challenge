package com.blah.app;

final public class Piece {

    public static enum Move {
        PerpendicularSingle,
        Perpendicular,
        Diagonal,
        Knight
    };

    private static Piece king = new Piece(Move.PerpendicularSingle);
    private static Piece queen = new Piece(Move.Perpendicular, Move.Diagonal);
    private static Piece bishop = new Piece(Move.Perpendicular);
    private static Piece rook = new Piece(Move.Diagonal);
    private static Piece knight = new Piece(Move.Knight);

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

    // TODO make it immutable?
    final private Move[] moves;

    private Piece(Move...moves) {
        this.moves = moves.clone();
    }

    public Move[] getMoves() {
        return this.moves.clone();
    }
}
