package com.infinityraider.miney_games.content.chess;

import com.infinityraider.miney_games.content.ModContainers;
import com.infinityraider.miney_games.core.ContainerMineyGame;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContainerChessTable extends ContainerMineyGame<TileChessTable, ChessGameWrapper> {
    protected ContainerChessTable(int id, Inventory inventory, @Nullable BlockEntity tile) {
        super(ModContainers.getInstance().CHESS_TABLE_MENU_TYPE.get(),
                id,
                tile instanceof TileChessTable ? (TileChessTable) tile : null,
                inventory, 8, 161);
    }

    public ContainerChessTable(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, inventory.player.getLevel().getBlockEntity(pos));
    }

    public static class WagerSlot extends Slot {
        public WagerSlot(Container pContainer, int pIndex, int pX, int pY) {
            super(pContainer, pIndex, pX, pY);
        }
    }


}
