package com.infinityraider.miney_games.games.chess;

public class ChessColour {
    public static final ChessColour WHITE = new ChessColour(PlayDirection.UP, 255, 255, 255);
    public static final ChessColour BLACK = new ChessColour(PlayDirection.DOWN,0, 0, 0);

    private final PlayDirection direction;
    private final int r;
    private final int g;
    private final int b;

    public ChessColour(PlayDirection direction, int r, int g, int b) {
        this.direction = direction;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public PlayDirection getDirection() {
        return this.direction;
    }

    public int getR() {
        return this.r;
    }

    public int getG() {
        return this.g;
    }

    public int getB() {
        return this.b;
    }
}
