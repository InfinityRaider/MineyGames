package com.infinityraider.miney_games.content.chess;

import com.infinityraider.miney_games.content.ModContainers;
import com.infinityraider.miney_games.core.ContainerMineyGame;
import com.infinityraider.miney_games.core.Wager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContainerChessTable extends ContainerMineyGame<TileChessTable, ChessGameWrapper> {
    protected ContainerChessTable(int id, Inventory inventory, @Nullable TileChessTable tile) {
        super(ModContainers.getInstance().CHESS_TABLE_MENU_TYPE.get(), id, tile, inventory, 8, 161);
        // wagers
        if(this.getTile() != null) {
            for(int i = 0; i < ChessGameWrapper.WAGER_SIZE; i++) {
                this.addSlot(new WagerSlotHandler(inventory.player, this.getTile(), true, i, 26, 105));
            }
            for(int i = 0; i < ChessGameWrapper.WAGER_SIZE; i++) {
                this.addSlot(new WagerSlotHandler(inventory.player, this.getTile(), false, i, 26, 125));
            }
        }
    }

    protected ContainerChessTable(int id, Inventory inventory, @Nullable BlockEntity tile) {
        this(id, inventory, tile instanceof TileChessTable ? (TileChessTable) tile : null);
    }

    public ContainerChessTable(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, inventory.player.getLevel().getBlockEntity(pos));
    }

    public static class WagerSlot extends Slot {
        public WagerSlot(Container pContainer, int pIndex, int pX, int pY) {
            super(pContainer, pIndex, pX, pY);
        }
    }

    public static class WagerSlotHandler extends SlotItemHandler {
        private final Player containerOwner;
        private final TileChessTable tile;
        private final boolean p1;

        public WagerSlotHandler(Player containerOwner, TileChessTable tile, boolean p1, int index, int xPosition, int yPosition) {
            super((p1? tile.getWrapper().getPlayer1() : tile.getWrapper().getPlayer2()).getWagers(), index, xPosition, yPosition);
            this.containerOwner = containerOwner;
            this.tile = tile;
            this.p1 = p1;
        }

        public Player getContainerOwner() {
            return this.containerOwner;
        }

        public TileChessTable getTile() {
            return this.tile;
        }

        public boolean isPlayer1() {
            return this.p1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Wager<ChessGameWrapper> getItemHandler() {
            return (Wager<ChessGameWrapper>) super.getItemHandler();
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            // TODO
            return super.mayPlace(stack);
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            // TODO
            return super.mayPickup(playerIn);
        }

        @Override
        public boolean isActive() {
            boolean owner = this.getItemHandler().isOwner(this.containerOwner);
            return owner && this.getItemHandler().getStatus().canClaim(owner, true);    // TODO check if the player is the winner
        }
    }
}
