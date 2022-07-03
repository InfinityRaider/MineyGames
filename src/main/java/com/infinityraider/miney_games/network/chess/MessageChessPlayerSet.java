package com.infinityraider.miney_games.network.chess;

import com.infinityraider.miney_games.content.chess.TileChessTable;
import net.minecraft.Util;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public abstract class MessageChessPlayerSet extends MessageChessBase {
    private boolean p1;
    private UUID id;

    public MessageChessPlayerSet() {
        super();
    }

    private MessageChessPlayerSet(TileChessTable table, boolean p1) {
        this(table, p1, Util.NIL_UUID);
    }

    private MessageChessPlayerSet(TileChessTable table, boolean p1, UUID id) {
        super(table);
        this.p1 = p1;
        this.id = id;
    }

    @Override
    protected boolean handleMessage(NetworkEvent.Context ctx) {
        if(this.p1) {
            this.getTable().getWrapper().getPlayer1().setPlayer(this.id);
        } else {
            this.getTable().getWrapper().getPlayer2().setPlayer(this.id);
        }
        return true;
    }

    public static class ToClient extends MessageChessPlayerSet {
        @SuppressWarnings("unused")
        public ToClient() {
            super();
        }

        public ToClient(TileChessTable table, boolean p1) {
            super(table, p1);
            this.sendToAll();
        }

        public ToClient(TileChessTable table, boolean p1, UUID id) {
            super(table, p1, id);
            this.sendToAll();
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_CLIENT;
        }
    }

    public static class ToServer extends MessageChessPlayerSet {
        @SuppressWarnings("unused")
        public ToServer() {
            super();
        }

        public ToServer(TileChessTable table, boolean p1) {
            super(table, p1);
            this.sendToServer();
        }

        public ToServer(TileChessTable table, boolean p1, UUID id) {
            super(table, p1, id);
            this.sendToServer();
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_SERVER;
        }
    }


}
