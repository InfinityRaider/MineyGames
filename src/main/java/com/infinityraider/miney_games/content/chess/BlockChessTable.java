package com.infinityraider.miney_games.content.chess;

import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import com.infinityraider.miney_games.reference.Names;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiFunction;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockChessTable extends BlockBaseTile<TileChessTable> {
    public static final int MAX_SIZE = 4;

    public static final InfProperty<Integer> SIZE = InfProperty.Creators.create("size", 1, 1, MAX_SIZE);
    public static final InfProperty<Integer> X = InfProperty.Creators.create("x", 0, 0, MAX_SIZE - 1);
    public static final InfProperty<Integer> Y = InfProperty.Creators.create("x", 0, 0, MAX_SIZE - 1);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(SIZE)
            .add(X)
            .add(Y)
            .build();

    private static final BiFunction<BlockPos, BlockState, TileChessTable> TILE_FACTORY = TileChessTable::new;


    public BlockChessTable() {
        super(Names.CHESS_TABLE, Properties.of(Material.STONE)
                .strength(1.5F, 6.0F)
        );
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockPos, BlockState, TileChessTable> getTileEntityFactory() {
        return TILE_FACTORY;
    }
}
