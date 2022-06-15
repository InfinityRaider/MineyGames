package com.infinityraider.miney_games.content.pool;

import com.infinityraider.infinitylib.item.BlockItemBase;
import com.infinityraider.miney_games.content.ModBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.CreativeModeTab;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemPoolTable extends BlockItemBase {
    public ItemPoolTable() {
        super(ModBlocks.getInstance().POOL_TABLE_BLOCK.get(), new Properties()
                .tab(CreativeModeTab.TAB_DECORATIONS)
                .stacksTo(64)
        );
    }
}
