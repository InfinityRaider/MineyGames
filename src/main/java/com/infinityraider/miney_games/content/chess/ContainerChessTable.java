package com.infinityraider.miney_games.content.chess;

import com.infinityraider.infinitylib.container.ContainerMenuBase;
import com.infinityraider.miney_games.content.ModContainers;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContainerChessTable extends ContainerMenuBase {
    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = 0;

    private final ChessGameWrapper game;

    public ContainerChessTable(int id, Inventory inventory, BlockPos pos) {
        super(ModContainers.getInstance().CHESS_TABLE_MENU_TYPE.get(), id, inventory, OFFSET_X, OFFSET_Y);
        BlockEntity tile = inventory.player.getLevel().getBlockEntity(pos);
        this.game = tile instanceof TileChessTable
                ? ((TileChessTable) tile).getWrapper()
                : null;
    }

    public boolean isValid() {
        return this.getGame() != null;
    }

    @Nullable
    public ChessGameWrapper getGame() {
        return this.game;
    }
}
