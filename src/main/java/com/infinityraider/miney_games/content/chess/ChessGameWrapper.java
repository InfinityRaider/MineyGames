package com.infinityraider.miney_games.content.chess;

import com.infinityraider.miney_games.core.GameWrapper;
import com.infinityraider.miney_games.games.chess.*;
import com.infinityraider.miney_games.reference.Names;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

public class ChessGameWrapper extends GameWrapper<ChessGame> {
    private final TileChessTable table;
    private ChessGame game;

    private final Participant player1;
    private final Participant player2;

    protected ChessGameWrapper(TileChessTable table) {
        this.table = table;
        this.game = new ChessGame();
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
        this.asParticipant(player).ifPresent(participant -> {
            boolean isTurn = this.getGame()
                    .map(ChessGame::getCurrentParticipant)
                    .map(ChessGame.Participant::getColour)
                    .map(col -> col.getName().equals(participant.getColour().getName()))
                    .orElse(false);
            if(isTurn) {
                this.getSquare(hit).ifPresent(participant::onSquareClicked);
            }
        });
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

    public Optional <ChessBoard.Square> getSquare(BlockHitResult hit) {
        if(hit.getType() == HitResult.Type.MISS) {
            return Optional.empty();
        }
        Vec3 abs = this.getTable().offsetAbs(hit.getLocation());
        int x = this.getTable().getChessSquareIndexAbsX(abs);
        int y = this.getTable().getChessSquareIndexAbsY(abs);
        if(x < 0 || y < 0) {
            return Optional.empty();
        }
        return this.getGame()
                .map(ChessGame::getBoard)
                .flatMap(board -> board.getSquare(x, y));
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.put(Names.NBT.PLAYER_1, this.getPlayer1().writeToNBT());
        tag.put(Names.NBT.PLAYER_2, this.getPlayer2().writeToNBT());
        // TODO: write selected square
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        this.getPlayer1().readFromNBT(tag.getCompound(Names.NBT.PLAYER_1));
        this.getPlayer2().readFromNBT(tag.getCompound(Names.NBT.PLAYER_2));
        // TODO: read selected square
    }

    private static class Participant {
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

        public void onSquareClicked(ChessBoard.Square square) {
            if(this.selected == null) {
                this.selected = square;
            } else {
                if(this.selected.equals(square)) {
                    this.selected = null;
                } else {
                    // TODO: check if a valid move was made and execute it
                }
            }
        }

        protected CompoundTag writeToNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID(Names.NBT.PARTICIPANT, this.id == null ? Util.NIL_UUID : this.id);
            tag.putString(Names.NBT.COLOUR, this.colour == null ? "" : this.colour.getName());
            tag.putInt(Names.NBT.SCORE, this.score);
            return tag;
        }

        protected void readFromNBT(CompoundTag tag) {
            UUID id = tag.getUUID(Names.NBT.PARTICIPANT);
            this.id = id.equals(Util.NIL_UUID) ? null : id;
            this.colour = ChessColour.fromName(tag.getString(Names.NBT.COLOUR));
            this.score = tag.getInt(Names.NBT.SCORE);
        }


    }

    private static class Settings implements IChessGameSettings {
        @Override
        public int boardWidth() {
            return 0;
        }

        @Override
        public int boardHeight() {
            return 0;
        }

        @Override
        public BiFunction<Integer, Integer, ChessBoard.Square> boardInitializer() {
            return null;
        }

        @Override
        public ChessClock createChessClock() {
            return null;
        }

        @Override
        public List<ChessColour> participants() {
            return null;
        }

        @Override
        public void pieceSetup(ChessGame game, ChessBoard.Square square) {

        }
    }
}
