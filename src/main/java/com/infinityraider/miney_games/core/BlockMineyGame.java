package com.infinityraider.miney_games.core;

import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class BlockMineyGame<T extends TileEntityBase> extends BlockBaseTile<T> {

    private Props props;

    public BlockMineyGame(String name, Properties properties) {
        super(name, properties);
    }

    private Props getProps() {
        if(this.props == null) {
            this.props = new Props(this);
        }
        return this.props;
    }

    @Override
    protected final InfPropertyConfiguration getPropertyConfiguration() {
        return this.getProps().getConfiguration();
    }

    protected abstract List<MineyGameSize> getAllSizes();

    public MineyGameSize getSize(BlockState state) {
        return this.getAllSizes().get(this.getProps().getSizeIndex(state));
    }

    public int getWidth(BlockState state) {
        return this.getSize(state).getWidth();
    }

    public int getDepth(BlockState state) {
        return this.getSize(state).getDepth();
    }

    public int getMaxWidth() {
        return this.getAllSizes().stream()
                .mapToInt(MineyGameSize::getWidth)
                .max()
                .orElseThrow(() -> new IllegalStateException("Miney Game Block Should at least have one size"));
    }

    public int getMaxDepth() {
        return this.getAllSizes().stream()
                .mapToInt(MineyGameSize::getDepth)
                .max()
                .orElseThrow(() -> new IllegalStateException("Miney Game Block Should at least have one size"));
    }

    public int getX(BlockState state) {
        return this.props.getX(state);
    }

    public int getY(BlockState state) {
        return this.props.getY(state);
    }

    public BlockState setOrientation(BlockState state, Direction orientation) {
        return this.props.setOrientation(state, orientation);
    }

    public Direction getOrientation(BlockState state) {
        return this.props.getOrientation(state);
    }

    public abstract VoxelShape getShape(BlockState state);

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getCollisionShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getVisualShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state, world, pos, context);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state, world, pos, context);
    }

    private static final class Props {
        public static final InfProperty<Direction> ORIENTATION = InfProperty.Creators.createHorizontals("orientation", Direction.NORTH);

        private final InfPropertyConfiguration properties;

        private final InfProperty<Integer> size;
        private final InfProperty<Integer> x;
        private final InfProperty<Integer> y;

        private Props(BlockMineyGame<?> block) {
            this.size = InfProperty.Creators.create("size", 1, 1, block.getAllSizes().size());
            this.x = InfProperty.Creators.create("x", 0, 0, block.getMaxWidth() - 1);
            this.y = InfProperty.Creators.create("y", 0, 0, block.getMaxDepth() - 1);
            this.properties = InfPropertyConfiguration.builder()
                    .add(this.size)
                    .add(this.x)
                    .add(this.y)
                    .add(ORIENTATION)
                    .build();

        }

        public int getSizeIndex(BlockState state) {
            return this.size.fetch(state) - 1;
        }

        public final InfPropertyConfiguration getConfiguration() {
            return this.properties;
        }

        public final int getX(BlockState state) {
            return this.x.fetch(state);
        }

        public final int getY(BlockState state) {
            return this.y.fetch(state);
        }

        public final BlockState setOrientation(BlockState state, Direction orientation) {
            return ORIENTATION.apply(state, orientation);
        }

        public final Direction getOrientation(BlockState state) {
            return ORIENTATION.fetch(state);
        }

    }
}
