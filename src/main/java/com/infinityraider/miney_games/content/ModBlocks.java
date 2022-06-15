package com.infinityraider.miney_games.content;

import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;
import com.infinityraider.miney_games.content.chess.BlockChessTable;
import com.infinityraider.miney_games.content.poker.BlockPokerTable;
import com.infinityraider.miney_games.content.pool.BlockPoolTable;

public final class ModBlocks extends ModContentRegistry {
    private static final ModBlocks INSTANCE = new ModBlocks();

    public static ModBlocks getInstance() {
        return INSTANCE;
    }

    public final RegistryInitializer<BlockChessTable> CHESS_TABLE_BLOCK;
    public final RegistryInitializer<BlockPokerTable> POKER_TABLE_BLOCK;
    public final RegistryInitializer<BlockPoolTable> POOL_TABLE_BLOCK;

    private ModBlocks() {
        this.CHESS_TABLE_BLOCK = this.block(BlockChessTable::new);
        this.POKER_TABLE_BLOCK = this.block(BlockPokerTable::new);
        this.POOL_TABLE_BLOCK = this.block(BlockPoolTable::new);
    }
}
