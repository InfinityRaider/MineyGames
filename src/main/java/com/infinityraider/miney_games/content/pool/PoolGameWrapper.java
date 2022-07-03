package com.infinityraider.miney_games.content.pool;

import com.infinityraider.miney_games.core.GameWrapper;
import com.infinityraider.miney_games.games.pool.PoolGame;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class PoolGameWrapper extends GameWrapper {
    private PoolGame game;

    protected PoolGameWrapper() {

    }

    public Optional<PoolGame> getGame() {
        return Optional.ofNullable(this.game);
    }

    @Override
    protected void tick() {

    }

    @Override
    protected InteractionResult onRightClick(Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    @Override
    protected void writeToNBT(CompoundTag tag) {

    }

    @Override
    protected void readFromNBT(CompoundTag tag) {

    }
}
