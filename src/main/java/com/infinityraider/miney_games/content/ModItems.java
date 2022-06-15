package com.infinityraider.miney_games.content;

import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;
import com.infinityraider.miney_games.content.chess.ItemChessTable;
import com.infinityraider.miney_games.content.poker.ItemPokerTable;
import com.infinityraider.miney_games.content.pool.ItemPoolTable;

public class ModItems extends ModContentRegistry {
    private static final ModItems INSTANCE = new ModItems();

    public static ModItems getInstance() {
        return INSTANCE;
    }

    public final RegistryInitializer<ItemChessTable> CHESS_TABLE_ITEM;
    public final RegistryInitializer<ItemPokerTable> POKER_TABLE_ITEM;
    public final RegistryInitializer<ItemPoolTable> POOL_TABLE_ITEM;

    private ModItems() {
        this.CHESS_TABLE_ITEM = this.item(ItemChessTable::new);
        this.POKER_TABLE_ITEM = this.item(ItemPokerTable::new);
        this.POOL_TABLE_ITEM = this.item(ItemPoolTable::new);
    }
}
