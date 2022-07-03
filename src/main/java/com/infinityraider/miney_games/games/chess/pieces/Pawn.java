package com.infinityraider.miney_games.games.chess.pieces;

import com.google.common.collect.Sets;
import com.infinityraider.miney_games.games.chess.*;

import java.util.Optional;
import java.util.Set;

public class Pawn extends ChessPiece.Type {
    private static final ChessPiece.Type INSTANCE = new Pawn();

    public static ChessPiece.Type getInstance() {
        return INSTANCE;
    }

    private Pawn() {
        super("pawn", 1);
    }

    @Override
    protected boolean canFinishGame() {
        return true;
    }

    @Override
    public Set<ChessMove> getPotentialMoves(ChessPiece piece) {
        // initialize moves
        Set<ChessMove> moves = Sets.newIdentityHashSet();
        // fetch useful data
        PlayDirection dir = piece.getDirection();
        ChessBoard board = piece.getBoard();
        ChessBoard.Square square = piece.currentSquare();
        // move one up
        this.checkSquare(piece, dir.dx(), dir.dy(), false).ifPresent(moves::add);
        // TODO: promotion
        // move two up on first move
        if (!moves.isEmpty() && !piece.hasMoved() && this.isHomeRow(board, square, dir)) {
            this.checkSquare(piece, 2 * dir.dx(), 2 * dir.dy(), false).ifPresent(moves::add);
        }
        // direct capture diagonal left
        this.captureDirect(piece, true).ifPresent(moves::add);
        // direct capture diagonal right
        this.captureDirect(piece, false).ifPresent(moves::add);
        // en passsant capture diagonal left
        this.captureEnPassant(piece, true).ifPresent(moves::add);
        // en passsant capture diagonal left
        this.captureEnPassant(piece, false).ifPresent(moves::add);
        // return
        return moves;
    }

    protected boolean isHomeRow(ChessBoard board, ChessBoard.Square square, PlayDirection dir) {
        Optional<ChessBoard.Square> singleBack = square.offset(board, dir, -1);
        Optional<ChessBoard.Square> doubleBack = square.offset(board, dir, -2);
        return singleBack.isPresent() && !doubleBack.isPresent();
    }

    protected Optional<ChessMove> captureDirect(ChessPiece pawn, boolean left) {
        PlayDirection dir = pawn.getDirection();
        return pawn.offset(dir.dx() + (left ? -dir.dy() : dir.dy()), dir.dy() + (left ? -dir.dx() : dir.dx()))
                .flatMap(ChessBoard.Square::getPiece)
                .flatMap(piece -> {
                    if (piece.getColour() != pawn.getColour()) {
                        return Optional.of(ChessMove.capture(pawn, piece.currentSquare(), piece));
                    } else {
                        return Optional.empty();
                    }
                });
    }

    protected Optional<ChessMove> captureEnPassant(ChessPiece pawn, boolean left) {
        PlayDirection dir = pawn.getDirection();
        // en passant capture: get the piece next to the pawn
        return pawn.offset(left ? -dir.dy() : dir.dy(), left ? -dir.dx() : dir.dx())
                .flatMap(ChessBoard.Square::getPiece)
                .flatMap(piece -> {
                    // if the piece is not a pawn, return
                    if (piece.getType() != pawn.getType()) {
                        return Optional.empty();
                    }
                    // if the piece is of the same colour, return
                    if (piece.getColour().getName().equals(pawn.getColour().getName())) {
                        return Optional.empty();
                    }
                    // check if the pawn has made a double move last move
                    return piece.getLastMove().flatMap(move -> {
                        // check if last move was previous move
                        boolean isPrevMove = pawn.getGame().getParticipant(piece.getColour()).getLastMove()
                                .map(last -> last == move)
                                .orElse(false);
                        if (isPrevMove) {
                            ChessBoard.Square from = move.fromSquare();
                            ChessBoard.Square to = move.toSquare();
                            int dx = to.getX() - from.getX();
                            int dy = to.getY() - from.getY();
                            if ((dx != 0 && dx == 2 * piece.getDirection().dx()) || (dy != 0 && dy == 2 * piece.getDirection().dy())) {
                                return Optional.of(ChessMove.capture(
                                        pawn,
                                        pawn.offset(dir.dx() + (left ? -dir.dy() : dir.dy()), dir.dy() + (left ? -dir.dx() : dir.dx())).orElseThrow(
                                                () -> new IllegalStateException("Tried to capture en passant to a non existent square")),
                                        piece
                                ));
                            }
                        }
                        return Optional.empty();
                    });
                });
    }
}
