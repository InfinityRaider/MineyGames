package com.infinityraider.miney_games.core;

import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
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

    public boolean isFunctional(BlockState state) {
        return this.getProps().isFunctional(state);
    }

    public MineyGameSize getSize(BlockState state) {
        return this.getProps().getSize(state);
    }

    public BlockState setSize(BlockState state, MineyGameSize size) {
        return this.getProps().setSize(state, size);
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
                .orElseThrow(() -> new IllegalStateException("Miney Game Block Should have at least one size"));
    }

    public int getMaxDepth() {
        return this.getAllSizes().stream()
                .mapToInt(MineyGameSize::getDepth)
                .max()
                .orElseThrow(() -> new IllegalStateException("Miney Game Block Should have at least one size"));
    }

    public int getRelX(BlockState state) {
        return this.props.getX(state);
    }

    public int getRelY(BlockState state) {
        return this.props.getY(state);
    }

    public int getAbsX(BlockState state) {
        MineyGameSize size = this.getSize(state);
        return this.getOrientation(state).xRelToAbs(this.getRelX(state), this.getRelY(state), size.getWidth(), size.getDepth());
    }

    public int getAbsY(BlockState state) {
        MineyGameSize size = this.getSize(state);
        return this.getOrientation(state).yRelToAbs(this.getRelX(state), this.getRelY(state), size.getWidth(), size.getDepth());
    }

    public BlockState setAbsCoordinates(BlockState state, int xAbs, int yAbs) {
        Orientation orientation = this.getOrientation(state);
        MineyGameSize size = this.getSize(state);
        state = this.getProps().setX(state, orientation.xAbsToRel(xAbs, yAbs, size.getWidth(), size.getDepth()));
        return this.getProps().setY(state, orientation.yAbsToRel(xAbs, yAbs, size.getWidth(), size.getDepth()));
    }

    public BlockState setOrientation(BlockState state, Direction orientation) {
        return this.props.setOrientation(state, orientation);
    }

    public BlockState setOrientation(BlockState state, Orientation orientation) {
        return this.props.setOrientation(state, orientation);
    }

    public Direction getDirection(BlockState state) {
        return this.props.getDirection(state);
    }

    public Orientation getOrientation(BlockState state) {
        return this.props.getOrientation(state);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.setOrientation(this.defaultBlockState(), context.getHorizontalDirection());
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(!world.isClientSide()) {
            new MultiBlockFormer(world, pos, this, this.getOrientation(state)).tryFormMultiBlock();
        }
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
        super.destroy(world, pos, state);
        if(!world.isClientSide()) {
            Orientation orientation = this.getOrientation(state);
            BlockState reverted = this.setOrientation(this.defaultBlockState(), orientation);
            this.getSize(state).stream(pos.offset(-this.getAbsX(state), 0, -this.getAbsY(state)), orientation)
                    .filter(p -> !p.equals(pos))
                    .forEach(p -> world.setBlock(p, reverted, 3));
        }
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

    /** Implicit class to handle block properties */
    private static final class Props {
        public static final InfProperty<Orientation> ORIENTATION = InfProperty.Creators.create(
                EnumProperty.create("orientation", Orientation.class),
                Orientation.SOUTH,
                (mirror, value) -> value.mirror(mirror),
                (rotation, value) -> value.rotate(rotation)
        );

        private static final MineyGameSize SINGLE = new MineyGameSize(1, 1);

        private final List<MineyGameSize> sizes;
        private final boolean single;

        private final InfPropertyConfiguration properties;

        private final InfProperty<Integer> size;
        private final InfProperty<Integer> x;
        private final InfProperty<Integer> y;

        private Props(BlockMineyGame<?> block) {
            this.sizes = block.getAllSizes();
            this.single = sizes.contains(SINGLE);
            this.size = InfProperty.Creators.create("size", 1,  this.allowSingle() ? 1 : 0, this.getSizes().size());
            this.x = InfProperty.Creators.create("x", 0, 0, block.getMaxWidth() - 1);
            this.y = InfProperty.Creators.create("y", 0, 0, block.getMaxDepth() - 1);
            this.properties = InfPropertyConfiguration.builder()
                    .add(this.size)
                    .add(this.x)
                    .add(this.y)
                    .add(ORIENTATION)
                    .build();

        }

        public boolean allowSingle() {
            return this.single;
        }

        public List<MineyGameSize> getSizes() {
            return this.sizes;
        }

        public boolean isFunctional(BlockState state) {
            if(this.getSize(state).equals(SINGLE)) {
                return this.allowSingle();
            }
            return true;
        }

        public final MineyGameSize getSize(BlockState state) {
            int index = this.size.fetch(state) - 1;
            return index < 0 ? SINGLE : this.getSizes().get(index);
        }

        public final BlockState setSize(BlockState state, MineyGameSize size) {
            return this.size.apply(state, this.getSizes().indexOf(size) + 1);
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

        public final BlockState setX(BlockState state, int x) {
            return this.x.apply(state, x);
        }

        public final BlockState setY(BlockState state, int y) {
            return this.y.apply(state, y);
        }

        public final BlockState setOrientation(BlockState state, Direction orientation) {
            return this.setOrientation(state, Orientation.fromDirection(orientation));
        }

        public final BlockState setOrientation(BlockState state, Orientation orientation) {
            return ORIENTATION.apply(state, orientation);
        }

        public final Direction getDirection(BlockState state) {
            return this.getOrientation(state).getDirection();
        }

        public final Orientation getOrientation(BlockState state) {
            return ORIENTATION.fetch(state);
        }
    }

    /** Wrapper for direction to allow easy rotation of x and y coordinates */
    public enum Orientation implements StringRepresentable {
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        WEST(Direction.WEST),
        EAST(Direction.EAST);

        private final Direction dir;

        Orientation(Direction dir) {
            this.dir = dir;
        }

        public Direction getDirection() {
            return this.dir;
        }

        public int xRelToAbs(int xRel, int yRel, int xSizeRel, int ySizeRel) {
            return switch (this) {
                case NORTH -> xSizeRel - xRel - 1;
                case SOUTH -> xRel;
                case WEST -> yRel;
                case EAST -> ySizeRel - yRel - 1;
            };
        }

        public int yRelToAbs(int xRel, int yRel, int xSizeRel, int ySizeRel) {
            return switch (this) {
                case NORTH -> ySizeRel - yRel - 1;
                case SOUTH -> yRel;
                case WEST -> xRel;
                case EAST -> xSizeRel - xRel - 1;
            };
        }

        public int xAbsToRel(int xAbs, int yAbs, int xSizeRel, int ySizeRel) {
            return switch (this) {
                case NORTH -> xSizeRel - xAbs - 1;
                case SOUTH -> xAbs;
                case WEST -> yAbs;
                case EAST -> ySizeRel - yAbs - 1;
            };
        }

        public int yAbsToRel(int xAbs, int yAbs, int xSizeRel, int ySizeRel) {
            return switch (this) {
                case NORTH -> ySizeRel - yAbs - 1;
                case SOUTH -> yAbs;
                case WEST -> xAbs;
                case EAST -> xSizeRel - xAbs - 1;
            };
        }

        public Direction.Axis getAxis() {
            return this.getDirection().getAxis();
        }

        public Orientation getClockWise() {
            return fromDirection(this.getDirection().getClockWise());
        }

        public Orientation getCounterClockWise() {
            return fromDirection(this.getDirection().getCounterClockWise());
        }

        public Orientation mirror(Mirror mirror) {
            return fromDirection(mirror.mirror(this.getDirection()));
        }

        public Orientation rotate(Rotation rotation) {
            return fromDirection(rotation.rotate(this.getDirection()));
        }

        @Override
        public String getSerializedName() {
            return this.getDirection().getSerializedName();
        }

        public static Orientation fromDirection(Direction direction) {
            switch (direction) {
                case NORTH: return NORTH;
                case SOUTH: return SOUTH;
                case WEST: return WEST;
                case EAST: return EAST;
            }
            throw new IllegalArgumentException("can not convert a vertical direction to an orientation");
        }
    }
}
