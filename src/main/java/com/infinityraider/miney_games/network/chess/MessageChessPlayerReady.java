package com.infinityraider.miney_games.network.chess;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public abstract class MessageChessPlayerReady extends MessageBase {
    private TileChessTable table;
    private boolean p1;
    private boolean ready;

    public MessageChessPlayerReady() {
        super();
    }

    private MessageChessPlayerReady(TileChessTable table, boolean p1, boolean ready) {
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
            MineyGames.instance.proxy().updateMineyGameGui();
        }
    }

    public static class ToClient extends MessageChessPlayerReady {
        @SuppressWarnings("unused")
        public ToClient() {
            super();
        }

        public ToClient(TileChessTable table, boolean p1, boolean ready) {
            super(table, p1, ready);
            this.sendToAll();
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_CLIENT;
        }
    }

    public static class ToServer extends MessageChessPlayerReady {
        @SuppressWarnings("unused")
        public ToServer() {
            super();
        }

        public ToServer(TileChessTable table, boolean p1, boolean ready) {
            super(table, p1, ready);
            this.sendToServer();
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_SERVER;
        }
    }


}
