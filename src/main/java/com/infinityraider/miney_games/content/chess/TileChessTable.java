package com.infinityraider.miney_games.content.chess;

import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.utility.RayTraceHelper;
import com.infinityraider.miney_games.client.render.ChessTableRenderer;
import com.infinityraider.miney_games.content.ModTiles;
import com.infinityraider.miney_games.core.TileMineyGame;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileChessTable extends TileMineyGame<BlockChessTable, ChessGameWrapper> {
    private static final double RAY_TRACE_RANGE = 7.0;

    private final ChessGameWrapper game;

    public TileChessTable(BlockPos pos, BlockState state) {
        super(ModTiles.getInstance().CHESS_TABLE_TILE.get(), pos, state);
        this.game = new ChessGameWrapper(this);
    }

    @Override
    public ChessGameWrapper getWrapper() {
        return this.game;
    }

    @Override
    public BlockChessTable.Size getSize() {
        return this.getBlock().getSize(this.getBlockState());
    }

    public Optional<HitResult> rayTrace(Player player) {
        return RayTraceHelper.getTargetBlock(player, RAY_TRACE_RANGE);
    }

    public Vec3 offsetAbs(Vec3 abs) {
        BlockPos pos = this.getBlockPos().offset(-this.getAbsX(), 0, -this.getAbsY());
        return abs.add(-pos.getX(), -pos.getY(), -pos.getZ());
    }

    public int getChessSquareIndexAbsX(Vec3 abs) {
        return this.getChessSquareIndex(abs.x());
    }

    public int getChessSquareIndexAbsY(Vec3 abs) {
        return this.getChessSquareIndex(abs.z());
    }

    public int getChessSquareIndexRelX(Vec3 rel) {
        return this.getChessSquareIndex(this.getOrientation().xAbsToRel(rel.x(), rel.y(), this.getSize()));
    }

    public int getChessSquareIndexRelY(Vec3 rel) {
        return this.getChessSquareIndex(this.getOrientation().yAbsToRel(rel.x(), rel.y(), this.getSize()));
    }

    public int getChessSquareIndex(double v) {
        return this.getSize().getSquareIndex(v);
    }

    public double getChessSquareMin(int square) {
        return this.getSize().getSquareMin(square);
    }

    public double getChessSquareMax(int square) {
        return this.getSize().getSquareMax(square);
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
