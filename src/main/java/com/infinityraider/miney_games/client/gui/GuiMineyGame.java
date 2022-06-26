package com.infinityraider.miney_games.client.gui;

import com.google.common.collect.Sets;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class GuiMineyGame<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    private final Set<Tickable> tickables;

    public GuiMineyGame(T container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tickables = Sets.newIdentityHashSet();
    }

    @Override
    protected <T extends GuiEventListener & NarratableEntry> T addWidget(T widget) {
        if(widget instanceof Tickable) {
            this.tickables.add((Tickable) widget);
        }
        return super.addWidget(widget);
    }

    @Override
    protected void containerTick() {
        this.tickables.forEach(Tickable::tick);
        this.onTick();
    }

    protected void onTick() {}
}
