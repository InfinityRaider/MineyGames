package com.infinityraider.miney_games.games.chess;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ChessGame {
    private final ChessBoard board;
    private final List<Participant> participants;
    private final List<Turn> turns;

    private int currentTurn;

    private int currentParticipant;
    private List<ChessMove> currentMoves;

    public ChessGame() {
        this(IChessGameSettings.DEFAULT);
    }

    public ChessGame(IChessGameSettings settings) {
        this.board = new ChessBoard(settings.boardInitializer(), settings.boardWidth(), settings.boardHeight());
        this.participants = settings.participants().stream().map(colour -> new Participant(this, colour)).collect(Collectors.toList());
        this.turns = Lists.newArrayList();
        this.getCurrentParticipant().scanPotentialMoves();
    }

    public ChessBoard getBoard() {
        return this.board;
    }

    public Participant getParticipant(ChessColour colour) {
        return this.participants.stream()
                .filter(participant -> participant.getColour() == colour)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Colour " + colour.getName() + " is not participating to this game"));
    }

    public List<Participant> getParticipants() {
        return this.participants;
    }

    public Participant getCurrentParticipant() {
        return this.getParticipants().get(this.currentParticipant);
    }

    protected void makeMove(ChessMove move) {
        this.currentMoves.add(move);
        if(currentMoves.size() == participants.size()) {
            this.turns.add(new Turn(this.getCurrentTurn(), ImmutableList.copyOf(this.currentMoves)));
            this.currentMoves = Lists.newArrayList();
            this.currentTurn++;
            this.currentParticipant = 0;
        } else {
            this.currentParticipant++;
        }
        this.getCurrentParticipant().scanPotentialMoves();
    }

    public int getCurrentTurn() {
        return this.currentTurn;
    }

    public static class Turn {
        private int count;
        private List<ChessMove> moves;

        private Turn(int count, List<ChessMove> moves) {
            this.count = count;
            this.moves = moves;
        }
    }

    public static class Participant {
        private final ChessGame game;
        private final ChessColour colour;

        private final List<ChessMove> moves;
        private final Set<ChessMove> potentialMoves;

        private boolean isChecked;

        public Participant(ChessGame game, ChessColour colour) {
            this.game = game;
            this.colour = colour;
            this.moves = Lists.newArrayList();
            this.potentialMoves = Sets.newIdentityHashSet();
        }

        public ChessGame getGame() {
            return this.game;
        }

        public ChessColour getColour() {
            return this.colour;
        }

        public Optional<ChessMove> getLastMove() {
            if(this.moves.size() == 0) {
                return Optional.empty();
            }
            return Optional.of(this.moves.get(this.moves.size() - 1));
        }

        public boolean isChecked() {
            return this.isChecked;
        }

        protected void scanPotentialMoves() {
            this.potentialMoves.clear();
            this.getGame().getBoard().streamPieces()
                    .filter(piece -> piece.getColour() == this.getColour())
                    .map(ChessPiece::getPotentialMoves)
                    .flatMap(Collection::stream)
                    .filter(move -> {
                        if(this.isChecked()) {
                            // TODO: filter for moves that break checks
                        }
                        return true;
                    })
                    .forEach(this.potentialMoves::add);
        }
    }
}
