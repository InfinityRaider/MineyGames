package com.infinityraider.miney_games.games.chess.pieces;

import com.google.common.collect.Sets;
import com.infinityraider.miney_games.games.chess.ChessMove;
import com.infinityraider.miney_games.games.chess.ChessPiece;

import java.util.Set;

public class Bishop extends ChessPiece.Type {
    private static final ChessPiece.Type INSTANCE = new Bishop();

    public static ChessPiece.Type getInstance() {
        return INSTANCE;
    }

    private Bishop() {
        super("bishop", 3);
    }

    @Override
    protected boolean canFinishGame() {
        return false;
    }

    @Override
    public Set<ChessMove> getPotentialMoves(ChessPiece piece) {
        // initialize moves
        Set<ChessMove> moves = Sets.newIdentityHashSet();
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
