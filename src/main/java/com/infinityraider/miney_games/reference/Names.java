package com.infinityraider.miney_games.reference;

public class Names {
    public static final String CHESS_TABLE = "chess_table";
    public static final String POKER_TABLE = "poker_table";
    public static final String POOL_TABLE = "pool_table";

    public static final class NBT extends Names {
        public static final String ADDITIONAL = "mg_add";
        public static final String COLOUR = "mg_clr";
        public static final String GAME = "mg_gme";
        public static final String MOVES = "mg_mvs";
        public static final String PARTICIPANT = "mg_p";
        public static final String PLAYER_1 = "mg_p1";
        public static final String PLAYER_2 = "mg_p2";
        public static final String SCORE = "mg_score";
        public static final String SETTINGS = "mg_sts";
        public static final String STATE = "mg_stt";
        public static final String WAGERS = "mg_wgs";
        public static final String X = "mg_x";
        public static final String Y = "mg_y";
        public static final String X2 = "mg_x2";
        public static final String Y2 = "mg_y2";
    }

    private Names() {
        throw new IllegalStateException("Not allowed to instantiate " + this.getClass());
    }
}
