package com.infinityraider.miney_games.games.chess;

import com.google.common.collect.Maps;

import java.util.Map;

public class ChessColour {
    // all colours
    private static final Map<String, ChessColour> COLOURS = Maps.newHashMap();

    // for two player chess
    public static final ChessColour WHITE = new ChessColour("white", PlayDirection.UP, 255, 255, 255);
    public static final ChessColour BLACK = new ChessColour("black", PlayDirection.DOWN,0, 0, 0);

    // for four player chess
    public static final ChessColour RED = new ChessColour("red", PlayDirection.UP, 255, 0, 0);
    public static final ChessColour GREEN = new ChessColour("green", PlayDirection.RIGHT,0, 255, 0);
    public static final ChessColour BLUE = new ChessColour("blue", PlayDirection.DOWN,0, 0, 255);
    public static final ChessColour YELLOW = new ChessColour("yellow", PlayDirection.LEFT,255, 255, 0);

    private final String name;
    private final PlayDirection direction;
    private final int r;
    private final int g;
    private final int b;

    public ChessColour(String name, PlayDirection direction, int r, int g, int b) {
        this.name = name;
        this.direction = direction;
        this.r = r;
        this.g = g;
        this.b = b;
        COLOURS.put(this.getName(), this);
    }

    public String getName() {
        return this.name;
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

    @Override
    public String toString() {
        return this.getName();
    }

    public static ChessColour fromName(String name) {
        return COLOURS.get(name);
    }
}
