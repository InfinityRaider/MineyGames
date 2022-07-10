package com.infinityraider.miney_games.core;

import com.infinityraider.miney_games.reference.Names;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.UUID;

public class Wager<W extends GameWrapper> extends ItemStackHandler {
    private final W game;

    private UUID owner;
    private Status status;

    public Wager(W game, int maxStacks) {
        super(maxStacks);
        this.game = game;
        this.reset();
    }

    public W getGame() {
        return this.game;
    }

    public Status getStatus() {
        return this.status;
    }

    public void join(UUID player) {
        // TODO: make sure it is safe to join
        this.owner = player;
        this.status = Status.DECIDING;
    }

    public void leave() {
        // TODO: make sure the stacks are given to the winner
        this.owner = null;
        this.status = Status.IDLE;
    }

    public UUID getOwnerId() {
        return this.owner == null ? Util.NIL_UUID : this.owner;
    }

    public boolean isOwner(Player player) {
        return this.owner != null && player.getUUID().equals(this.owner);
    }

    public void reset() {
        this.stacks = NonNullList.withSize(this.stacks.size(), ItemStack.EMPTY);
        this.owner = null;
        this.status = Status.IDLE;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putUUID(Names.NBT.PARTICIPANT, this.getOwnerId());
        tag.putInt(Names.NBT.STATE, this.status.ordinal());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        super.deserializeNBT(tag);
        UUID id = tag.contains(Names.NBT.PARTICIPANT) ? tag.getUUID(Names.NBT.PARTICIPANT) : Util.NIL_UUID;
        this.owner = id.equals(Util.NIL_UUID) ? null : id;
        this.status = tag.contains(Names.NBT.STATE) ? Status.values()[tag.getInt(Names.NBT.PARTICIPANT)] : Status.IDLE;
    }

    public enum Status {
        IDLE(false),
        DECIDING(true),
        PROPOSED(true),
        ACCEPTED(true),
        DECLINED(true),
        PLAYING(false),
        ENDED(false);

        private final boolean canModify;

        Status(boolean canModify) {
            this.canModify = canModify;
        }

        public boolean hasEnded() {
            return this == ENDED;
        }

        public boolean canClaim(boolean owner, boolean winner) {
            if(this.hasEnded()) {
                return winner;
            }
            if(this.canModify) {
                return owner;
            }
            return false;
        }

        public Status onModified() {
            if(this.canModify) {
                return DECIDING;
            }
            return this;
        }
    }
}
