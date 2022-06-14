package com.infinityraider.miney_games.games.chess.pieces;

import com.google.common.collect.Sets;
import com.infinityraider.miney_games.games.chess.ChessMove;
import com.infinityraider.miney_games.games.chess.ChessPiece;

import java.util.Set;

public class Knight extends ChessPiece.Type {
    @Override
    public Set<ChessMove> getPotentialMoves(ChessPiece piece) {
        // initialize moves
        Set<ChessMove> moves = Sets.newIdentityHashSet();
        // try potential squares
        this.checkSquare(piece, -2, 1).ifPresent(moves::add);
        this.checkSquare(piece, -2, -1).ifPresent(moves::add);
        this.checkSquare(piece, -1, 2).ifPresent(moves::add);
        this.checkSquare(piece, -1, -2).ifPresent(moves::add);
        this.checkSquare(piece, 1, 2).ifPresent(moves::add);
        this.checkSquare(piece, 1, -2).ifPresent(moves::add);
        this.checkSquare(piece, 2, 1).ifPresent(moves::add);
        this.checkSquare(piece, 2, -1).ifPresent(moves::add);
        // return the moves
        return moves;
    }
}
