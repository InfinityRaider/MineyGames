package com.infinityraider.miney_games.network.chess;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageReader;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.infinitylib.network.serialization.IMessageWriter;
import com.infinityraider.miney_games.games.chess.ChessBoard;
import com.infinityraider.miney_games.games.chess.ChessGame;
import com.infinityraider.miney_games.games.chess.ChessMove;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.Optional;

public abstract class MessageChessGameBase extends MessageBase {
    public MessageChessGameBase() {
        super();
    }

    @Override
    protected List<IMessageSerializer<?>> getNecessarySerializers() {
        return ImmutableList.of(MOVE_SYNCHRONIZER);
    }

    private static final IMessageSerializer<SyncMove> MOVE_SYNCHRONIZER = new IMessageSerializer<>() {
        @Override
        public boolean accepts(Class<SyncMove> clazz) {
            return SyncMove.class == clazz;
        }

        @Override
        public IMessageWriter<SyncMove> getWriter(Class<SyncMove> clazz) {
            return (buf, move) -> move.writeToBuf(buf);
        }

        @Override
        public IMessageReader<SyncMove> getReader(Class<SyncMove> clazz) {
            return SyncMove::new;
        }
    };

    protected static final class SyncMove {
        private final int fromX;
        private final int fromY;
        private final int toX;
        private final int toY;

        protected SyncMove(ChessMove move) {
            this.fromX = move.fromSquare().getX();
            this.fromY = move.fromSquare().getY();
            this.toX = move.toSquare().getX();
            this.toY = move.toSquare().getY();
        }

        protected SyncMove(FriendlyByteBuf buf) {
            this.fromX = buf.readInt();
            this.fromY = buf.readInt();
            this.toX = buf.readInt();
            this.toY = buf.readInt();
        }

        protected void writeToBuf(FriendlyByteBuf buf) {
            buf.writeInt(fromX);
            buf.writeInt(fromY);
            buf.writeInt(toX);
            buf.writeInt(toY);
        }

        public Optional<ChessMove> getMove(ChessGame game) {
            return this.fromSquare(game).flatMap(from -> this.toSquare(game).flatMap(to -> game.getPotentialMove(from, to)));
        }

        public Optional<ChessBoard.Square> fromSquare(ChessGame game) {
            return game.getBoard().getSquare(this.fromX, this.fromY);
        }

        public Optional<ChessBoard.Square> toSquare(ChessGame game) {
            return game.getBoard().getSquare(this.toX, this.toY);
        }
    }
}
