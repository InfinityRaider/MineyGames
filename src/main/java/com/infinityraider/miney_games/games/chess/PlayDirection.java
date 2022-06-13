package com.infinityraider.miney_games.games.chess;

import java.util.function.Supplier;

public enum  PlayDirection {
    UP(0, 1, Opposites::up),
    DOWN(0, -1, Opposites::down),
    LEFT(-1, 0, Opposites::left),
    RIGHT(1, 0, Opposites::right);

    private final int dx;
    private final int dy;
    private final Supplier<PlayDirection> opposite;

    PlayDirection(int dx, int dy, Supplier<PlayDirection> opposite) {
        this.dx = dx;
        this.dy = dy;
        this.opposite = opposite;
    }

    public int dx() {
        return this.dx;
    }

    public int dy() {
        return this.dy;
    }

    public PlayDirection opposite() {
        return this.opposite.get();
    }

    private static final class Opposites {
        private static PlayDirection up() {
            return DOWN;
        }

        private static PlayDirection down() {
            return UP;
        }

        private static PlayDirection left() {
            return RIGHT;
        }

        private static PlayDirection right() {
            return LEFT;
        }

        private Opposites() {}
    }
}
