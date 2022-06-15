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
    private ChessGameStatus status;

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
        this.status = ChessGameStatus.PREGAME;
        this.board = new ChessBoard(settings.boardInitializer(), settings.boardWidth(), settings.boardHeight());
        this.participants = settings.participants().stream().map(colour -> new Participant(this,  settings.createChessClock(), colour)).collect(Collectors.toList());
        this.turns = Lists.newArrayList();
        this.getBoard().forEach(square -> settings.pieceSetup(this, square));
    }

    public ChessGameStatus getStatus() {
        return this.status;
    }

    public void start() {
        this.getCurrentParticipant().scanPotentialMoves();
        this.status = ChessGameStatus.ONGOING;
        this.getCurrentParticipant().getClock().start();
    }

    public void tick() {
        if(this.getStatus().isGoing()) {
            this.participants.stream().map(Participant::getClock).forEach(ChessClock::tick);
        }
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

    public Set<ChessMove> getPotentialMoves() {
        return this.getCurrentParticipant().getPotentialMoves();
    }

    protected void onTimeUp(Participant participant) {
        this.status = ChessGameStatus.TIME_OUT;
    }

    protected void makeMove(ChessMove move) {
        // execute the move
        move.execute();
        // notify the participant the move has been made
        this.getCurrentParticipant().onMoveMade(move);
        // update the move and turn history
        this.currentMoves.add(move);
        if(currentMoves.size() == participants.size()) {
            this.turns.add(new Turn(this.getCurrentTurn(), ImmutableList.copyOf(this.currentMoves)));
            this.currentMoves = Lists.newArrayList();
            this.currentTurn++;
            this.currentParticipant = 0;
        } else {
            this.currentParticipant++;
        }
        // check if there is sufficient material to continue the game
        if(this.participants.stream().noneMatch(Participant::hasSufficientMaterial)) {
            this.status = ChessGameStatus.DRAW;
        } else {
            // scan the next participant's potential moves
            Participant participant = this.getCurrentParticipant().scanPotentialMoves();
            if (participant.getPotentialMoves().isEmpty()) {
                // if there are no moves remaining, the game is over
                if (this.getCurrentParticipant().isMated()) {
                    this.status = ChessGameStatus.MATE;
                } else {
                    this.status = ChessGameStatus.STALEMATE;
                }
            } else {
                // the game continues, start the turn of the next participant
                this.getCurrentParticipant().startTurn();
            }
        }
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

    public static class Participant implements ChessClock.ICallback {
        private final ChessGame game;
        private final ChessClock clock;
        private final ChessColour colour;

        private final List<ChessMove> moves;
        private final Set<ChessMove> potentialMoves;

        private boolean mated;
        private boolean timeUp;

        public Participant(ChessGame game, ChessClock clock, ChessColour colour) {
            this.game = game;
            this.clock = clock.addCallback(this);
            this.colour = colour;
            this.moves = Lists.newArrayList();
            this.potentialMoves = Sets.newIdentityHashSet();
        }

        public Set<ChessMove> getPotentialMoves() {
            return this.potentialMoves;
        }

        private void startTurn() {
            this.getClock().start();
        }

        private void onMoveMade(ChessMove move) {
            this.getClock().stop();
            this.moves.add(move);
            this.potentialMoves.clear();
        }

        public ChessGame getGame() {
            return this.game;
        }

        public ChessBoard getBoard() {
            return this.getGame().getBoard();
        }

        public ChessClock getClock() {
            return this.clock;
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

        public boolean isMated() {
            return this.mated;
        }

        public boolean isTimeUp() {
            return this.timeUp;
        }

        protected Participant scanPotentialMoves() {
            this.potentialMoves.clear();
            this.getBoard().streamPieces()
                    .filter(piece -> piece.getColour() == this.getColour())
                    .map(ChessPiece::getPotentialMoves)
                    .flatMap(Collection::stream)
                    .filter(move -> {
                        // simulate the move to check if a check is occurring
                        move.execute();
                        boolean check = this.isKingAttacked();
                        move.undo();
                        return !check;
                    })
                    .forEach(this.potentialMoves::add);
            if(this.getPotentialMoves().isEmpty()) {
                if(this.isKingAttacked()) {
                    this.mated = true;
                }
            }
            return this;
        }

        protected boolean isKingAttacked() {
            return this.getBoard().streamPieces()
                    // iterate over all pieces of different colours
                    .filter(piece -> piece.getColour() != this.getColour())
                    // check all their potential moves
                    .map(ChessPiece::getPotentialMoves)
                    .flatMap(Collection::stream)
                    // only consider moves that would capture a piece
                    .filter(move -> move.getType().isCapture())
                    // get the targeted square
                    .map(ChessMove::toSquare)
                    // get the piece on the square
                    .map(ChessBoard.Square::getPiece)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    // check if the piece is of the current colour
                    .filter(piece -> piece.getColour() == this.getColour())
                    // check if the piece is the king
                    .anyMatch(piece -> piece.getType() == ChessPiece.Pieces.KING);
        }

        protected boolean hasSufficientMaterial() {
            // gather all remaining pieces that are, on their own, insufficient to finish the game
            List<ChessPiece> pieces = this.getBoard().streamPieces()
                    .filter(piece -> piece.getColour() == this.getColour())
                    .filter(piece -> !piece.getType().canFinishGame())
                    .collect(Collectors.toList());
            // if the player has less than two pieces that can finish the game, material is insufficient
            if(pieces.size() < 2) {
                return false;
            }
            // if all remaining pieces are bishops of the same colour, material is insufficient
            if(pieces.stream().allMatch(piece -> piece.getType() == ChessPiece.Pieces.BISHOP)) {
                final boolean even = pieces.get(0).currentSquare().isEven();
                if(pieces.stream().map(ChessPiece::currentSquare).allMatch(square -> square.isEven() == even)) {
                    return false;
                }
            }
            // material is sufficient if the player has more than two pieces left (that are not all bishops of the same colour)
            if(pieces.size() > 2) {
                return true;
            }
            // if the player has exactly two knights left, material is insufficient
            if(pieces.get(0).getType() == ChessPiece.Pieces.KNIGHT && pieces.get(1).getType() == ChessPiece.Pieces.KNIGHT) {
                return false;
            }
            // either two bishops of a different colour, or a knight and a bishop, thus, sufficient material
            return true;
        }

        @Override
        public void onTimeUp(ChessClock clock) {
            if(clock == this.clock) {
                this.timeUp = true;
                this.getGame().onTimeUp(this);
            }
        }
    }
}
