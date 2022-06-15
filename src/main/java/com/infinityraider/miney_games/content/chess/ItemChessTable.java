package com.infinityraider.miney_games.content.chess;

import com.infinityraider.infinitylib.item.BlockItemBase;
import com.infinityraider.miney_games.content.ModContent;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.CreativeModeTab;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemChessTable extends BlockItemBase {
    public ItemChessTable() {
        super(ModContent.getInstance().CHESS_TABLE_BLOCK.get(), new Properties()
                .tab(CreativeModeTab.TAB_DECORATIONS)
                .stacksTo(64)
        );
    }
}
