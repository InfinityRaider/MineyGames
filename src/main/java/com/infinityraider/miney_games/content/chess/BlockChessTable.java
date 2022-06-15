package com.infinityraider.miney_games.content.chess;

import com.google.common.collect.ImmutableList;
import com.infinityraider.miney_games.core.BlockMineyGame;
import com.infinityraider.miney_games.core.MineyGameSize;
import com.infinityraider.miney_games.reference.Names;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockChessTable extends BlockMineyGame<TileChessTable> {
    public static final List<MineyGameSize> SIZES = ImmutableList.of(
            new MineyGameSize(1, 1),
            new MineyGameSize(2, 2),
            new MineyGameSize(3, 3),
            new MineyGameSize(4, 4)
    );

    private static final BiFunction<BlockPos, BlockState, TileChessTable> TILE_FACTORY = TileChessTable::new;

    public BlockChessTable() {
        super(Names.CHESS_TABLE, Properties.of(Material.STONE)
                .strength(1.5F, 6.0F)
                .noOcclusion()
        );
    }

    @Override
    public BiFunction<BlockPos, BlockState, TileChessTable> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Override
    protected List<MineyGameSize> getAllSizes() {
        return SIZES;
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        MineyGameSize size = this.getSize(state);
        return switch (size.getWidth()) {
            case 1 -> HitBoxes.getSize1Shape();
            case 2 -> HitBoxes.getSize2Shape(this.getX(state), this.getY(state));
            case 3 -> HitBoxes.getSize3Shape(this.getX(state), this.getY(state));
            case 4 -> HitBoxes.getSize4Shape(this.getX(state), this.getY(state));
            default -> Shapes.block();
        };
    }

    public static class HitBoxes {
        private static final VoxelShape SIZE_1 = Stream.of(
                Block.box(2, 0, 2, 14, 2, 14),
                Block.box(5, 2, 5, 11, 8, 11),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        public static VoxelShape getSize1Shape() {
            return SIZE_1;
        }

        public static VoxelShape getSize2Shape(int x, int y) {
            return Shapes.block();  // TODO
        }

        public static VoxelShape getSize3Shape(int x, int y) {
            return Shapes.block();  // TODO
        }

        public static VoxelShape getSize4Shape(int x, int y) {
            return Shapes.block();  // TODO
        }
    }
}
