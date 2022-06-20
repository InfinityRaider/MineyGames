package com.infinityraider.miney_games.games.chess.pieces;

import com.google.common.collect.Sets;
import com.infinityraider.miney_games.games.chess.ChessBoard;
import com.infinityraider.miney_games.games.chess.ChessMove;
import com.infinityraider.miney_games.games.chess.ChessPiece;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class King extends ChessPiece.Type {
    private static final ChessPiece.Type INSTANCE = new King();

    public static ChessPiece.Type getInstance() {
        return INSTANCE;
    }

    private King() {
        super("king", 20);
    }

    @Override
    protected boolean canFinishGame() {
        return false;
    }

    @Override
    public Set<ChessMove> getPotentialMoves(ChessPiece king) {
        // initialize moves
        Set<ChessMove> moves = Sets.newIdentityHashSet();
        // try potential squares
        this.checkSquare(king, -1, -1).ifPresent(move -> this.checkAndAddMove(king, move, moves));
        this.checkSquare(king, -1, 0).ifPresent(move -> this.checkAndAddMove(king, move, moves));
        this.checkSquare(king, -1, 1).ifPresent(move -> this.checkAndAddMove(king, move, moves));
        this.checkSquare(king, 0, -1).ifPresent(move -> this.checkAndAddMove(king, move, moves));
        this.checkSquare(king, 0, -1).ifPresent(move -> this.checkAndAddMove(king, move, moves));
        this.checkSquare(king, 1, -1).ifPresent(move -> this.checkAndAddMove(king, move, moves));
        this.checkSquare(king, 1, 0).ifPresent(move -> this.checkAndAddMove(king, move, moves));
        this.checkSquare(king, 1, 1).ifPresent(move -> this.checkAndAddMove(king, move, moves));
        // check for castling
        if(!king.hasMoved()) {
            king.getBoard().streamPieces()
                    .filter(piece -> piece.getColour() == king.getColour())
                    .filter(piece -> !piece.hasMoved())
                    .filter(piece -> piece.getType() == ChessPiece.Pieces.ROOK)
                    .forEach(rook -> {
                        // check if the path between the rook and the king is free
                        if(rook.currentSquare().getX() == king.currentSquare().getX()) {
                            this.checkCastleSquares(king, rook, 0, rook.currentSquare().getY() > king.currentSquare().getY() ? 1 : -1)
                                    .ifPresent(moves::add);
                        } else if(rook.currentSquare().getY() == king.currentSquare().getY()) {
                            this.checkCastleSquares(king, rook, rook.currentSquare().getX() > king.currentSquare().getX() ? 1 : -1, 0)
                                    .ifPresent(moves::add);
                        }
                    });
        }
        // return the moves
        return moves;

    }

    protected void checkAndAddMove(ChessPiece king, ChessMove move, Set<ChessMove> moves) {
        if(!this.isSquareWatched(king, move.toSquare())) {
            moves.add(move);
        }
    }

    protected boolean isSquareWatched(ChessPiece king, ChessBoard.Square square) {
        return king.getBoard().streamPieces()
                .filter(piece -> piece.getColour() != king.getColour())
                .map(ChessPiece::getPotentialMoves)
                .flatMap(Collection::stream)
                .anyMatch(move -> move.toSquare().getX() == square.getX() && move.toSquare().getY() == square.getY());
    }

    protected Optional<ChessMove> checkCastleSquares(ChessPiece king, ChessPiece rook, int dx, int dy) {
        ChessBoard.Square kingSquare = king.currentSquare();
        ChessBoard.Square rookSquare = rook.currentSquare();
        int x = kingSquare.getX();
        int y = kingSquare.getY();
        int count = 0;
        while(x != rookSquare.getX() && y != rookSquare.getY()) {
            x += dx;
            y += dy;
            count += 1;
            final int copyCount = count;
            boolean blocked = king.offset(x, y).map(square -> {
                if(square.getPiece().isPresent()) {
                    // there is a piece between the rook and the king
                    return true;
                }
                if(copyCount <= 2) {
                    // check if the square is under attack
                    if(this.isSquareWatched(king, square)) {
                        return true;
                    }
                }
                // the way for castling is free
                return false;
            }).orElse(true);
            if(blocked) {
                return Optional.empty();
            }
        }
        return Optional.of(ChessMove.castle(king, rook));
    }
}
