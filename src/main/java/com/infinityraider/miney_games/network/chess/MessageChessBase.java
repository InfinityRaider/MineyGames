package com.infinityraider.miney_games.network.chess;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public abstract class MessageChessBase extends MessageBase {
    private TileChessTable table;

    public MessageChessBase() {
        super();
    }

    protected MessageChessBase(TileChessTable table) {
        this();
        this.table = table;
    }

    protected TileChessTable getTable() {
        return this.table;
    }

    @Override
    protected final void processMessage(NetworkEvent.Context ctx) {
        if(this.table != null && this.handleMessage(ctx)) {
            MineyGames.instance.proxy().updateMineyGameGui();
        }
    }

    protected boolean verifySender(NetworkEvent.Context ctx, boolean p1) {
        if(this.getMessageDirection() == NetworkDirection.PLAY_TO_SERVER) {
            UUID id = p1
                    ? this.getTable().getWrapper().getPlayer1().getPlayerId()
                    : this.getTable().getWrapper().getPlayer2().getPlayerId();
            return ctx.getSender() != null && ctx.getSender().getUUID().equals(id);
        }
        return true;
    }

    protected abstract boolean handleMessage(NetworkEvent.Context ctx);
}
