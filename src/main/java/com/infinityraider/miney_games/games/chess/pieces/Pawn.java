package com.infinityraider.miney_games.games.chess.pieces;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.infinityraider.miney_games.games.chess.*;

import java.util.Optional;
import java.util.Set;

public class Pawn extends ChessPiece.Type {
    private static final Set<ChessPiece.Type> VALID_PROMOTIONS = ImmutableSet.of(
            ChessPiece.Pieces.ROOK,
            ChessPiece.Pieces.KNIGHT,
            ChessPiece.Pieces.BISHOP,
            ChessPiece.Pieces.QUEEN
    );

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
        if(!moves.isEmpty() && !piece.hasMoved() && this.isHomeRow(board, square, dir)) {
            this.checkSquare(piece, 2*dir.dx(), 2*dir.dy(), false).ifPresent(moves::add);
        }
        // capture diagonal left
        this.capture(piece, true);
        // capture diagonal right
        this.capture(piece, false);
        // return
        return moves;
    }

    protected boolean isHomeRow(ChessBoard board, ChessBoard.Square square, PlayDirection dir) {
        Optional<ChessBoard.Square> singleBack = square.offset(board, dir, -1);
        Optional<ChessBoard.Square> doubleBack = square.offset(board, dir, -2);
        return singleBack.isPresent() && !doubleBack.isPresent();
    }

    protected Optional<ChessMove> capture(ChessPiece pawn, boolean left) {
        PlayDirection dir = pawn.getDirection();
        // direct capture
        Optional<ChessMove> direct = pawn.offset(dir.dx() + (left ? -dir.dy() : dir.dy()), dir.dy() + (left ? -dir.dx() : dir.dx()))
                .flatMap(ChessBoard.Square::getPiece)
                .flatMap(piece -> {
                    if(piece.getColour() != pawn.getColour()) {
                        return Optional.of(ChessMove.capture(pawn, piece.currentSquare(), piece));
                    } else {
                        return Optional.empty();
                    }
                });
        if(direct.isPresent()) {
            return direct;
        }
        // en passant capture: get the piece next to the pawn
        return pawn.offset(left ? -dir.dy() : dir.dy(), left ? -dir.dx() : dir.dx())
                .flatMap(ChessBoard.Square::getPiece)
                .flatMap(piece -> {
                    // if the piece is not a pawn, return
                    if(piece.getType() != pawn.getType()) {
                        return Optional.empty();
                    }
                    // if the piece is of the same colour, return
                    if(piece.getColour() != pawn.getColour()) {
                        return Optional.empty();
                    }
                    // check if the pawn has made a double move last move
                    return piece.getLastMove().flatMap(move -> {
                        // check if last move was previous move
                        boolean isPrevMove = pawn.getGame().getParticipant(piece.getColour()).getLastMove()
                                .map(last -> last == move)
                                .orElse(false);
                        if(isPrevMove) {
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
