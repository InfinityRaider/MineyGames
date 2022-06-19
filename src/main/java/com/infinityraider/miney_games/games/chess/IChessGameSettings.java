package com.infinityraider.miney_games.games.chess;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public interface IChessGameSettings {
    default int boardWidth() {
        return 8;
    }

    default int boardHeight() {
        return 8;
    }

    default BiFunction<Integer, Integer, ChessBoard.Square> boardInitializer() {
        return ChessBoard.Square::new;
    }

    ChessClock createChessClock();

    List<ChessColour> participants();

    void pieceSetup(ChessGame game, ChessBoard.Square square);

    IChessGameSettings DEFAULT = new IChessGameSettings() {
        private final List<ChessColour> participants = ImmutableList.of(ChessColour.WHITE, ChessColour.BLACK);

        @Override
        public ChessClock createChessClock() {
            return new ChessClock();
        }

        @Override
        public List<ChessColour> participants() {
            return participants;
        }

        @Override
        public void pieceSetup(ChessGame game, ChessBoard.Square square) {
            if(square.getY() == 0) {
                this.createBackRankPiece(game, ChessColour.WHITE, square).ifPresent(square::setPiece);
                return;
            }
            if (square.getY() == 1) {
                square.setPiece(new ChessPiece(game, ChessColour.WHITE, ChessPiece.Pieces.PAWN, square));
                return;
            }
            if(square.getY() == 6) {
                square.setPiece(new ChessPiece(game, ChessColour.BLACK, ChessPiece.Pieces.PAWN, square));
                return;
            }
            if (square.getY() == 7) {
                this.createBackRankPiece(game, ChessColour.BLACK, square).ifPresent(square::setPiece);
            }
        }

        protected Optional<ChessPiece> createBackRankPiece(ChessGame game, ChessColour colour, ChessBoard.Square square) {
            return switch (square.getX()) {
                case 0 -> Optional.of(new ChessPiece(game, colour, ChessPiece.Pieces.ROOK, square));
                case 1 -> Optional.of(new ChessPiece(game, colour, ChessPiece.Pieces.KNIGHT, square));
                case 2 -> Optional.of(new ChessPiece(game, colour, ChessPiece.Pieces.BISHOP, square));
                case 3 -> Optional.of(new ChessPiece(game, colour, ChessPiece.Pieces.QUEEN, square));
                case 4 -> Optional.of(new ChessPiece(game, colour, ChessPiece.Pieces.KING, square));
                case 5 -> Optional.of(new ChessPiece(game, colour, ChessPiece.Pieces.BISHOP, square));
                case 6 -> Optional.of(new ChessPiece(game, colour, ChessPiece.Pieces.KNIGHT, square));
                case 7 -> Optional.of(new ChessPiece(game, colour, ChessPiece.Pieces.ROOK, square));
                default -> Optional.empty();
            };
        }
    };

}
