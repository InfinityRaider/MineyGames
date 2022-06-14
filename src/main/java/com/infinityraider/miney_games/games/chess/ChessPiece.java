package com.infinityraider.miney_games.games.chess;

import com.infinityraider.miney_games.games.chess.pieces.*;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class ChessPiece {
    private final ChessGame game;
    private final ChessColour colour;
    private final Type type;

    private ChessBoard.Square square;
    private final List<ChessMove> moves;
    private boolean isCaptured;

    private Set<ChessColour> checking;

    public ChessPiece(ChessGame game, ChessColour colour, Type type, ChessBoard.Square square) {
        this.game = game;
        this.colour = colour;
        this.type = type;
        this.square = square;
        this.moves = Lists.newArrayList();
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

    public Optional<ChessBoard.Square> offset() {
        return this.currentSquare().offset(this.getBoard(), this.getDirection());
    }

    public Optional<ChessBoard.Square> offset(int mul) {
        return this.currentSquare().offset(this.getBoard(), this.getDirection(), mul);
    }

    public Optional<ChessBoard.Square> offset(int dx, int dy) {
        return this.currentSquare().offset(this.getBoard(), dx, dy);
    }

    public Optional<ChessMove> getLastMove() {
        if(this.moves.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(this.moves.get(this.moves.size() - 1));
        }
    }

    public boolean hasMoved() {
        return !this.moves.isEmpty();
    }

    public boolean isCaptured() {
        return this.isCaptured;
    }

    public Set<ChessMove> getPotentialMoves() {
        return this.getType().getPotentialMoves(this);
    }

    public static abstract class Type {
        protected abstract Set<ChessMove> getPotentialMoves(ChessPiece piece);

        protected void scanMoves(ChessPiece piece, int dx, int dy, Consumer<ChessMove> consumer) {
            boolean flag = true;
            int x = 0;
            int y = 0;
            while(flag) {
                // increment coordinates
                x += dx;
                y += dy;
                // check the square
                flag = this.checkSquare(piece, x, y).map(move -> {
                    consumer.accept(move);
                    return !move.hasCaptured();
                }).orElse(false);
            }
        }

        protected Optional<ChessMove> checkSquare(ChessPiece piece, int dx, int dy) {
            return this.checkSquare(piece, dx, dy, true);
        }

        protected Optional<ChessMove> checkSquare(ChessPiece piece, int dx, int dy, boolean canCapture) {
            return piece.offset(dx, dy).flatMap(square -> {
                Optional<ChessPiece> current = square.getPiece();
                if(current.isPresent()) {
                    if(current.get().getColour().equals(piece.getColour())) {
                        return Optional.empty();
                    } else {
                        return canCapture
                                ? Optional.of(ChessMove.capture(piece, square, current.get()))
                                : Optional.empty();
                    }
                } else {
                    return Optional.of(ChessMove.move(piece, square));
                }
            });
        }
    }

    public static class Pieces {
        public static final Type PAWN = new Pawn();
        public static final Type ROOK = new Rook();
        public static final Type KNIGHT = new Knight();
        public static final Type BISHOP = new Bishop();
        public static final Type QUEEN = new Queen();
        public static final Type KING = new King();
    }
}
