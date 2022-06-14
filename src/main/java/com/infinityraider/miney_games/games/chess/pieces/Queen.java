package com.infinityraider.miney_games.games.chess.pieces;

import com.google.common.collect.Sets;
import com.infinityraider.miney_games.games.chess.ChessMove;
import com.infinityraider.miney_games.games.chess.ChessPiece;

import java.util.Set;

public class Queen extends ChessPiece.Type {
    @Override
    protected boolean canFinishGame() {
        return true;
    }

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
        // scan diagonal left down
        this.scanMoves(piece, -1, -1, moves::add);
        // scan diagonal left up
        this.scanMoves(piece, -1, 1, moves::add);
        // scan diagonal right up
        this.scanMoves(piece, 1, 1, moves::add);
        // scan diagonal right down
        this.scanMoves(piece, 1, -1, moves::add);
        // return the moves
        return moves;
    }
}
