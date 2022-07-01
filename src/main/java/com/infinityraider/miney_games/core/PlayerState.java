package com.infinityraider.miney_games.core;

import com.infinityraider.miney_games.reference.Names;
import net.minecraft.nbt.CompoundTag;

public enum PlayerState {
    EMPTY,
    PREPARING,
    READY,
    PLAYING;

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isPreparing() {
        return this == PREPARING;
    }

    public boolean isReady() {
        return this == READY;
    }

    public boolean isPlaying() {
        return this == PLAYING;
    }

    public void toTag(CompoundTag tag) {
        tag.putInt(Names.NBT.STATE, this.ordinal());
    }

    public static PlayerState fromTag(CompoundTag tag) {
        int id = tag.contains(Names.NBT.STATE) ? tag.getInt(Names.NBT.STATE) : 0;
        id = Math.max(0, id);
        id = Math.min(id, values().length - 1);
        return values()[id];
    }
}
