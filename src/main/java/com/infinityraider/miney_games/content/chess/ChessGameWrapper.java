package com.infinityraider.miney_games.content.chess;

import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.core.GameWrapper;
import com.infinityraider.miney_games.core.PlayerState;
import com.infinityraider.miney_games.core.Wager;
import com.infinityraider.miney_games.games.chess.*;
import com.infinityraider.miney_games.network.chess.*;
import com.infinityraider.miney_games.reference.Names;
import net.minecraft.Util;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.*;
import java.util.List;

public class ChessGameWrapper extends GameWrapper<ChessGame> {
    private final TileChessTable table;
    private final Settings settings;

    private ChessGame game;

    private final Participant player1;
    private final Participant player2;

    protected ChessGameWrapper(TileChessTable table) {
        this.table = table;
        this.settings = new Settings(); // TODO: differentiate between client and server clock
        this.game = new ChessGame(this.getSettings());
        this.player1 = new Participant(this, true).setColour(ChessColour.WHITE);
        this.player2 = new Participant(this, false).setColour(ChessColour.BLACK);
    }

    public TileChessTable getTable() {
        return this.table;
    }

    @Override
    public Optional<ChessGame> getGame() {
        return Optional.ofNullable(this.game);
    }

    public Settings getSettings() {
        return this.settings;
    }

    public Participant getPlayer1() {
        return this.player1;
    }

    public Participant getPlayer2() {
        return this.player2;
    }

    protected void tick() {
        if(this.game != null) {
            this.game.tick();
        }
    }

    @Override
    public InteractionResult onRightClick(Player player, InteractionHand hand, BlockHitResult hit) {
        if(player.getLevel().isClientSide()) {
            return InteractionResult.PASS;
        }
        if(player.isDiscrete()) {
            if(player instanceof ServerPlayer) {
                NetworkHooks.openGui((ServerPlayer) player, this.getTable(), this.getTable().getBlockPos());
            }
        } else {
            this.asParticipant(player).ifPresent(participant -> this.getGame()
                    .map(ChessGame::getCurrentParticipant)
                    .map(ChessGame.Participant::getColour)
                    .ifPresent(colour -> {
                        if (colour.getName().equals(participant.getColour().getName())) {
                            this.getSquare(hit).ifPresent(square -> participant.onSquareClicked(square, player));
                        }
                    }));
        }
        return InteractionResult.PASS;
    }

    private static void togglePlayerDebug(Participant participant, Player player) {
        if(participant.hasPlayer()) {
            if(participant.isPlayer(player)) {
                participant.removePlayer();
            }
        } else {
            participant.setPlayer(player);
        }
    }

    public void startGame() {
        if(this.isRunning()) {
            return;
        }
        if(this.getPlayer1().hasPlayer() && this.getPlayer1().getState().isReady() && this.getPlayer2().hasPlayer() && this.getPlayer2().getState().isReady()) {
            this.game.start();
            this.getPlayer1().onGameStarted();
            this.getPlayer2().onGameStarted();
            if(this.isServerSide()) {
                new MessageChessGameStart.ToClient(this.getTable());
            }
        }
    }

    public boolean isRunning() {
        return this.getGame().map(ChessGame::getStatus)
                .map(ChessGameStatus::isGoing)
                .orElse(false);
    }

    public boolean isPlayer1(Player player) {
        return this.getPlayer1().isPlayer(player);
    }

    public boolean isPlayer2(Player player) {
        return this.getPlayer2().isPlayer(player);
    }

    public Optional<Participant> asParticipant(Player player) {
        if(this.isPlayer1(player)) {
            return Optional.of(this.getPlayer1());
        }
        if(this.isPlayer2(player)) {
            return Optional.of(this.getPlayer2());
        }
        return Optional.empty();
    }

    protected Optional<ChessMove> getLastMove() {
        return this.getGame().flatMap(ChessGame::getLastMove);
    }

    protected Optional<ChessBoard.Square> getSquare(BlockHitResult hit) {
        if(hit.getType() == HitResult.Type.MISS) {
            return Optional.empty();
        }
        Vec3 abs = this.getTable().offsetAbs(hit.getLocation());
        return this.getSquare(
                this.getTable().getChessSquareIndexAbsX(abs),
                this.getTable().getChessSquareIndexAbsY(abs)
        );
    }

