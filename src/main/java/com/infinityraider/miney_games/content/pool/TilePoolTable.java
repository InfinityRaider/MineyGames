package com.infinityraider.miney_games.content.pool;

import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.miney_games.client.render.PoolTableRenderer;
import com.infinityraider.miney_games.content.ModTiles;
import com.infinityraider.miney_games.core.TileMineyGame;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TilePoolTable extends TileMineyGame<BlockPoolTable> {
    public TilePoolTable(BlockPos pos, BlockState state) {
        super(ModTiles.getInstance().POOL_TABLE_TILE.get(), pos, state);
    }

    @Override
    public void tick() {

    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundTag tag) {

    }

    @Override
    protected void readTileNBT(@Nonnull CompoundTag tag) {

    }

    public static TilePoolTable.RenderFactory createRenderFactory() {
        return new TilePoolTable.RenderFactory();
    }

    private static class RenderFactory implements InfinityTileEntityType.IRenderFactory<TilePoolTable> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        public PoolTableRenderer createRenderer() {
            return new PoolTableRenderer();
        }
    }
}
