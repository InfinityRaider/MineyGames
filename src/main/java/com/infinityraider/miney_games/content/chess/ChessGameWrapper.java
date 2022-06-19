package com.infinityraider.miney_games.content.chess;

import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.core.GameWrapper;
import com.infinityraider.miney_games.games.chess.*;
import com.infinityraider.miney_games.network.chess.MessageSelectSquare;
import com.infinityraider.miney_games.network.chess.MessageSyncChessMove;
import com.infinityraider.miney_games.reference.Names;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        this.player1 = new Participant(this);
        this.player2 = new Participant(this);
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
        this.asParticipant(player).ifPresent(participant -> this.getGame()
                .map(ChessGame::getCurrentParticipant)
                .map(ChessGame.Participant::getColour)
                .ifPresent(colour -> {
                    if(colour.getName().equals(participant.getColour().getName())) {
                        this.getSquare(hit).ifPresent(square -> participant.onSquareClicked(square, player));
                    }
                }));
        return InteractionResult.PASS;
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

    public Optional<ChessMove> getLastMove() {
        return this.getGame().flatMap(ChessGame::getLastMove);
    }

    public Optional<ChessBoard.Square> getSquare(BlockHitResult hit) {
        if(hit.getType() == HitResult.Type.MISS) {
            return Optional.empty();
        }
        Vec3 abs = this.getTable().offsetAbs(hit.getLocation());
        return this.getSquare(
                this.getTable().getChessSquareIndexAbsX(abs),
                this.getTable().getChessSquareIndexAbsY(abs)
        );
    }

    public Optional<ChessBoard.Square> getSquare(int x, int y) {
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
                        new MessageSyncChessMove(this.getTable(), move).sendToAll();
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

    public void onSyncMessage(MessageSyncChessMove msg) {
        boolean moved = this.getGame()
                .flatMap(msg::getMove)
                .map(this::makeMove)
                .orElse(false);
        if(!moved) {
            MineyGames.instance.getLogger().error("DETECTED DE-SYNC WITH SERVER IN CHESS GAME");
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.put(Names.NBT.SETTINGS, this.getSettings().writeToTag());
        tag.put(Names.NBT.GAME, this.writeChessGame());
        tag.put(Names.NBT.PLAYER_1, this.getPlayer1().writeToNBT());
        tag.put(Names.NBT.PLAYER_2, this.getPlayer2().writeToNBT());
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        this.getSettings().readFromTag(tag.getCompound(Names.NBT.SETTINGS));
        this.readChessGame(tag.getCompound(Names.NBT.GAME));
        this.getPlayer1().readFromNBT(tag.getCompound(Names.NBT.PLAYER_1));
        this.getPlayer2().readFromNBT(tag.getCompound(Names.NBT.PLAYER_2));
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

        private UUID id;
        private ChessColour colour;
        private int score;

        private ChessBoard.Square selected;

        public Participant(ChessGameWrapper game) {
            this.game = game;
        }

        public ChessGameWrapper getWrapper() {
            return this.game;
        }

        public Optional<ChessGame> getGame() {
            return this.getWrapper().getGame();
        }

        public TileChessTable getTable() {
            return this.getWrapper().getTable();
        }

        public void setPlayer(Player player) {
            this.id = player.getUUID();
        }

        public boolean hasPlayer() {
            return this.id != null;
        }

        public boolean isPlayer(Player player) {
            return this.hasPlayer() && this.id.equals(player.getUUID());
        }

        public ChessColour getColour() {
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

        public boolean selectSquare(ChessBoard.Square square) {
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

        protected boolean makeMove(ChessBoard.Square to) {
            if(this.selected != null) {
                return this.getWrapper().makeMoveServer(this.selected, to);
            }
            return false;
        }

        protected CompoundTag writeToNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID(Names.NBT.PARTICIPANT, this.id == null ? Util.NIL_UUID : this.id);
            tag.putString(Names.NBT.COLOUR, this.colour == null ? "" : this.colour.getName());
            tag.putInt(Names.NBT.SCORE, this.score);
            tag.putInt(Names.NBT.X, this.selected == null ? -1 : this.selected.getX());
            tag.putInt(Names.NBT.Y, this.selected == null ? -1 : this.selected.getY());
            return tag;
        }

        protected void readFromNBT(CompoundTag tag) {
            UUID id = tag.getUUID(Names.NBT.PARTICIPANT);
            this.id = id.equals(Util.NIL_UUID) ? null : id;
            this.colour = ChessColour.fromName(tag.getString(Names.NBT.COLOUR));
            this.score = tag.getInt(Names.NBT.SCORE);
            this.selected = this.getWrapper().getSquare(tag.getInt(Names.NBT.X), tag.getInt(Names.NBT.Y)).orElse(null);
        }
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
