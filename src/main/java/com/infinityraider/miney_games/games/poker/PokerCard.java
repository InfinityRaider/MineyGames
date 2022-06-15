package com.infinityraider.miney_games.games.poker;

import java.util.Arrays;
import java.util.stream.Stream;

public class PokerCard {
    public static Stream<PokerCard> stream() {
        return Arrays.stream(Type.values()).flatMap(type -> Arrays.stream(Value.values()).map(value -> new PokerCard(type, value)));
    }
    private final Type type;
    private final Value value;

    protected PokerCard(Type type, Value value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return this.type;
    }

    public Colour getColour() {
        return this.getType().getColour();
    }

    public Value getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj instanceof PokerCard) {
            PokerCard other = (PokerCard) obj;
            return this.getType() == other.getType() && this.getValue() == other.getValue();
        }
        return false;
    }

    public enum Type {
        CLUBS(Colour.BLACK),
        SPADES(Colour.BLACK),
        DIAMONDS(Colour.RED),
        HEARTS(Colour.RED);

        private final Colour colour;

        Type(Colour colour) {
            this.colour = colour;
        }

        public Colour getColour() {
            return this.colour;
        }
    }

    public enum Value {
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING,
        ACE;
    }

    public enum Colour {
        BLACK(0, 0, 0),
        RED(255, 0, 0);

        private final int r;
        private final int g;
        private final int b;

        Colour(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
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
}
