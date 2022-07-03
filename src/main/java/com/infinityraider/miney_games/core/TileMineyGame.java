package com.infinityraider.miney_games.core;

import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.miney_games.reference.Names;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class TileMineyGame<B extends BlockMineyGame<?>, W extends GameWrapper> extends TileEntityBase {
    public TileMineyGame(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract W getWrapper();

    @Override
    public final void tick() {
        this.getWrapper().tick();
        this.onTick();
    }

    protected void onTick() {}

    public final InteractionResult onRightClick(Player player, InteractionHand hand, BlockHitResult hit) {
        return this.getWrapper().onRightClick(player, hand, hit);
    }

    @SuppressWarnings("unchecked")
    public B getBlock() {
        return (B) this.getBlockState().getBlock();
    }

    public boolean isMainTile() {
        return this.getAbsX() == 0 && this.getAbsY() == 0;
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

    public int xRelToAbs(int xRel, int yRel) {
        return this.getOrientation().xRelToAbs(xRel, yRel, this.getSize());
    }

    public int yRelToAbs(int xRel, int yRel) {
        return this.getOrientation().yRelToAbs(xRel, yRel, this.getSize());
    }

    public int xAbsToRel(int xAbs, int yAbs) {
        return this.getOrientation().xAbsToRel(xAbs, yAbs, this.getSize());
    }

    public int yAbsToRel(int xAbs, int yAbs) {
        return this.getOrientation().yAbsToRel(xAbs, yAbs, this.getSize());
    }

    public double xRelToAbs(double xRel, double yRel) {
        return this.getOrientation().xRelToAbs(xRel, yRel, this.getSize());
    }

    public double yRelToAbs(double xRel, double yRel) {
        return this.getOrientation().yRelToAbs(xRel, yRel, this.getSize());
    }

    public double xAbsToRel(double xAbs, double yAbs) {
        return this.getOrientation().xAbsToRel(xAbs, yAbs, this.getSize());
    }

    public double yAbsToRel(double xAbs, double yAbs) {
        return this.getOrientation().yAbsToRel(xAbs, yAbs, this.getSize());
    }

    @Override
    protected final void writeTileNBT(CompoundTag tag) {
        this.getWrapper().writeToNBT(tag);
        CompoundTag additional = new CompoundTag();
        this.writeAdditionalNBT(tag);
        tag.put(Names.NBT.ADDITIONAL, additional);
    }

    @Override
    protected final void readTileNBT(CompoundTag tag) {
        this.getWrapper().readFromNBT(tag);
        this.readAdditionalNBT(tag.getCompound(Names.NBT.ADDITIONAL));
    }

    protected void writeAdditionalNBT(CompoundTag tag) {}

    protected void readAdditionalNBT(CompoundTag tag) {}
}
