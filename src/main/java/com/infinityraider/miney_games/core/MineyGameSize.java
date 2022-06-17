package com.infinityraider.miney_games.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Optional;
import java.util.stream.Stream;

public class MineyGameSize {
    private final int width;
    private final int depth;

    public MineyGameSize(int width, int depth) {
        this.width = width;
        this.depth = depth;
    }

    public int getWidth() {
        return this.width;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getAbsSizeX(Orientation orientation) {
        return switch (orientation) {
            case NORTH, SOUTH -> this.getWidth();
            case EAST, WEST -> this.getDepth();
        };
    }

    public int getAbsSizeY(Orientation orientation) {
        return switch (orientation) {
            case NORTH, SOUTH -> this.getDepth();
            case EAST, WEST -> this.getWidth();
        };
    }

    public Stream<BlockPos> stream(Orientation orientation) {
        return this.stream(new BlockPos(0, 0, 0), orientation);
    }

    public Stream<BlockPos> stream(BlockPos offset, Orientation orientation) {
        return BlockPos.betweenClosedStream(
                offset,
                offset.offset(this.getAbsSizeX(orientation) - 1, 0, this.getAbsSizeY(orientation) - 1)
        );
    }

    public Optional<Orientation> matches(BlockPos min, BlockPos max, Orientation closest) {
        int width = max.getX() - min.getX() + 1;
        int depth = max.getZ() - min.getZ() + 1;
        if(width == this.getWidth() && depth == this.getDepth()) {
            // width and depth are equal, orientation doesn't matter
            if(this.getWidth() == this.getDepth()) {
                return Optional.of(closest);
            }
            // width and depth match: 0 or 180 degree rotation (north or south)
            if(closest.getAxis() == Direction.Axis.Z) {
                // if the rotation matches the orientation, return it
                return Optional.of(closest);
            } else {
                // else rotate anti-clockwise
                return Optional.of(closest.getCounterClockWise());
            }
        }
        if(width == this.getDepth() && depth == this.getWidth()) {
            // width and depth are flipped: 90 or 270 degree rotation (east or west)
            if(closest.getAxis() == Direction.Axis.X) {
                // if the rotation matches the orientation, return it
                return Optional.of(closest);
            } else {
                // else rotate clockwise
                return Optional.of(closest.getClockWise());
            }
        }
        // either width or depth does not match, not a valid shape
        return Optional.empty();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj instanceof MineyGameSize) {
            MineyGameSize other = (MineyGameSize) obj;
            return other.getWidth() == this.getWidth() && other.getDepth() == this.getDepth();
        }
        return false;
    }
}
