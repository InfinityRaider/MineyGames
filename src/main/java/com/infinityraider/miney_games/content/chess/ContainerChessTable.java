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
    private final ChessGameWrapper game;

    protected ContainerChessTable(int id, Inventory inventory, @Nullable BlockEntity tile) {
        super(ModContainers.getInstance().CHESS_TABLE_MENU_TYPE.get(), id, inventory, 8, 84);
        this.game = tile instanceof TileChessTable
                ? ((TileChessTable) tile).getWrapper()
                : null;
    }

    public ContainerChessTable(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, inventory.player.getLevel().getBlockEntity(pos));
    }

    public boolean isValid() {
        return this.getGame() != null;
    }

    @Nullable
    public ChessGameWrapper getGame() {
        return this.game;
    }
}
