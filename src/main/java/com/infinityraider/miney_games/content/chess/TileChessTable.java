package com.infinityraider.miney_games.content.chess;

import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.utility.RayTraceHelper;
import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.client.render.ChessTableRenderer;
import com.infinityraider.miney_games.content.ModTiles;
import com.infinityraider.miney_games.core.TileMineyGame;
import com.infinityraider.miney_games.reference.Names;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
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
public class TileChessTable extends TileMineyGame<BlockChessTable, ChessGameWrapper> implements MenuProvider {
    public static final TranslatableComponent NAME = new TranslatableComponent(MineyGames.instance.getModId() + ".gui." + Names.CHESS_TABLE);
    public static final double RAY_TRACE_RANGE = 7.0;

    private final ChessGameWrapper game;

    public TileChessTable(BlockPos pos, BlockState state) {
        super(ModTiles.getInstance().CHESS_TABLE_TILE.get(), pos, state);
        this.game = new ChessGameWrapper(this);
    }

    @Override
    public ChessGameWrapper getWrapper() {
        if(this.getLevel() == null) {
            return this.game;
        }
        if(this.isMainTile()) {
            return this.game;
        }
        return this.getBlock().getMainTile(this.getLevel(), this.getBlockPos(), this.getBlockState())
                .map(TileChessTable::getWrapper)
                .orElse(this.game);
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
        return this.getChessSquareIndex(this.xAbsToRel(rel.x(), rel.y()));
    }

    public int getChessSquareIndexRelY(Vec3 rel) {
        return this.getChessSquareIndex(this.yAbsToRel(rel.x(), rel.y()));
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

    public int iAbsToRel(int iAbs, int jAbs) {
        return this.getOrientation().xAbsToRel(iAbs, jAbs, BlockChessTable.Size.SQUARES, BlockChessTable.Size.SQUARES);
    }

    public int jAbsToRel(int iAbs, int jAbs) {
        return this.getOrientation().yAbsToRel(iAbs, jAbs, BlockChessTable.Size.SQUARES, BlockChessTable.Size.SQUARES);
    }

    @Override
    public Component getDisplayName() {
        return NAME;
    }

    @Nullable
    @Override
    public ContainerChessTable createMenu(int id, Inventory inv, Player player) {
        return new ContainerChessTable(id, inv, this);
    }

    private static class RenderFactory implements InfinityTileEntityType.IRenderFactory<TileChessTable> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        public ChessTableRenderer createRenderer() {
            return new ChessTableRenderer();
        }
    }
}
