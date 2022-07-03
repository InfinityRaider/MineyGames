package com.infinityraider.miney_games.network.chess;

import com.infinityraider.miney_games.content.chess.TileChessTable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public abstract class MessageChessPlayerReady extends MessageChessBase {
    private boolean p1;
    private boolean ready;

    public MessageChessPlayerReady() {
        super();
    }

    private MessageChessPlayerReady(TileChessTable table, boolean p1, boolean ready) {
        super(table);
        this.p1 = p1;
        this.ready = ready;
    }

    @Override
    protected boolean handleMessage(NetworkEvent.Context ctx) {
        // safety check
        if(this.verifySender(ctx, this.p1)) {
            if(this.p1) {
                this.getTable().getWrapper().getPlayer1().setReadiness(this.ready);
            } else {
                this.getTable().getWrapper().getPlayer2().setReadiness(this.ready);
            }
            return true;
        }
        return false;
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
