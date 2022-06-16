package com.infinityraider.miney_games.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class MultiBlockFormer {
    private final Level world;
    private final BlockMineyGame<?> block;

    private BlockMineyGame.Orientation orientation;

    private final List<BlockPos> toVisit;
    private final Set<BlockPos> visited;
    private final Set<BlockPos> approved;

    private BlockPos min;
    private BlockPos max;

    public MultiBlockFormer(Level world, BlockPos pos, BlockMineyGame<?> block, BlockMineyGame.Orientation orientation) {
        this.world = world;
        this.block = block;
        this.orientation = orientation;
        this.toVisit = Lists.newArrayList(pos);
        this.visited = Sets.newHashSet();
        this.approved = Sets.newHashSet();
        this.min = pos;
        this.max = pos;
    }

    public Level getWorld() {
        return this.world;
    }

    public BlockMineyGame<?> getBlock() {
        return this.block;
    }

    protected void setOrientation(BlockMineyGame.Orientation orientation) {
        this.orientation = orientation;
    }

    public BlockMineyGame.Orientation getOrientation() {
        return this.orientation;
    }

    public BlockPos getMin() {
        return this.min;
    }

    public BlockPos getMax() {
        return this.max;
    }

    public void tryFormMultiBlock() {
        this.scanRecursive();
        // no need to form a multi block of size 1
        if(this.getMin().equals(this.getMax())) {
            return;
        }
        // check if the number of blocks corresponds to the found range
        int amount = (this.getMax().getX() - this.getMin().getX() + 1) * (this.getMax().getZ() - this.getMin().getZ() + 1);
        if(amount != this.approved.size()) {
            return;
        }
        for(MineyGameSize size : this.getBlock().getAllSizes()) {
            boolean success = size.matches(this.getMin(), this.getMax(), this.getOrientation())
                    .map(orientation -> {
                        this.setOrientation(orientation);
                        return true;
                    })
                    .orElse(false);
            if(success) {
                this.formMultiBlock(size);
                break;
            }
        }
    }

    protected void formMultiBlock(MineyGameSize size) {
        size.stream(this.getMin(), this.getOrientation()).forEach(pos -> {
            BlockState state = this.getBlock().defaultBlockState();
            state = this.getBlock().setOrientation(state, this.getOrientation());
            state = this.getBlock().setSize(state, size);
            state = this.getBlock().setAbsCoordinates(state, pos.getX() - this.getMin().getX(), pos.getZ() - this.getMin().getZ());
            this.getWorld().setBlock(pos, state, 3);
        });
    }

    protected void scanRecursive() {
        // if there are no more positions to check, return
        if(this.toVisit.isEmpty()) {
            return;
        }
        // remove the next unvisited position from the list and at it to the list of visited positions
        BlockPos current = this.toVisit.remove(0);
        this.visited.add(current);
        // fetch the block at the current position
        BlockState state = this.getWorld().getBlockState(current);
        // check if the block is the same as the current one
        if(state.getBlock() == this.getBlock()) {
            // add it to the approved positions
            this.approved.add(current);
            // update current min and max
            this.updateMinAndMax(current);
            // if the block is part of a multi-block, add all positions it contains
            int pX = this.getBlock().getAbsX(state);
            int pY = this.getBlock().getAbsY(state);
            this.addPositions(this.getBlock().getSize(state).stream(current.offset(-pX, 0, -pY), this.getBlock().getOrientation(state)));
            // also add the neighbours to the positions to check
            this.addPositions(Arrays.stream(Direction.values()).filter(dir -> dir.getAxis().isHorizontal()).map(current::relative));
        }
        // recursion
        this.scanRecursive();
    }

    protected void updateMinAndMax(BlockPos pos) {
        if(pos.getX() <= this.getMin().getX() && pos.getY() <= this.getMin().getY() && pos.getZ() <= this.getMin().getZ()) {
            this.min = pos;
        }
        if(pos.getX() >= this.getMax().getX() && pos.getY() >= this.getMax().getY() && pos.getZ() >= this.getMax().getZ()) {
            this.max = pos;
        }
    }

    protected void addPositions(Stream<BlockPos> positions) {
        positions.filter(pos -> !this.toVisit.contains(pos))
                .filter(pos -> !this.visited.contains(pos))
                .forEach(this.toVisit::add);
    }
}
