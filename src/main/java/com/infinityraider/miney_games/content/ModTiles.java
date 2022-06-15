package com.infinityraider.miney_games.content;

import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import com.infinityraider.miney_games.content.poker.TilePokerTable;
import com.infinityraider.miney_games.content.pool.TilePoolTable;
import com.infinityraider.miney_games.reference.Names;

public final class ModTiles extends ModContentRegistry {
    private static final ModTiles INSTANCE = new ModTiles();

    public static ModTiles getInstance() {
        return INSTANCE;
    }

    public final RegistryInitializer<InfinityTileEntityType<TileChessTable>> CHESS_TABLE_TILE;
    public final RegistryInitializer<InfinityTileEntityType<TilePokerTable>> POKER_TABLE_TILE;
    public final RegistryInitializer<InfinityTileEntityType<TilePoolTable>> POOL_TABLE_TILE;

    private ModTiles() {
        this.CHESS_TABLE_TILE = this.blockEntity(() ->
                InfinityTileEntityType.builder(Names.CHESS_TABLE, TileChessTable::new)
                        .addBlock(ModBlocks.getInstance().CHESS_TABLE_BLOCK.get())
                        .setTicking()
                        .setRenderFactory(TileChessTable.createRenderFactory())
                        .build()
        );

        this.POKER_TABLE_TILE = this.blockEntity(() ->
                InfinityTileEntityType.builder(Names.POKER_TABLE, TilePokerTable::new)
                        .addBlock(ModBlocks.getInstance().POKER_TABLE_BLOCK.get())
                        .setTicking()
                        .setRenderFactory(TilePokerTable.createRenderFactory())
                        .build()
        );

        this.POOL_TABLE_TILE = this.blockEntity(() ->
                InfinityTileEntityType.builder(Names.POOL_TABLE, TilePoolTable::new)
                        .addBlock(ModBlocks.getInstance().POOL_TABLE_BLOCK.get())
                        .setTicking()
                        .setRenderFactory(TilePoolTable.createRenderFactory())
                        .build()
        );
    }
}
