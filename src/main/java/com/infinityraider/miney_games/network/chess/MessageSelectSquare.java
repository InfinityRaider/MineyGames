package com.infinityraider.miney_games.network.chess;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.miney_games.MineyGames;
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

    public MessageSelectSquare(TileChessTable table) {
        this(table, -1, -1);
    }

    public MessageSelectSquare(TileChessTable table, ChessBoard.Square square) {
        this(table, square.getX(), square.getY());
    }

    public MessageSelectSquare(TileChessTable table, int x, int y) {
        this();
        this.table = table;
        this.x = x;
        this.y = y;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if (this.table != null) {
            this.table.getWrapper().asParticipant(MineyGames.instance.getClientPlayer())
                    .ifPresent(p -> {
                        if (this.x < 0 || this.y < 0) {
                            p.deselectSquare();
                        } else {
                            if(!p.selectSquare(this.x, this.y)) {
                                MineyGames.instance.getLogger().error("Failed to select square on client: [" + this.x + ", " + this.y + "]" );
                            }
                        }
                    });
        }
    }
}
