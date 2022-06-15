package com.infinityraider.miney_games.content.poker;

import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import com.infinityraider.miney_games.reference.Names;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiFunction;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPokerTable extends BlockBaseTile<TilePokerTable> {
    public static final int MAX_SIZE = 2;

    public static final InfProperty<Integer> SIZE = InfProperty.Creators.create("size", 1, 2, MAX_SIZE);
    public static final InfProperty<Integer> X = InfProperty.Creators.create("x", 0, 0, 3);
    public static final InfProperty<Integer> Y = InfProperty.Creators.create("y", 0, 0, 7);
    public static final InfProperty<Direction> ORIENTATION = InfProperty.Creators.createHorizontals("orientation", Direction.NORTH);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(SIZE)
            .add(X)
            .add(Y)
            .add(ORIENTATION)
            .build();

    private static final BiFunction<BlockPos, BlockState, TilePokerTable> TILE_FACTORY = TilePokerTable::new;


    public BlockPokerTable() {
        super(Names.POKER_TABLE, Properties.of(Material.WOOD)
                .strength(2.0F, 3.0F)
        );
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockPos, BlockState, TilePokerTable> getTileEntityFactory() {
        return TILE_FACTORY;
    }
}
