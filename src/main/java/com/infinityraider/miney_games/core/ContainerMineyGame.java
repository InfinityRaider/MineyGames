package com.infinityraider.miney_games.core;

import com.infinityraider.infinitylib.container.ContainerMenuBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContainerMineyGame<T extends TileMineyGame<?, W>, W extends GameWrapper<?>> extends ContainerMenuBase {
    @Nullable
    private final T tile;

    public ContainerMineyGame(@Nullable MenuType<?> type, int id, @Nullable T tile, Inventory inventory, int xOffset, int yOffset) {
        super(type, id, inventory, xOffset, yOffset);
        this.tile = tile;
    }

    public boolean isValid() {
        T tile = this.getTile();
        return tile != null && !tile.isRemoved();
    }

    @Nullable
    public T getTile() {
        return this.tile;
    }

    @Nullable
    public W getGame() {
        T tile = this.getTile();
        return tile == null ? null : tile.getWrapper();
    }


}
