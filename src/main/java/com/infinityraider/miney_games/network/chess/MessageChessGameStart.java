package com.infinityraider.miney_games.network.chess;

import com.infinityraider.miney_games.content.chess.TileChessTable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public abstract class MessageChessGameStart extends MessageChessBase {
    public MessageChessGameStart() {
        super();
    }

    private MessageChessGameStart(TileChessTable table) {
        super(table);
    }

    @Override
    protected boolean handleMessage(NetworkEvent.Context ctx) {
        this.getTable().getWrapper().startGame();
        return true;
    }

    public static class ToClient extends MessageChessGameStart {
        @SuppressWarnings("unused")
        public ToClient() {
            super();
        }

        public ToClient(TileChessTable table) {
            super(table);
            this.sendToAll();
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_CLIENT;
        }
    }

    public static class ToServer extends MessageChessGameStart {
        @SuppressWarnings("unused")
        public ToServer() {
            super();
        }

        public ToServer(TileChessTable table) {
            super(table);
            this.sendToServer();
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_SERVER;
        }
    }


}
