package com.infinityraider.miney_games.games.chess;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class ChessMove {
    public static ChessMove move(ChessPiece piece, ChessBoard.Square to) {
        return new ChessMove(Type.MOVE, piece, to, ImmutableSet.of());
    }

    public static ChessMove capture(ChessPiece piece, ChessBoard.Square to, ChessPiece captured) {
        return new ChessMove(Type.CAPTURE, piece, to, ImmutableSet.of(new Capture(captured)));
    }

    public static ChessMove castle(ChessPiece king, ChessPiece rook) {
        return new Castle(king, rook);
    }

    private final Type type;
    private final ChessPiece piece;
    private final ChessBoard.Square from;
    private final ChessBoard.Square to;

    private final Set<Capture> captures;

    protected ChessMove(Type type, ChessPiece piece, ChessBoard.Square to, Set<Capture> captures) {
        this.type = type;
        this.piece = piece;
        this.from = piece.currentSquare();
        this.to = to;
        this.captures = captures;
    }

    public void execute() {
        this.captures.forEach(Capture::execute);
        this.fromSquare().removePiece();
        this.toSquare().setPiece(this.getPiece());
        this.getPiece().onMove(this);
    }

    public void undo() {
        this.toSquare().removePiece();
        this.fromSquare().setPiece(this.getPiece());
        this.captures.forEach(Capture::undo);
        this.getPiece().undoMove(this);
    }

    public Type getType() {
        return this.type;
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

    public boolean hasCaptured() {
        return !this.captures.isEmpty();
    }

    protected static class Capture {
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

        private void execute() {
            this.getSquare().removePiece();
            this.getPiece().setCaptured(true);
        }

        private void undo() {
            this.getSquare().setPiece(this.getPiece());
            this.getPiece().setCaptured(false);
            this.getPiece().setSquare(this.getSquare());
        }
    }

    protected static class Castle extends ChessMove {
        private final ChessPiece rook;
        private final ChessBoard.Square rookFromSquare;
        private final ChessBoard.Square rookToSquare;

        protected Castle(ChessPiece king, ChessPiece rook) {
            super(Type.CASTLE, king, getNewKingSquare(king, rook), ImmutableSet.of());
            this.rook = rook;
            this.rookFromSquare = rook.currentSquare();
            this.rookToSquare = getNewRookSquare(king, rook);
        }

        @Override
        public void execute() {
            this.kingToSquare().setPiece(this.getKing());
            this.kingFromSquare().removePiece();
            this.rookToSquare().setPiece(this.getRook());
            this.rookFromSquare().removePiece();
            this.getKing().onMove(this);
            this.getRook().onMove(this);
            this.getRook().setSquare(this.rookToSquare());
        }

        @Override
        public void undo() {
            this.getRook().undoMove(this);
            this.getKing().undoMove(this);
            this.rookFromSquare().setPiece(this.getRook());
            this.rookToSquare().removePiece();
            this.kingFromSquare().setPiece(this.getKing());
            this.kingToSquare().removePiece();
            this.getRook().setSquare(this.rookFromSquare());
        }

        public ChessPiece getKing() {
            return this.getPiece();
        }

        public ChessBoard.Square kingFromSquare() {
            return this.fromSquare();
        }

        public ChessBoard.Square kingToSquare() {
            return this.toSquare();
        }

        public ChessPiece getRook() {
            return this.rook;
        }

        public ChessBoard.Square rookFromSquare() {
            return this.rookFromSquare;
        }

        public ChessBoard.Square rookToSquare() {
            return this.rookToSquare;
        }

        private static ChessBoard.Square getNewKingSquare(ChessPiece king, ChessPiece rook) {
            ChessBoard.Square kingSquare = king.currentSquare();
            ChessBoard.Square rookSquare = rook.currentSquare();
            int dx = 0;
            int dy = 0;
            if(rookSquare.getX() == kingSquare.getX()) {
                dy = rookSquare.getY() > kingSquare.getY() ? 2 :- 2;
            } else if(rookSquare.getY() == kingSquare.getY()) {
                dx = rookSquare.getX() > kingSquare.getX() ? 2 : - 2;
            } else {
                throw new IllegalStateException("Can not castle a king and rook which are not on the same file or rank");
            }
            return kingSquare.offset(king.getBoard(), dx, dy).orElseThrow(() -> new IllegalStateException("Tried to castle off the board"));
        }

        private static ChessBoard.Square getNewRookSquare(ChessPiece king, ChessPiece rook) {
            int dx = 0;
            int dy = 0;
            ChessBoard.Square kingSquare = king.currentSquare();
            ChessBoard.Square rookSquare = rook.currentSquare();
            if(rookSquare.getX() == kingSquare.getX()) {
                dy = rookSquare.getY() > kingSquare.getY() ? 1 :- 1;
            } else if(rookSquare.getY() == kingSquare.getY()) {
                dx = rookSquare.getX() > kingSquare.getX() ? 1 : - 1;
            } else {
                throw new IllegalStateException("Can not castle a king and rook which are not on the same file or rank");
            }
            return kingSquare.offset(king.getBoard(), dx, dy).orElseThrow(() -> new IllegalStateException("Tried to castle off the board"));
        }
    }

    public enum Type {
        MOVE,
        CAPTURE,
        CASTLE;

        public boolean isCapture() {
            return this == CAPTURE;
        }

        public boolean isCastle() {
            return this == CASTLE;
        }
    }
}
