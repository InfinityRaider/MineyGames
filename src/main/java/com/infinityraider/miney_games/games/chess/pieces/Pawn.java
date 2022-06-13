package com.infinityraider.miney_games.games.chess.pieces;

import com.google.common.collect.Sets;
import com.infinityraider.miney_games.games.chess.*;

import java.util.Optional;
import java.util.Set;

public class Pawn extends ChessPiece.Type {
    @Override
    public Set<ChessMove> getPotentialMoves(ChessPiece piece) {
        // initialize moves
        Set<ChessMove> moves = Sets.newIdentityHashSet();
        // fetch useful data
        PlayDirection dir = piece.getDirection();
        ChessGame game = piece.getGame();
        ChessBoard board = piece.getBoard();
        ChessBoard.Square square = piece.currentSquare();
        boolean hasMoved = piece.hasMoved();
        // move one up
        square.offset(board, dir.dx(), dir.dy()).ifPresent(target -> {
            if(!target.getPiece().isPresent()) {

            }
        });
        // move two up on first move
        if(!hasMoved && this.isHomeRow(board, square, dir)) {
            square.offset(board, 2*dir.dx(), 2*dir.dy()).ifPresent(moves::add);
        }
        // capture diagonal left

        // capture diagonal right

        // return
        return moves;
    }

    protected boolean isHomeRow(ChessBoard board, ChessBoard.Square square, PlayDirection dir) {
        Optional<ChessBoard.Square> singleBack = square.offset(board, dir, -1);
        Optional<ChessBoard.Square> doubleBack = square.offset(board, dir, -2);
        return singleBack.isPresent() && !doubleBack.isPresent();
    }

    protected boolean canCapture(ChessBoard board, ChessBoard.Square square, ChessColour colour, boolean left) {
        PlayDirection dir = colour.getDirection();
        boolean direct = square.offset(board, dir.dx() + (left ? -dir.dy() : dir.dy()), dir.dy() + (left ? -dir.dx() : dir.dx()))
                .flatMap(ChessBoard.Square::getPiece)
                .map(piece -> piece.getColour() != colour)
                .orElse(false);
        if(direct) {
            return true;
        }
    }
}