    protected Optional<ChessBoard.Square> getSquare(int x, int y) {
        if(x < 0 || y < 0) {
            return Optional.empty();
        }
        return this.getGame()
                .map(ChessGame::getBoard)
                .flatMap(board -> board.getSquare(x, y));

    }

    protected boolean makeMoveServer(ChessBoard.Square from, ChessBoard.Square to) {
        return this.getGame()
                .flatMap(game -> game.getPotentialMove(from, to))
                .map(move -> {
                    boolean moved = this.makeMove(move);
                    if(moved) {
                        new MessageSyncChessMove(this.getTable(), move);
                    }
                    return moved;
                })
                .orElse(false);
    }

    protected boolean makeMove(ChessMove move) {
        return this.getGame().map(game -> {
            if(move.getPiece().getGame() != game) {
                return false;
            }
            if(!game.getStatus().isGoing()) {
                return false;
            }
            if(game.getCurrentParticipant().getColour() != move.getPiece().getColour()) {
                return false;
            }
            game.makeMove(move);
            return true;
        }).orElse(false);
    }

    public void resign(boolean p1) {
        if(this.isRunning()) {
            this.getGame().ifPresent(game -> {
                if(p1) {
                    game.getParticipant(this.getPlayer1().getColour()).resign();
                    this.getPlayer1().onLose();
                    this.getPlayer2().onWin();
                } else {
                    game.getParticipant(this.getPlayer2().getColour()).resign();
                    this.getPlayer1().onWin();
                    this.getPlayer2().onLose();
                }
                if(this.isServerSide()) {
                    new MessageChessPlayerResign.ToClient(this.getTable(), p1);
                }
            });
        }
    }

    public void onSyncMessage(MessageSyncChessMove msg) {
        boolean moved = this.getGame()
                .flatMap(msg::getMove)
                .map(this::makeMove)
                .orElse(false);
        if(!moved) {
            MineyGames.instance.getLogger().error("DETECTED DE-SYNC WITH SERVER IN CHESS GAME");
        }
    }

    public boolean isClientSide() {
        return this.getTable().isRemote();
    }

    public boolean isServerSide() {
        return !this.isClientSide();
    }

    @Override
    protected void writeToNBT(CompoundTag tag) {
        tag.put(Names.NBT.SETTINGS, this.getSettings().writeToTag());
        tag.put(Names.NBT.GAME, this.writeChessGame());
        tag.put(Names.NBT.PLAYER_1, this.getPlayer1().writeToNBT());
        tag.put(Names.NBT.PLAYER_2, this.getPlayer2().writeToNBT());
    }

    @Override
    protected void readFromNBT(CompoundTag tag) {
        // settings
        if (tag.contains(Names.NBT.SETTINGS)) {
            this.getSettings().readFromTag(tag.getCompound(Names.NBT.SETTINGS));
        } else {
            this.settings.reset();
        }
        // chess game
        if (tag.contains(Names.NBT.GAME)) {
            this.readChessGame(tag.getCompound(Names.NBT.GAME));
        } else {
            this.game = new ChessGame(this.getSettings());
        }
        // participant 1
        if (tag.contains(Names.NBT.PLAYER_1)) {
            this.getPlayer1().readFromNBT(tag.getCompound(Names.NBT.PLAYER_1));
        } else {
            this.getPlayer1().reset();
        }
        // participant 2
        if (tag.contains(Names.NBT.PLAYER_2)) {
            this.getPlayer2().readFromNBT(tag.getCompound(Names.NBT.PLAYER_2));
        } else {
            this.getPlayer2().reset();
        }
    }

    protected CompoundTag writeChessGame() {
        CompoundTag tag = new CompoundTag();
        this.getGame().ifPresent(game -> {
            if(game.getStatus().isStarted()) {
                ListTag movesTag = new ListTag();
                game.getMoveHistory().forEach(move -> {
                    CompoundTag moveTag = new CompoundTag();
                    moveTag.putInt(Names.NBT.X, move.fromSquare().getX());
                    moveTag.putInt(Names.NBT.Y, move.fromSquare().getY());
                    moveTag.putInt(Names.NBT.X2, move.toSquare().getX());
                    moveTag.putInt(Names.NBT.Y2, move.toSquare().getY());
                    movesTag.add(moveTag);
                });
                tag.put(Names.NBT.MOVES, movesTag);
            }
        });
        // TODO: clock sync logic
        return tag;
    }

