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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public int getWidth(BlockState state) {
        return this.getSize(state).getWidth();
    }

    public int getDepth(BlockState state) {
        return this.getSize(state).getDepth();
    }

    public int getRelX(BlockState state) {
        return this.props.getX(state);
    }

    public int getRelY(BlockState state) {
        return this.props.getY(state);
    }

    public int getAbsX(BlockState state) {
        return this.getOrientation(state).xRelToAbs(this.getRelX(state), this.getRelY(state), this.getSize(state));
    }

    public int getAbsY(BlockState state) {
        return this.getOrientation(state).yRelToAbs(this.getRelX(state), this.getRelY(state), this.getSize(state));
    }

    public BlockState setSizeAndPosition(BlockState state, MineyGameSize size, int xAbs, int yAbs) {
        Orientation orientation = this.getOrientation(state);
        int xRel = orientation.xAbsToRel(xAbs, yAbs, size);
        int yRel = orientation.yAbsToRel(xAbs, yAbs, size);
        return this.getProps().setSizeAndPosition(state, size, xRel, yRel);
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
            this.getSize(state).stream(pos.offset(-this.getAbsX(state), 0, -this.getAbsY(state)), orientation)
                    .filter(p -> !p.equals(pos))
                    .forEach(p -> world.setBlock(p, this.getProps().revert(world.getBlockState(p)), 3));
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

        private static final WrappedSize SINGLE = new WrappedSize(0, new MineyGameSize(1, 1));

        private final List<MineyGameSize> sizes;
        private final boolean single;
        private final InfProperty<Position> position;
        private final InfPropertyConfiguration properties;

        private Props(BlockMineyGame<?> block) {
            this.sizes = block.getAllSizes();
            this.single = sizes.contains(SINGLE.getSize());
            this.position = this.initPositionProperty();
            this.properties = InfPropertyConfiguration.builder()
                    .add(this.position)
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
            if(this.getSize(state).equals(SINGLE.getSize())) {
                return this.allowSingle();
            }
            return true;
        }

        private Position getPosition(BlockState state) {
            return this.position.fetch(state);
        }

        public final MineyGameSize getSize(BlockState state) {
            return this.getPosition(state).getSize();
        }

        public final BlockState setSizeAndPosition(BlockState state, MineyGameSize size, int x, int y) {
            return this.position.getPossibleValues().stream()
                    .filter(p -> p.getSize().equals(size))
                    .filter(p -> p.getX() == x)
                    .filter(p -> p.getY() == y)
                    .findFirst()
                    .map(position -> this.position.apply(state, position))
                    .orElse(state);
        }

        public final BlockState revert(BlockState state) {
            return this.position.apply(state);
        }

        public final InfPropertyConfiguration getConfiguration() {
            return this.properties;
        }

        public final int getX(BlockState state) {
            return this.getPosition(state).getX();
        }

        public final int getY(BlockState state) {
            return this.getPosition(state).getY();
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

        private InfProperty<Position> initPositionProperty() {
            PositionProperty property = new PositionProperty(Stream.concat(
                    this.getSizes().stream().map(size -> new WrappedSize(this.getSizes().indexOf(size) + 1, size)),
                    this.allowSingle() ? Stream.empty() : Stream.of(SINGLE)
            ).flatMap(WrappedSize::stream).collect(Collectors.toMap(
                    Position::getSerializedName,
                    pos -> pos
            )));
            // return the inf property (no need for handling mirroring or rotations as this is handled by the orientation)
            return InfProperty.Creators.create(property, property.getDefault());
        }
    }

    /** Position property class */
    public static final class PositionProperty extends Property<Position> {
        private final Map<String, Position> positions;
        private final Position defaultPosition;

        protected PositionProperty(Map<String, Position> positions) {
            super("position", Position.class);
            this.positions = positions;
            this.defaultPosition = positions.values().stream()
                    .filter(pos -> pos.getSize().getWidth() == 1)
                    .filter(pos -> pos.getSize().getDepth() == 1)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Can not initialize MineyGameBlock properties without a size 1"));
        }

        public Position getDefault() {
            return this.defaultPosition;
        }

        @Override
        public Collection<Position> getPossibleValues() {
            return this.positions.values();
        }

        @Override
        public String getName(Position position) {
            return position.getSerializedName();
        }

        @Override
        public Optional<Position> getValue(String name) {
            return Optional.ofNullable(this.positions.get(name));
        }
    }

    /** Wrapper for size in order to have unique positions */
    private static final class WrappedSize {
        private final int index;
        private final MineyGameSize size;
        private final Position[][] positions;

        private WrappedSize(int index, MineyGameSize size) {
            this.index = index;
            this.size = size;
            this.positions = new Position[this.getWidth()][this.getDepth()];
            for(int x = 0; x < this.getWidth(); x++) {
                for(int y = 0; y < this.getDepth(); y++) {
                    this.positions[x][y] = new Position(this, x, y);
                }
            }
        }

        public int getIndex() {
            return this.index;
        }

        public MineyGameSize getSize() {
            return this.size;
        }

        public int getWidth() {
            return this.getSize().getWidth();
        }

        public int getDepth() {
            return this.getSize().getDepth();
        }

        public Position getPosition(int x, int y) {
            return this.positions[x][y];
        }

        public Stream<Position> stream() {
            return Arrays.stream(this.positions).flatMap(Arrays::stream);
        }
    }

    /** Position class for the block state property */
    private static class Position implements StringRepresentable, Comparable<Position> {
        private final WrappedSize size;
        private final int x;
        private final int y;

        protected Position(WrappedSize size, int x, int y) {
            this.size = size;
            this.x = x;
            this.y = y;
        }

        public MineyGameSize getSize() {
            return this.getWrappedSize().getSize();
        }

        public WrappedSize getWrappedSize() {
            return this.size;
        }

        public int getIndex() {
            return this.getWrappedSize().getIndex();
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return "size_" + this.getIndex() + "_x" + this.getX() + "_y" + this.getY();
        }

        @Override
        public int compareTo(@NotNull Position other) {
            int di = this.getIndex() - other.getIndex();
            if(di != 0) {
                return di;
            }
            int dx = this.getX() - other.getX();
            if(dx != 0) {
                return dx;
            }
            return this.getY() - other.getY();
        }
    }

}
