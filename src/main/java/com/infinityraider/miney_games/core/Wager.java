package com.infinityraider.miney_games.core;

import com.infinityraider.miney_games.reference.Names;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class Wager<W extends GameWrapper<?>> {
    private static final int MAX_STACKS = 9;

    private final W game;
    private final ItemStack[] wagers;

    private UUID owner;
    private Status status;

    public Wager(W game) {
        this.game = game;
        this.wagers = new ItemStack[MAX_STACKS];
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

    public boolean isOwner(Player player) {
        return this.owner != null && player.getUUID().equals(this.owner);
    }

    public void reset() {
        for(int i = 0; i < MAX_STACKS; i++) {
            this.wagers[i] = ItemStack.EMPTY;
        }
        this.owner = null;
        this.status = Status.IDLE;
    }

    public CompoundTag writeToNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag wagers = new ListTag();
        for(int i = 0; i < MAX_STACKS; i++) {
            wagers.add(this.wagers[i].save(new CompoundTag()));
        }
        tag.put(Names.NBT.WAGERS, wagers);
        return tag;
    }

    public void readFromNBT(CompoundTag tag) {
        ListTag wagers = tag.contains(Names.NBT.WAGERS) ? tag.getList(Names.NBT.WAGERS, Tag.TAG_COMPOUND) : new ListTag();
        for(int i = 0; i < MAX_STACKS; i++) {
            this.wagers[i] = wagers.size() > i ? ItemStack.of(wagers.getCompound(i)) : ItemStack.EMPTY;
        }
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
