package com.infinityraider.miney_games.games.chess;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class ChessMove {
    public static ChessMove move(ChessPiece piece, ChessBoard.Square to) {
        return new ChessMove(piece, to, ImmutableSet.of());
    }

    public static ChessMove capture(ChessPiece piece, ChessBoard.Square to, ChessPiece captured) {
        return new ChessMove(piece, to, ImmutableSet.of(new Capture(captured)));
    }

    public static ChessMove castle(ChessPiece king, ChessPiece rook) {

    }

    private final ChessPiece piece;

    private final ChessBoard.Square from;
    private final ChessBoard.Square to;

    private final Set<Capture> captures;

    protected ChessMove(ChessPiece piece, ChessBoard.Square to, Set<Capture> captures) {
        this.piece = piece;
        this.from = piece.currentSquare();
        this.to = to;
        this.captures = captures;
    }

    public ChessPiece getPiece() {
        return this.piece;
    }

    public ChessBoard.Square fromSquare() {
        return this.from;
    }

    public ChessBoard.Square toSquare() {
        return this.to;
    }

    private static class Capture {
        private final ChessPiece piece;
        private final ChessBoard.Square square;

        public Capture(ChessPiece piece) {
            this.piece = piece;
            this.square = piece.currentSquare();
        }

        public ChessPiece getPiece() {
            return this.piece;
        }

        public ChessBoard.Square getSquare() {
            return this.square;
        }
    }

    protected static class Castle extends ChessMove {
        private final ChessPiece rook;
        private final ChessBoard.Square rookFromSquare;

        protected Castle(ChessPiece king, ChessPiece rook) {
            super(king, king.currentSquare(), to, ImmutableSet.of());
            this.rook = rook;
            this.rookFromSquare = rook.currentSquare();
        }

        private static ChessBoard.Square getNewKingSquare(ChessPiece king, ChessPiece rook) {
            PlayDirection direction = king.getDirection();
            // TODO
        }
    }
}
