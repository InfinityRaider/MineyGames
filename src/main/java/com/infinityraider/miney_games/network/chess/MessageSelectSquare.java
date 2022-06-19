package com.infinityraider.miney_games.network.chess;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import com.infinityraider.miney_games.games.chess.ChessBoard;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageSelectSquare extends MessageBase {
    private TileChessTable table;
    private int x;
    private int y;

    public MessageSelectSquare() {
        super();
    }

    public MessageSelectSquare(TileChessTable table, ChessBoard.Square square) {
        this.table = table;
        this.x = square.getX();
        this.y = square.getY();
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.table != null) {

        }
    }
}
