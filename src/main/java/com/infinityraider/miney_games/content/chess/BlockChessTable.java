package com.infinityraider.miney_games.content.chess;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
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
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockChessTable extends BlockMineyGame<TileChessTable> {
    public static final List<MineyGameSize> SIZES = ImmutableList.of(
            new Size(1),
            new Size(2),
            new Size(3),
            new Size(4)
    );

    private static final BiFunction<BlockPos, BlockState, TileChessTable> TILE_FACTORY = TileChessTable::new;

    public BlockChessTable() {
        super(Names.CHESS_TABLE, Properties.of(Material.STONE)
                .strength(1.5F, 6.0F)
                .noOcclusion()
        );
    }

    @Override
    public Size getSize(BlockState state) {
        return (Size) super.getSize(state);
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
        return HitBoxes.SHAPES.computeIfAbsent(state, s -> {
            MineyGameSize size = this.getSize(state);
            return switch (size.getWidth()) {
                case 1 -> HitBoxes.getSize1Shape();
                case 2 -> HitBoxes.getSize2Shape(this.getAbsX(state), this.getAbsY(state));
                case 3 -> HitBoxes.calculateSize3Shape(this.getAbsX(state), this.getAbsY(state));
                case 4 -> HitBoxes.calculateSize4Shape(this.getAbsX(state), this.getAbsY(state));
                default -> Shapes.block();
            };
        });
    }

    public static class Size extends MineyGameSize {
        private static final int TEX_SIZE = 96;
        private static final int MARGIN = 4;
        private static final int SQUARES = 8;

        public Size(int width) {
            super(width, width);
        }

        public double getBoardMin() {
            return (0.0 + MARGIN*this.getWidth())/TEX_SIZE;
        }

        public double getBoardMax() {
            return (0.0 + (TEX_SIZE - MARGIN)*this.getWidth())/TEX_SIZE;
        }

        public int getSquareIndex(double x) {
            double min = this.getBoardMin();
            double max = this.getBoardMax();
            double squareSize = (max - min)/SQUARES;
            if(x < min || x > max) {
                // outside of the board
                return -1;
            }
            return (int) ((x - min)/squareSize);
        }

        public double getSquareMin(int square) {
            double min = this.getBoardMin();
            double max = this.getBoardMax();
            double squareSize = (max - min)/SQUARES;
            return min + squareSize*square;
        }

        public double getSquareMax(int square) {
            double min = this.getBoardMin();
            double max = this.getBoardMax();
            double squareSize = (max - min)/SQUARES;
            return min + squareSize*(1 + square);
        }
    }

    public static class HitBoxes {
        private static final Map<BlockState, VoxelShape> SHAPES = Maps.newConcurrentMap();

        private static final VoxelShape SIZE_1 = Stream.of(
                Block.box(2, 0, 2, 14, 2, 14),
                Block.box(5, 2, 5, 11, 8, 11),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_2_X0_Y0 = Stream.of(
                Block.box(4, 0, 4, 16, 2, 16),
                Block.box(9, 2, 9, 16, 8, 16),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_2_X0_Y1 = Stream.of(
                Block.box(4, 0, 0, 16, 2, 12),
                Block.box(9, 2, 0, 16, 8, 7),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_2_X1_Y0 = Stream.of(
                Block.box(0, 0, 4, 12, 2, 16),
                Block.box(0, 2, 9, 7, 8, 16),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_2_X1_Y1 = Stream.of(
                Block.box(0, 0, 0, 12, 2, 12),
                Block.box(0, 2, 0, 7, 8, 7),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_3_X0_Y0 = Stream.of(
                Block.box(6, 0, 6, 16, 2, 16),
                Block.box(13, 2, 13, 16, 8, 16),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_3_X0_Y2 = Stream.of(
                Block.box(6, 0, 0, 16, 2, 10),
                Block.box(13, 2, 0, 16, 8, 3),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_3_X0_EDGE = Stream.of(
                Block.box(6, 0, 0, 16, 2, 16),
                Block.box(13, 2, 0, 16, 8, 16),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_3_X2_Y0 = Stream.of(
                Block.box(0, 0, 6, 10, 2, 16),
                Block.box(0, 2, 13, 3, 8, 16),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_3_X2_Y2 = Stream.of(
                Block.box(0, 0, 0, 10, 2, 10),
                Block.box(0, 2, 0, 3, 8, 3),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_3_X2_EDGE = Stream.of(
                Block.box(0, 0, 0, 10, 2, 16),
                Block.box(0, 2, 0, 3, 8, 16),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_3_Y0_EDGE = Stream.of(
                Block.box(0, 0, 6, 16, 2, 16),
                Block.box(0, 2, 13, 16, 8, 16),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_3_Y2_EDGE = Stream.of(
                Block.box(0, 0, 0, 16, 2, 10),
                Block.box(0, 2, 0, 16, 8, 3),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_4_X0_Y0 = Stream.of(
                Block.box(8, 0, 8, 16, 2, 16),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_4_X0_Y3 = Stream.of(
                Block.box(8, 0, 0, 16, 2, 8),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_4_X0_EDGE = Stream.of(
                Block.box(8, 0, 0, 16, 2, 16),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_4_X3_Y0 = Stream.of(
                Block.box(0, 0, 8, 8, 2, 16),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_4_X3_Y3 = Stream.of(
                Block.box(0, 0, 0, 8, 2, 8),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_4_X3_EDGE = Stream.of(
                Block.box(0, 0, 0, 8, 2, 16),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_4_Y0_EDGE = Stream.of(
                Block.box(0, 0, 8, 16, 2, 16),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape SIZE_4_Y3_EDGE = Stream.of(
                Block.box(0, 0, 0, 16, 2, 8),
                Block.box(0, 8, 0, 16, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final VoxelShape MID = Block.box(0, 0, 0, 16, 12, 16);

        public static VoxelShape getSize1Shape() {
            return SIZE_1;
        }

        public static VoxelShape getSize2Shape(int x, int y) {
            if(x == 0) {
                if(y == 0) {
                    return SIZE_2_X0_Y0;
                } else {
                    return SIZE_2_X0_Y1;
                }
            } else {
                if(y == 0) {
                    return SIZE_2_X1_Y0;
                } else {
                    return SIZE_2_X1_Y1;
                }
            }
        }

        public static VoxelShape calculateSize3Shape(int x, int y) {
            if(x == 0) {
                if(y == 0) {
                    return SIZE_3_X0_Y0;
                } else if(y == 2) {
                    return SIZE_3_X0_Y2;
                } else {
                    return SIZE_3_X0_EDGE;
                }
            } else if(x == 2) {
                if(y == 0) {
                    return SIZE_3_X2_Y0;
                } else if(y == 2) {
                    return SIZE_3_X2_Y2;
                } else {
                    return SIZE_3_X2_EDGE;
                }
            } else {
                if(y == 0) {
                    return SIZE_3_Y0_EDGE;
                } else if(y == 2) {
                    return SIZE_3_Y2_EDGE;
                } else {
                    return MID;
                }
            }
        }

        public static VoxelShape calculateSize4Shape(int x, int y) {
            if(x == 0) {
                if(y == 0) {
                    return SIZE_4_X0_Y0;
                } else if(y == 3) {
                    return SIZE_4_X0_Y3;
                } else {
                    return SIZE_4_X0_EDGE;
                }
            } else if(x == 3) {
                if(y == 0) {
                    return SIZE_4_X3_Y0;
                } else if(y == 3) {
                    return SIZE_4_X3_Y3;
                } else {
                    return SIZE_4_X3_EDGE;
                }
            } else {
                if (y == 0) {
                    return SIZE_4_Y0_EDGE;
                } else if (y == 3) {
                    return SIZE_4_Y3_EDGE;
                } else {
                    return MID;
                }
            }
        }
    }
}
