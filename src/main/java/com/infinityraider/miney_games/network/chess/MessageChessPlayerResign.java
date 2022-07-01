package com.infinityraider.miney_games.network.chess;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public abstract class MessageChessPlayerResign extends MessageBase {
    private TileChessTable table;
    private boolean p1;

    public MessageChessPlayerResign() {
        super();
    }

    private MessageChessPlayerResign(TileChessTable table, boolean p1) {
        this();
        this.table = table;
        this.p1 = p1;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.table != null) {
            this.table.getWrapper().resign(this.p1);
            MineyGames.instance.proxy().updateMineyGameGui();
        }
    }

    public static class ToClient extends MessageChessPlayerResign {
        @SuppressWarnings("unused")
        public ToClient() {
            super();
        }

        public ToClient(TileChessTable table, boolean p1) {
            super(table, p1);
            this.sendToAll();
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_CLIENT;
        }
    }

    public static class ToServer extends MessageChessPlayerResign {
        @SuppressWarnings("unused")
        public ToServer() {
            super();
        }

        public ToServer(TileChessTable table, boolean p1) {
            super(table, p1);
            this.sendToServer();
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_SERVER;
        }
    }
}
