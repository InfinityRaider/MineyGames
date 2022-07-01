package com.infinityraider.miney_games.games.chess;

public enum ChessGameStatus {
    PREGAME(false, false),
    ONGOING(false, false),
    MATE(true, false),
    TIME_OUT(true, false),
    STALEMATE(true, true),
    DRAW(true, true),
    RESIGNED(true, false);

    final boolean finished;
    final boolean draw;

    ChessGameStatus(boolean finished, boolean draw) {
        this.finished = finished;
        this.draw = draw;
    }

    public boolean isStarted() {
        return this != PREGAME;
    }

    public boolean isGoing() {
        return this == ONGOING;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isDraw() {
        return this.draw;
    }
}
