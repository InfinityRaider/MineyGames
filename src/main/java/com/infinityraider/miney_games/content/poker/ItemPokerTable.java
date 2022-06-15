package com.infinityraider.miney_games.content.poker;

import com.infinityraider.infinitylib.item.BlockItemBase;
import com.infinityraider.miney_games.content.ModBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.CreativeModeTab;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemPokerTable extends BlockItemBase {
    public ItemPokerTable() {
        super(ModBlocks.getInstance().POKER_TABLE_BLOCK.get(), new Properties()
                .tab(CreativeModeTab.TAB_DECORATIONS)
                .stacksTo(64)
        );
    }
}
