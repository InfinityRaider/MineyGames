package com.infinityraider.miney_games.games.chess;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ChessBoard {
    private final Square[][] board;
    private final int width;
    private final int height;

    public ChessBoard() {
        this(Square::new);
    }

    public ChessBoard(BiFunction<Integer, Integer, Square> initializer) {
        this(initializer, 8, 8);
    }

    public ChessBoard(BiFunction<Integer, Integer, Square> initializer, int width, int height) {
        this.board = new Square[width][height];
        this.width = width;
        this.height = height;
        for(int x = 0; x < this.getWidth(); x++) {
            for(int y = 0; y < this.getHeight(); y++) {
                this.board[x][y] = initializer.apply(x, y);
            }
        }
    }

    public final int getWidth() {
        return this.width;
    }

    public final int getHeight() {
        return this.height;
    }

    public Stream<Square> streamSquares() {
        return Arrays.stream(this.board).flatMap(Arrays::stream).filter(Square::isAccessible);
    }

    public Stream<ChessPiece> streamPieces() {
        return this.streamSquares()
                .map(Square::getPiece)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public void forEach(Consumer<Square> consumer) {
        this.streamSquares().forEach(consumer);
    }

    public void performMove(ChessMove move) {

    }

    public void undoMove(ChessMove move) {

    }

    public Optional<Square> getSquare(int x, int y) {
        if(x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight()) {
            Square square = this.board[x][y];
            if(square.isAccessible()) {
                return Optional.of(square);
            }
        }
        return Optional.empty();
    }

    private Optional<Square> offset(Square square, int x, int y) {
        return this.getSquare(square.getX() + x, square.getY() + y);
    }

    private Optional<Square> offset(Square square, PlayDirection dir) {
        return this.offset(square, dir, 1);
    }

    private Optional<Square> offset(Square square, PlayDirection dir, int mul) {
        return this.offset(square, mul*dir.dx(), mul*dir.dy());
    }

    public static class Square {
        public static final Square CAPTURED = new Square(Integer.MIN_VALUE, Integer.MIN_VALUE) {
            @Override
            public boolean isAccessible() {
                return false;
            }

            @Override
            public Optional<Square> offset(ChessBoard board, int dx, int dy) {
                return Optional.empty();
            }

            @Override
            public Optional<Square> offset(ChessBoard board, PlayDirection direction) {
                return Optional.empty();
            }

            @Override
            public Optional<Square> offset(ChessBoard board, PlayDirection direction, int mul) {
                return Optional.empty();
            }
        };

        private final int x;
        private final int y;

        private final String coordinates;

        @Nullable
        private ChessPiece piece;

        public Square(int x, int y) {
            this.x = x;
            this.y = y;
            this.coordinates = buildCoordinates(x, y);
        }

        public boolean isEven() {
            return (this.getX() + this.getY()) % 2 == 0;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public boolean is(int x, int y) {
            return this.getX() == x && this.getY() == y;
        }

        public String getCoordinates() {
            return this.coordinates;
        }

        public boolean isAccessible() {
            return true;
        }

        public Optional<Square> offset(ChessBoard board, int dx, int dy) {
            return board.offset(this, dx, dy);
        }

        public Optional<Square> offset(ChessBoard board, PlayDirection direction) {
            return board.offset(this, direction);
        }

        public Optional<Square> offset(ChessBoard board, PlayDirection direction, int mul) {
            return board.offset(this, direction, mul);
        }

        public Optional<ChessPiece> getPiece() {
            return Optional.ofNullable(this.piece);
        }

        public Square removePiece() {
            this.piece = null;
            return this;
        }

        public Square setPiece(ChessPiece piece) {
            this.piece = piece;
            piece.getGame().onPieceAdded(piece);
            return this;
        }

        @Override
        public String toString() {
            return this.getCoordinates();
        }

        protected static String buildCoordinates(int x, int y) {
            return "" + (char) ('a' + x) + (y + 1);
        }
    }
}
