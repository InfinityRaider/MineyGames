package com.infinityraider.miney_games.games.chess;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class ChessGame {
    private final ChessBoard board;
    private final List<Participant> participants;

    private int currentTurn;
    private List<ChessMove> currentMoves;

    public ChessGame() {
        this(IChessGameSettings.DEFAULT);
    }

    public ChessGame(IChessGameSettings settings) {
        this.board = new ChessBoard(settings.boardInitializer(), settings.boardWidth(), settings.boardHeight());
        this.participants = settings.participants().stream().map(Participant::new).collect(Collectors.toList());
    }

    public ChessBoard getBoard() {
        return this.board;
    }

    public List<Participant> getParticipants() {
        return this.participants;
    }

    public static class Turn {
        private int count;
        private List<ChessMove> moves;

        private Turn(int count, List<ChessMove> moves) {
            this.count = count;
            this.moves = moves;
        }
    }

    private static class Participant {
        private final ChessColour colour;
        private final List<ChessMove> moves;

        public Participant(ChessColour colour) {
            this.colour = colour;
            this.moves = Lists.newArrayList();
        }

        public ChessColour getColour() {
            return this.colour;
        }
    }
}
