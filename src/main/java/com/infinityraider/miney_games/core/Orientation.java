package com.infinityraider.miney_games.core;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

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

    public int xRelToAbs(int xRel, int yRel, MineyGameSize size) {
        return this.xRelToAbs(xRel, yRel, size.getWidth(), size.getDepth());
    }

    public int yRelToAbs(int xRel, int yRel, MineyGameSize size) {
        return this.yRelToAbs(xRel, yRel, size.getWidth(), size.getDepth());
    }

    public int xAbsToRel(int xAbs, int yAbs, MineyGameSize size) {
        return this.xAbsToRel(xAbs, yAbs, size.getWidth(), size.getDepth());
    }

    public int yAbsToRel(int xAbs, int yAbs, MineyGameSize size) {
        return this.yAbsToRel(xAbs, yAbs, size.getWidth(), size.getDepth());
    }

    public double xRelToAbs(double xRel, double yRel, double xSizeRel, double ySizeRel) {
        return switch (this) {
            case NORTH -> xSizeRel - xRel - 1;
            case SOUTH -> xRel;
            case WEST -> yRel;
            case EAST -> ySizeRel - yRel - 1;
        };
    }

    public double yRelToAbs(double xRel, double yRel, double xSizeRel, double ySizeRel) {
        return switch (this) {
            case NORTH -> ySizeRel - yRel - 1;
            case SOUTH -> yRel;
            case WEST -> xRel;
            case EAST -> xSizeRel - xRel - 1;
        };
    }

    public double xAbsToRel(double xAbs, double yAbs, double xSizeRel, double ySizeRel) {
        return switch (this) {
            case NORTH -> xSizeRel - xAbs - 1;
            case SOUTH -> xAbs;
            case WEST -> yAbs;
            case EAST -> ySizeRel - yAbs - 1;
        };
    }

    public double yAbsToRel(double xAbs, double yAbs, double xSizeRel, double ySizeRel) {
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
