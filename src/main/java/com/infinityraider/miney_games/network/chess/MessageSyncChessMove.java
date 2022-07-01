package com.infinityraider.miney_games.network.chess;

import com.infinityraider.miney_games.content.chess.TileChessTable;
import com.infinityraider.miney_games.games.chess.ChessGame;
import com.infinityraider.miney_games.games.chess.ChessMove;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public class MessageSyncChessMove extends MessageChessGameBase {
    private TileChessTable table;
    private MessageChessGameBase.SyncMove move;

    public MessageSyncChessMove() {
        super();
    }

    public MessageSyncChessMove(TileChessTable tile, ChessMove move) {
        this();
        this.table = tile;
        this.move = new MessageChessGameBase.SyncMove(move);
        this.sendToAll();
    }

    public Optional<ChessMove> getMove(ChessGame game) {
        return this.move.getMove(game);
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.table != null && this.move != null) {
            this.table.getWrapper().onSyncMessage(this);
        }
    }
}
