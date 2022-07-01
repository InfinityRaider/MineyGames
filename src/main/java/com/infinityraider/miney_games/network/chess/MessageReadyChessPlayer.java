package com.infinityraider.miney_games.network.chess;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import net.minecraft.Util;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public abstract class MessageReadyChessPlayer extends MessageBase {
    private TileChessTable table;
    private boolean p1;
    private boolean ready;

    public MessageReadyChessPlayer() {
        super();
    }

    public MessageReadyChessPlayer(TileChessTable table, boolean p1, boolean ready) {
        this();
        this.table = table;
        this.p1 = p1;
        this.ready = ready;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.table != null) {
            if(this.p1) {
                this.table.getWrapper().getPlayer1().setReadiness(this.ready);
            } else {
                this.table.getWrapper().getPlayer2().setReadiness(this.ready);
            }
        }
    }

    public static class ToClient extends MessageReadyChessPlayer {
        @SuppressWarnings("unused")
        public ToClient() {
            super();
        }

        public ToClient(TileChessTable table, boolean p1, boolean ready) {
            super(table, p1, ready);
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

    public static class ToServer extends MessageReadyChessPlayer {
        @SuppressWarnings("unused")
        public ToServer() {
            super();
        }

        public ToServer(TileChessTable table, boolean p1, boolean ready) {
            super(table, p1, ready);
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_SERVER;
        }
    }


}
