package com.infinityraider.miney_games.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public abstract class GameWrapper{
    protected abstract void tick();

    protected abstract InteractionResult onRightClick(Player player, InteractionHand hand, BlockHitResult hit);

    protected abstract void writeToNBT(CompoundTag tag);

    protected abstract void readFromNBT(CompoundTag tag);
}
