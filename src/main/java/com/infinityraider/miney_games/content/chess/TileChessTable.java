package com.infinityraider.miney_games.content.chess;

import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.miney_games.client.render.ChessTableRenderer;
import com.infinityraider.miney_games.content.ModContent;
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
public class TileChessTable extends TileEntityBase {
    public TileChessTable(BlockPos pos, BlockState state) {
        super(ModContent.getInstance().CHESS_TABLE_TILE.get(), pos, state);
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

    public static RenderFactory createRenderFactory() {
        return new RenderFactory();
    }

    private static class RenderFactory implements InfinityTileEntityType.IRenderFactory<TileChessTable> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        public ChessTableRenderer createRenderer() {
            return new ChessTableRenderer();
        }
    }
}
