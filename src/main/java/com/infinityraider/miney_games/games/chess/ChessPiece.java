package com.infinityraider.miney_games.games.chess;

import com.infinityraider.miney_games.games.chess.pieces.Pawn;

import java.util.Set;

public class ChessPiece {
    private final ChessGame game;
    private final ChessColour colour;
    private final Type type;

    private ChessBoard.Square square;
    private boolean hasMoved;
    private boolean isCaptured;

    public ChessPiece(ChessGame game, ChessColour colour, Type type, ChessBoard.Square square) {
        this.game = game;
        this.colour = colour;
        this.type = type;
        this.square = square;
        this.hasMoved = false;
        this.isCaptured = false;
    }

    public ChessGame getGame() {
        return this.game;
    }

    public ChessBoard getBoard() {
        return this.getGame().getBoard();
    }

    public ChessColour getColour() {
        return this.colour;
    }

    public PlayDirection getDirection() {
        return this.getColour().getDirection();
    }

    public Type getType() {
        return this.type;
    }

    public ChessBoard.Square currentSquare() {
        return this.square;
    }

    public boolean hasMoved() {
        return this.hasMoved;
    }

    public boolean isCaptured() {
        return this.isCaptured;
    }

    public static abstract class Type {
        public abstract Set<ChessBoard.Square> getPotentialSquares(ChessPiece piece);
    }

    public static class Pieces {
        public static final Type PAWN = new Pawn();
    }
}
