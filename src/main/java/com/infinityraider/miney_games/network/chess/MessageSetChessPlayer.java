package com.infinityraider.miney_games.network.chess;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import net.minecraft.Util;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public abstract class MessageSetChessPlayer extends MessageBase {
    private TileChessTable table;
    private boolean p1;
    private UUID id;

    public MessageSetChessPlayer() {
        super();
    }

    public MessageSetChessPlayer(TileChessTable table, boolean p1) {
        this(table, p1, Util.NIL_UUID);
    }

    public MessageSetChessPlayer(TileChessTable table, boolean p1, UUID id) {
        this();
        this.table = table;
        this.p1 = p1;
        this.id = id;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.table != null) {
            if(this.p1) {
                this.table.getWrapper().getPlayer1().setPlayer(this.id);
            } else {
                this.table.getWrapper().getPlayer2().setPlayer(this.id);
            }
        }
    }

    public static class ToClient extends MessageSetChessPlayer {
        @SuppressWarnings("unused")
        public ToClient() {
            super();
        }

        public ToClient(TileChessTable table, boolean p1) {
            super(table, p1);
        }

        public ToClient(TileChessTable table, boolean p1, UUID id) {
            super(table, p1, id);
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_CLIENT;
        }

        @Override
        protected void processMessage(NetworkEvent.Context ctx) {
            super.processMessage(ctx);
            MineyGames.instance.proxy().updateMineyGameGui();
        }
    }

    public static class ToServer extends MessageSetChessPlayer {
        @SuppressWarnings("unused")
        public ToServer() {
            super();
        }

        public ToServer(TileChessTable table, boolean p1) {
            super(table, p1);
        }

        public ToServer(TileChessTable table, boolean p1, UUID id) {
            super(table, p1, id);
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_SERVER;
        }
    }


}
