package com.infinityraider.miney_games.reference;

public class Names {
    public static final String CHESS_TABLE = "chess_table";
    public static final String POKER_TABLE = "poker_table";
    public static final String POOL_TABLE = "pool_table";

    public static final class NBT extends Names {
        public static final String ADDITIONAL = "mg_add";
        public static final String COLOUR = "mg_clr";
        public static final String PARTICIPANT = "mg_p";
        public static final String PLAYER_1 = "mg_p1";
        public static final String PLAYER_2 = "mg_p2";
        public static final String SCORE = "mg_score";
    }

    private Names() {
        throw new IllegalStateException("Not allowed to instantiate " + this.getClass());
    }
}