    protected void readChessGame(CompoundTag tag) {
        ChessGame game = new ChessGame(this.getSettings());
        if(tag.contains(Names.NBT.MOVES)) {
            game.start();
            ListTag moves = tag.getList(Names.NBT.MOVES, Tag.TAG_COMPOUND);
            for(int i = 0; i < moves.size(); i ++) {
                CompoundTag moveTag = moves.getCompound(i);
                Optional<ChessBoard.Square> from = game.getBoard().getSquare(moveTag.getInt(Names.NBT.X), moveTag.getInt(Names.NBT.Y));
                Optional<ChessBoard.Square> to = game.getBoard().getSquare(moveTag.getInt(Names.NBT.X2), moveTag.getInt(Names.NBT.Y2));
                if(from.isPresent() && to.isPresent()) {
                    boolean fail = game.getPotentialMove(from.get(), to.get()).map(move -> {
                        game.makeMove(move);
                        return false;
                    }).orElse(true);
                    if(fail) {
                        MineyGames.instance.getLogger().error("Detected de-sync while syncing chess game after move " + i + " (failed to execute move)");
                        break;
                    }
                } else {
                    MineyGames.instance.getLogger().error( "Detected de-sync while syncing chess game after move " + i + " (failed to retrieve " + (to.isPresent() ? "from" : "to") + " square)");
                    break;
                }
            }
        }
        // TODO: clock sync logic
        this.game = game;
    }

    public static class Participant {
        private final ChessGameWrapper game;
        private final boolean p1;
        private final Wager<ChessGameWrapper> wagers;

        private UUID id;
        private PlayerState state;
        private ChessColour colour;
        private int score;

        private ChessBoard.Square selected;

        public Participant(ChessGameWrapper game, boolean p1) {
            this.game = game;
            this.p1 = p1;
            this.wagers = new Wager<>(game);
            this.state = PlayerState.EMPTY;
        }

        public ChessGameWrapper getWrapper() {
            return this.game;
        }

        private Participant setColour(ChessColour colour) {
            this.colour = colour;
            return this;
        }

        public boolean isPlayer1() {
            return this.p1;
        }

        public boolean isPlayer2() {
            return !this.p1;
        }

        public Wager<ChessGameWrapper> getWagers() {
            return this.wagers;
        }

        protected Optional<ChessGame> getGame() {
            return this.getWrapper().getGame();
        }

        protected Optional<ChessBoard> getBoard() {
            return this.getGame().map(ChessGame::getBoard);
        }

        protected Optional<ChessBoard.Square> getSquare(int x, int y) {
            return this.getBoard().flatMap(board -> board.getSquare(x, y));
        }

        protected Optional<ChessGame.Participant> getParticipant() {
            return this.getGame().map(game -> game.getParticipant(this.getColour()));
        }

        public TileChessTable getTable() {
            return this.getWrapper().getTable();
        }

        public PlayerState getState() {
            return this.state;
        }

        public void removePlayer() {
            if(this.getWrapper().isRunning()) {
                // can't modify the players while the game is running
                return;
            }
            this.id = null;
            this.wagers.leave();
            this.state = PlayerState.EMPTY;
            if(this.isPlayer1()) {
                this.getWrapper().getPlayer2().onOtherPlayerLeft();
            } else {
                this.getWrapper().getPlayer1().onOtherPlayerLeft();
            }
            if(this.isServerSide()) {
                new MessageChessPlayerSet.ToClient(this.getTable(), this.isPlayer1());
            }
        }

        public void setPlayer(UUID id) {
            if(id.equals(Util.NIL_UUID)) {
                this.removePlayer();
            } else {
                if(this.getWrapper().isRunning()) {
                    // can't modify the players while the game is running
                    return;
                }
                this.id = id;
                this.wagers.join(id);
                this.state = PlayerState.PREPARING;
                if(this.isPlayer1()) {
                    this.getWrapper().getPlayer2().onOtherPlayerJoined();
                } else {
                    this.getWrapper().getPlayer1().onOtherPlayerJoined();
                }
                if(this.isServerSide()) {
                    new MessageChessPlayerSet.ToClient(this.getTable(), this.isPlayer1(), id);
                }
            }
        }

