package com.infinityraider.miney_games.content.poker;

import com.infinityraider.miney_games.core.GameWrapper;
import com.infinityraider.miney_games.games.poker.PokerGame;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class PokerGameWrapper extends GameWrapper {
    private PokerGame game;

    public Optional<PokerGame> getGame() {
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
