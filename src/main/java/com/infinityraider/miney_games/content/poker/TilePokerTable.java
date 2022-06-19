package com.infinityraider.miney_games.content.poker;

import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.miney_games.client.render.PokerTableRenderer;
import com.infinityraider.miney_games.content.ModTiles;
import com.infinityraider.miney_games.core.TileMineyGame;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TilePokerTable extends TileMineyGame<BlockPokerTable, PokerGameWrapper> {
    private final PokerGameWrapper game;

    public TilePokerTable(BlockPos pos, BlockState state) {
        super(ModTiles.getInstance().POKER_TABLE_TILE.get(), pos, state);
        this.game = new PokerGameWrapper();
    }

    public static TilePokerTable.RenderFactory createRenderFactory() {
        return new TilePokerTable.RenderFactory();
    }

    @Override
    protected PokerGameWrapper getWrapper() {
        return this.game;
    }

    private static class RenderFactory implements InfinityTileEntityType.IRenderFactory<TilePokerTable> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        public PokerTableRenderer createRenderer() {
            return new PokerTableRenderer();
        }
    }
}