        protected void setPlayer(Player player) {
            this.setPlayer(player.getUUID());
        }

        public boolean hasPlayer() {
            return this.id != null;
        }

        public boolean isPlayer(Player player) {
            return this.hasPlayer() && this.id.equals(player.getUUID());
        }

        public UUID getPlayerId() {
            return this.id == null ? Util.NIL_UUID : this.id;
        }

        protected void onOtherPlayerJoined() {
            if(this.hasPlayer()) {
                this.state = PlayerState.PREPARING;
            }
        }

        protected void onOtherPlayerLeft() {
            if(this.hasPlayer()) {
                this.state = PlayerState.PREPARING;
            }
        }

        protected void onGameStarted() {
            if(this.hasPlayer()) {
                this.state = PlayerState.PLAYING;
            }
        }

        protected void onWin() {
            // TODO

        }

        protected void onLose() {
            // TODO
        }

        protected void onDraw() {
            // TODO
        }

        public void setReadiness(boolean ready) {
            if(!this.hasPlayer()) {
                return;
            }
            if(ready) {
                if(this.state.isPreparing()) {
                    this.state = PlayerState.READY;
                    ready = true;
                } else {
                    ready = false;
                }
            } else {
                if(this.state.isReady()) {
                    this.state = PlayerState.PREPARING;
                    ready = false;
                } else {
                    ready = true;
                }
            }
            if(this.isServerSide()) {
                new MessageChessPlayerReady.ToClient(this.getTable(), this.isPlayer1(), ready);
            }
        }

        protected ChessColour getColour() {
            return this.colour;
        }

        protected void onSquareClicked(ChessBoard.Square square, Player player) {
            if(!this.isPlayer(player)) {
                return;
            }
            if(this.selected == null) {
                if(this.selectSquare(square)) {
                    this.selected = square;
                    new MessageSelectSquare(this.getTable(), square).sendTo(player);
                }
            } else {
                if(this.selected.equals(square)) {
                    this.deselectSquare();
                    new MessageSelectSquare(this.getTable()).sendTo(player);
                } else if(this.makeMove(square)) {
                    this.selected = null;
                }
            }
        }

        public void deselectSquare() {
            this.selected = null;
        }

        public boolean selectSquare(int x, int y) {
            return this.getGame().map(ChessGame::getBoard)
                    .flatMap(board -> board.getSquare(x, y))
                    .map(this::selectSquare)
                    .orElse(false);
        }

        protected boolean selectSquare(ChessBoard.Square square) {
            if(this.canSelect(square)) {
                this.selected = square;
                return true;
            }
            return false;
        }

        protected boolean canSelect(ChessBoard.Square square) {
            return square.getPiece()
                    .map(ChessPiece::getColour)
                    .map(colour -> colour.getName().equals(this.getColour().getName()))
                    .orElse(false);
        }

        public boolean hasSelected() {
            return this.selected != null;
        }

        protected boolean makeMove(ChessBoard.Square to) {
            if(this.selected != null) {
                return this.getWrapper().makeMoveServer(this.selected, to);
            }
            return false;
        }

        public boolean isClientSide() {
            return this.getTable().isRemote();
        }

        public boolean isServerSide() {
            return !this.isClientSide();
        }

