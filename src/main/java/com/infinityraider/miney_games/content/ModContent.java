package com.infinityraider.miney_games.content;

import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;
import com.infinityraider.miney_games.content.chess.BlockChessTable;
import com.infinityraider.miney_games.content.chess.ItemChessTable;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import com.infinityraider.miney_games.reference.Names;

public final class ModContent extends ModContentRegistry {
    private static final ModContent INSTANCE = new ModContent();

    public static ModContent getInstance() {
        return INSTANCE;
    }

    // chess
    public final RegistryInitializer<BlockChessTable> CHESS_TABLE_BLOCK;
    public final RegistryInitializer<ItemChessTable> CHESS_TABLE_ITEM;
    public final RegistryInitializer<InfinityTileEntityType<TileChessTable>> CHESS_TABLE_TILE;

    // poker

    // pool


    private ModContent() {
        this.CHESS_TABLE_BLOCK = this.block(BlockChessTable::new);
        this.CHESS_TABLE_ITEM = this.item(ItemChessTable::new);
        this.CHESS_TABLE_TILE = this.blockEntity(() ->
                InfinityTileEntityType.builder(Names.CHESS_TABLE, TileChessTable::new)
                        .addBlock(this.CHESS_TABLE_BLOCK.get())
                        .setTicking()
                        .setRenderFactory(TileChessTable.createRenderFactory())
                        .build()
        );
    }

}
