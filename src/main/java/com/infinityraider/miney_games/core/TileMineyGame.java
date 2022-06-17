package com.infinityraider.miney_games.core;

import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TileMineyGame<B extends BlockMineyGame<?>> extends TileEntityBase {
    public TileMineyGame(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @SuppressWarnings("unchecked")
    public B getBlock() {
        return (B) this.getBlockState().getBlock();
    }

    public boolean isFunctional() {
        return this.getBlock().isFunctional(this.getBlockState());
    }

    public MineyGameSize getSize() {
        return this.getBlock().getSize(this.getBlockState());
    }

    public int getWidth() {
        return this.getBlock().getWidth(this.getBlockState());
    }

    public int getDepth() {
        return this.getBlock().getDepth(this.getBlockState());
    }

    public int getRelX() {
        return this.getBlock().getRelX(this.getBlockState());
    }

    public int getRelY() {
        return this.getBlock().getRelY(this.getBlockState());
    }

    public int getAbsX() {
        return this.getBlock().getAbsX(this.getBlockState());
    }

    public int getAbsY() {
        return this.getBlock().getAbsY(this.getBlockState());
    }

    public Direction getDirection() {
        return this.getBlock().getDirection(this.getBlockState());
    }

    public Orientation getOrientation() {
        return this.getBlock().getOrientation(this.getBlockState());
    }
}
