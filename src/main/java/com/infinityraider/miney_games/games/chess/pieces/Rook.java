package com.infinityraider.miney_games.games.chess.pieces;

import com.google.common.collect.Sets;
import com.infinityraider.miney_games.games.chess.ChessMove;
import com.infinityraider.miney_games.games.chess.ChessPiece;

import java.util.Set;

public class Rook extends ChessPiece.Type {
    @Override
    public Set<ChessMove> getPotentialMoves(ChessPiece piece) {
        // initialize moves
        Set<ChessMove> moves = Sets.newIdentityHashSet();
        // scan left
        this.scanMoves(piece, -1, 0, moves::add);
        // scan right
        this.scanMoves(piece, 1, 0, moves::add);
        // scan up
        this.scanMoves(piece, 0, 1, moves::add);
        // scan down
        this.scanMoves(piece, 0, -1, moves::add);
        // return the moves
        return moves;
    }
}
