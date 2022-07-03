package com.infinityraider.miney_games.client.handler;

import com.google.common.collect.Maps;
import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.content.chess.ChessGameWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.EnumMap;

@OnlyIn(Dist.CLIENT)
public final class ModelHandler {
    private static final ModelHandler INSTANCE = new ModelHandler();

    public static ModelHandler getInstance() {
        return INSTANCE;
    }

    private ModelHandler() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void registerSpecialModels(ModelRegistryEvent event) {
        ChessModels.MODELS.values().forEach(ForgeModelBakery::addSpecialModel);
    }

    public static ResourceLocation getChessModel(ChessGameWrapper.Piece piece) {
        return ChessModels.MODELS.get(piece);
    }

    public static final class ChessModels {
        private static final String DIR = "games/chess/";

        public static final ResourceLocation PAWN_WHITE = white("pawn");
        public static final ResourceLocation ROOK_WHITE = white("rook");
        public static final ResourceLocation KNIGHT_WHITE = white("knight");
        public static final ResourceLocation BISHOP_WHITE = white("bishop");
        public static final ResourceLocation QUEEN_WHITE = white("queen");
        public static final ResourceLocation KING_WHITE = white("king");

        public static final ResourceLocation PAWN_BLACK = black("pawn");
        public static final ResourceLocation ROOK_BLACK = black("rook");
        public static final ResourceLocation KNIGHT_BLACK = black("knight");
        public static final ResourceLocation BISHOP_BLACK = black("bishop");
        public static final ResourceLocation QUEEN_BLACK = black("queen");
        public static final ResourceLocation KING_BLACK = black("king");

        public static final EnumMap<ChessGameWrapper.Piece, ResourceLocation> MODELS = Maps.newEnumMap(ChessGameWrapper.Piece.class);

        private static ResourceLocation white(String name) {
            return new ResourceLocation(MineyGames.instance.getModId(), DIR + "white_" + name);
        }

        private static ResourceLocation black(String name) {
            return new ResourceLocation(MineyGames.instance.getModId(), DIR + "black_" + name);
        }

        private ChessModels() {
            throw new IllegalStateException("LEAVE ME ALONE");
        }

        static {
            // white pieces
            MODELS.put(ChessGameWrapper.Piece.WHITE_PAWN, PAWN_WHITE);
            MODELS.put(ChessGameWrapper.Piece.WHITE_ROOK, ROOK_WHITE);
            MODELS.put(ChessGameWrapper.Piece.WHITE_KNIGHT, KNIGHT_WHITE);
            MODELS.put(ChessGameWrapper.Piece.WHITE_BISHOP, BISHOP_WHITE);
            MODELS.put(ChessGameWrapper.Piece.WHITE_QUEEN, QUEEN_WHITE);
            MODELS.put(ChessGameWrapper.Piece.WHITE_KING, KING_WHITE);
            // black pieces
            MODELS.put(ChessGameWrapper.Piece.BLACK_PAWN, PAWN_BLACK);
            MODELS.put(ChessGameWrapper.Piece.BLACK_ROOK, ROOK_BLACK);
            MODELS.put(ChessGameWrapper.Piece.BLACK_KNIGHT, KNIGHT_BLACK);
            MODELS.put(ChessGameWrapper.Piece.BLACK_BISHOP, BISHOP_BLACK);
            MODELS.put(ChessGameWrapper.Piece.BLACK_QUEEN, QUEEN_BLACK);
            MODELS.put(ChessGameWrapper.Piece.BLACK_KING, KING_BLACK);
        }
    }
}
