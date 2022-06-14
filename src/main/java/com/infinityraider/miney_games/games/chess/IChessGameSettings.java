package com.infinityraider.miney_games.games.chess;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.BiFunction;

public interface IChessGameSettings {
    int boardWidth();

    int boardHeight();

    BiFunction<Integer, Integer, ChessBoard.Square> boardInitializer();

    ChessClock createChessClock();

    List<ChessColour> participants();

    ChessPiece pieceSetup(ChessGame game, ChessBoard.Square square);

    IChessGameSettings DEFAULT = new IChessGameSettings() {
        private final List<ChessColour> participants = ImmutableList.of(ChessColour.WHITE, ChessColour.BLACK);

        @Override
        public int boardWidth() {
            return 8;
        }

        @Override
        public int boardHeight() {
            return 0;
        }

        @Override
        public BiFunction<Integer, Integer, ChessBoard.Square> boardInitializer() {
            return ChessBoard.Square::new;
        }

        @Override
        public ChessClock createChessClock() {
            return null;
        }

        @Override
        public List<ChessColour> participants() {
            return participants;
        }

        @Override
        public ChessPiece pieceSetup(ChessGame game, ChessBoard.Square square) {
            return null;
        }
    };

}
