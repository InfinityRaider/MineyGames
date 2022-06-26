package com.infinityraider.miney_games.core;

import com.infinityraider.infinitylib.container.ContainerMenuBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContainerMineyGame extends ContainerMenuBase {
    public ContainerMineyGame(@Nullable MenuType<?> type, int id, Inventory inventory, int xOffset, int yOffset) {
        super(type, id, inventory, xOffset, yOffset);
    }


}
