package com.infinityraider.miney_games.client.gui;

import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.container.ContainerMenuBase;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class GuiMineyGame<T extends ContainerMenuBase> extends AbstractContainerScreen<T> {
    private final Set<Tickable> tickables;

    public GuiMineyGame(T container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tickables = Sets.newIdentityHashSet();
    }

    @Override
    public void render(PoseStack transforms, int mouseX, int mouseY, float partialTick) {
        super.render(transforms, mouseX, mouseY, partialTick);
        this.renderTooltip(transforms, mouseX, mouseY);
    }

    @Override
    protected <W extends GuiEventListener & NarratableEntry> W addWidget(W widget) {
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

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    public void enablePlayerInventorySlots() {
        this.menu.enablePlayerInventorySlots();
    }

    public void disablePlayerInventorySlots() {
        this.menu.disablePlayerInventorySlots();
    }
}