        public Optional<Vec3i> getHighLightColour(int relX, int relY, boolean hovered) {
            // first check if the square contains a king which is in check
            boolean check = this.getSquare(relX, relY)
                    .flatMap(ChessBoard.Square::getPiece)
                    .map(piece -> piece.getType() == ChessPiece.Pieces.KING && piece.isAttacked())
                    .orElse(false);
            if (check) {
                // the king is in check
                return Optional.of(hovered || this.selected.is(relX, relY)
                        // if the king is selected or hovered over, mark it purple
                        ? HighLights.PURPLE
                        // if the king is not selected or hovered over, mark it red
                        : HighLights.RED
                );
            }
            // the square does not contain a king or is not in check, go back to normal logic
            if (this.hasSelected()) {
                // there is a selected square
                if (this.selected.is(relX, relY)) {
                    // hovered over the currently selected square, mark it blue
                    return Optional.of(HighLights.BLUE);
                } else {
                    // a piece is selected, decide based on hover and selection status
                    return this.selected.getPiece().flatMap(piece -> {
                        // first check if the square is a valid move for the selected piece
                        boolean valid = piece.getPotentialMoves().stream()
                                .map(ChessMove::toSquare)
                                .anyMatch(square -> square.is(relX, relY));
                        if (hovered) {
                            // the square is being hovered over, if it is a valid move return cyan, else return orange
                            return Optional.of(valid ? HighLights.CYAN : HighLights.ORANGE);
                        } else if (valid) {
                            // the square is not being hovered over but is valid, mark it in green
                            return Optional.of(HighLights.GREEN);
                        }
                        // default to no highlighting
                        return Optional.empty();
                    });
                }
            } else {
                // there is no selected square
                return this.getSquare(relX, relY).map(square -> {
                    // check if there is a piece
                    return square.getPiece().map(piece -> {
                        if (piece.isColour(this.getColour())) {
                            if (piece.getPotentialMoves().isEmpty()) {
                                // hovering over a piece without valid moves; highlight yellow
                                return HighLights.YELLOW;
                            } else {
                                // hovering over a piece wiht valid moves; highlight green
                                return HighLights.GREEN;
                            }
                        } else {
                            // hovering over a piece of a different colour; highlight orange
                            return HighLights.ORANGE;
                        }
                    // hovered over an arbitrary square, highlight in blue
                    }).orElse(HighLights.BLUE);
                });
            }
        }

        protected void reset() {
            this.id = null;
            this.score = 0;
            this.selected = null;
            this.state = PlayerState.EMPTY;
            this.getWagers().reset();
        }

        protected CompoundTag writeToNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID(Names.NBT.PARTICIPANT, this.id == null ? Util.NIL_UUID : this.id);
            tag.putString(Names.NBT.COLOUR, this.colour == null ? "" : this.colour.getName());
            tag.putInt(Names.NBT.SCORE, this.score);
            tag.putInt(Names.NBT.X, this.selected == null ? -1 : this.selected.getX());
            tag.putInt(Names.NBT.Y, this.selected == null ? -1 : this.selected.getY());
            this.getState().toTag(tag);
            tag.put(Names.NBT.WAGERS, this.getWagers().writeToNBT());
            return tag;
        }

        protected void readFromNBT(CompoundTag tag) {
            UUID id = tag.contains(Names.NBT.PARTICIPANT) ? tag.getUUID(Names.NBT.PARTICIPANT) : Util.NIL_UUID;
            this.id = id.equals(Util.NIL_UUID) ? null : id;
            this.colour = ChessColour.fromName(tag.getString(Names.NBT.COLOUR));
            this.score = tag.getInt(Names.NBT.SCORE);
            this.selected = this.getWrapper().getSquare(tag.getInt(Names.NBT.X), tag.getInt(Names.NBT.Y)).orElse(null);
            this.state = PlayerState.fromTag(tag);
            if(tag.contains(Names.NBT.WAGERS, Tag.TAG_COMPOUND)) {
                this.getWagers().readFromNBT(tag.getCompound(Names.NBT.WAGERS));
            } else {
                this.getWagers().reset();
            }
        }
    }

    private static final class HighLights {
        public static final Vec3i BLUE = new Vec3i(0, 0, 255);
        public static final Vec3i CYAN = new Vec3i(0, 128, 128);
        public static final Vec3i ORANGE = new Vec3i(255, 128, 0);
        public static final Vec3i GREEN = new Vec3i(0, 255, 0);
        public static final Vec3i RED = new Vec3i(255, 0, 0);
        public static final Vec3i PURPLE = new Vec3i(128, 0, 128);
        public static final Vec3i YELLOW = new Vec3i(255, 255, 0);
    }

    private static class Settings implements IChessGameSettings {
        @Override
        public ChessClock createChessClock() {
            return DEFAULT.createChessClock();
        }

        @Override
        public List<ChessColour> participants() {
            return DEFAULT.participants();
        }

        @Override
        public void pieceSetup(ChessGame game, ChessBoard.Square square) {
            DEFAULT.pieceSetup(game, square);
        }

        protected void reset() {
            // TODO
        }

        public CompoundTag writeToTag() {
            CompoundTag tag = new CompoundTag();
            // TODO
            return tag;
        }

        public void readFromTag(CompoundTag tag) {
            // TODO
        }
    }
}
