package com.infinityraider.miney_games.games.chess;

import com.google.common.collect.Maps;
import com.infinityraider.miney_games.games.chess.pieces.*;
import org.apache.commons.compress.utils.Lists;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class ChessPiece {
    private final ChessGame game;
    private final ChessColour colour;
    private final Type type;

    private ChessBoard.Square square;
    private final List<ChessMove> moves;
    private boolean isCaptured;

    public ChessPiece(ChessGame game, ChessColour colour, Type type, ChessBoard.Square square) {
        this.game = game;
        this.colour = colour;
        this.type = type;
        this.square = square;
        this.moves = Lists.newArrayList();
        this.isCaptured = false;
    }

    public String getName() {
        return this.getType().getName();
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

    public boolean isColour(ChessColour colour) {
        return colour.getName().equals(this.getColour().getName());
    }

    public PlayDirection getDirection() {
        return this.getColour().getDirection();
    }

    public Type getType() {
        return this.type;
    }

    public int getWorth() {
        return this.getType().getWorth();
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

    public boolean isAttacked() {
        return this.getGame().getParticipants().stream()
                .filter(p -> !p.getColour().getName().equals(this.getColour().getName()))
                .flatMap(p -> p.getPieces().stream())
                .flatMap(piece -> piece.getPotentialMoves().stream())
                .filter(move -> move.hasCaptures())
                .flatMap(move -> move.getCaptures())
                .anyMatch(piece -> piece == this);
    }

    protected Set<ChessMove> scanPotentialMoves() {
        if(this.isCaptured()) {
            return Collections.emptySet();
        }
        return this.getType().getPotentialMoves(this);
    }

    public Set<ChessMove> getPotentialMoves() {
        return this.getGame().getPotentialMoves(this);
    }

    void onMove(ChessMove move) {
        this.moves.add(move);
        this.square = move.toSquare();
    }

    void undoMove(ChessMove move) {
        this.moves.remove(move);
        this.square = move.fromSquare();
    }

    void setCaptured(boolean captured) {
        this.isCaptured = captured;
        this.square = ChessBoard.Square.CAPTURED;
    }

    protected void setSquare(ChessBoard.Square square) {
        this.square = square;
    }

    @Override
    public String toString() {
        return this.getType() + "_" + this.getColour();
    }

    public static abstract class Type {
        private static final Map<String, Type> TYPES = Maps.newHashMap();

        private final String name;
        private final int worth;

        protected Type(String name, int worth) {
            this.name = name;
            this.worth = worth;
            TYPES.put(this.getName(), this);
        }

        public final String getName() {
            return this.name;
        }

        public final int getWorth() {
            return this.worth;
        }

        protected  abstract boolean canFinishGame();

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
                    return !move.hasCaptures();
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

        @Override
        public String toString() {
            return this.getName();
        }
    }

    public static class Pieces {
        public static Type fromName(String name) {
            return Type.TYPES.get(name);
        }

        public static final Type PAWN = Pawn.getInstance();
        public static final Type ROOK = Rook.getInstance();
        public static final Type KNIGHT = Knight.getInstance();
        public static final Type BISHOP = Bishop.getInstance();
        public static final Type QUEEN = Queen.getInstance();
        public static final Type KING = King.getInstance();
    }
}
