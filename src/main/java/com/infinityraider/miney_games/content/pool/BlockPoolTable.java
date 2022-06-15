package com.infinityraider.miney_games.content.pool;

import com.google.common.collect.ImmutableList;
import com.infinityraider.miney_games.core.BlockMineyGame;
import com.infinityraider.miney_games.core.MineyGameSize;
import com.infinityraider.miney_games.reference.Names;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.BiFunction;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPoolTable extends BlockMineyGame<TilePoolTable> {
    public static final List<MineyGameSize> SIZES = ImmutableList.of(
            new MineyGameSize(2, 4),
            new MineyGameSize(3, 6)
    );

    private static final BiFunction<BlockPos, BlockState, TilePoolTable> TILE_FACTORY = TilePoolTable::new;


    public BlockPoolTable() {
        super(Names.POOL_TABLE, Properties.of(Material.WOOD)
                .strength(2.0F, 3.0F)
        );
    }

    @Override
    public BiFunction<BlockPos, BlockState, TilePoolTable> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Override
    protected List<MineyGameSize> getAllSizes() {
        return SIZES;
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        return Shapes.block();
    }
}