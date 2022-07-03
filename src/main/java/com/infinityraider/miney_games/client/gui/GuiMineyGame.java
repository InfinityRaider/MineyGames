package com.infinityraider.miney_games.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.core.ContainerMineyGame;
import com.infinityraider.miney_games.core.GameWrapper;
import com.infinityraider.miney_games.core.TileMineyGame;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class GuiMineyGame<C extends ContainerMineyGame<T, G>, T extends TileMineyGame<?, G>, G extends GameWrapper> extends AbstractContainerScreen<C> {
    private final List<GuiButtonMineyGame> buttons;
    private final Set<Tickable> tickables;

    public GuiMineyGame(C container, Inventory inv, Component title) {
        super(container, inv, title);
        this.buttons = Lists.newArrayList();
        this.tickables = Sets.newIdentityHashSet();
    }

    public Player getPlayer() {
        return MineyGames.instance.getClientPlayer();
    }

    public boolean isValid() {
        return this.getMenu().isValid();
    }

    @Nullable
    public T getTile() {
        return this.getMenu().getTile();
    }

    @Nullable
    public G getGame() {
        return this.getMenu().getGame();
    }

    @Override
    public void render(PoseStack transforms, int mouseX, int mouseY, float partialTick) {
        super.render(transforms, mouseX, mouseY, partialTick);
        this.renderTooltip(transforms, mouseX, mouseY);
    }

    @Override
    protected <W extends GuiEventListener & NarratableEntry> W addWidget(W widget) {
        if(widget instanceof GuiButtonMineyGame) {
            this.buttons.add((GuiButtonMineyGame) widget);
        }
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

    public abstract void updateGuiState();

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

    protected void toggleAllButtons(boolean status) {
        this.buttons.forEach(button -> {
            if(status) {
                button.enable();
            } else {
                button.disable();
            }
        });
    }

    public void drawPlayerInGui(int x, int y, int mouseX, int mouseY) {
        this.drawPlayerInGui(this.getPlayer(), x, y, mouseX, mouseY);
    }

    public void drawPlayerInGui(UUID id, int x, int y, int mouseX, int mouseY) {
        this.getPlayer(id).ifPresent(player ->  this.drawPlayerInGui(player, x, y, mouseX, mouseY));
    }

    public void drawPlayerInGui(Player player, int x, int y, int mouseX, int mouseY) {
        InventoryScreen.renderEntityInInventory(
                this.leftPos + x,
                this.topPos + y,
                30,
                this.leftPos + x - mouseX,
                this.topPos + y - 50 - mouseY,
                player
        );
    }

    public Optional<Player> getPlayer(UUID id) {
        ClientLevel world = Minecraft.getInstance().level;
        if(world == null) {
            return Optional.empty();
        }
        for(Entity entity : world.entitiesForRendering()) {
            if(entity instanceof Player) {
                Player player = (Player) entity;
                if(player.getUUID().equals(id)) {
                    return Optional.of(player);
                }
            }
        }
        return Optional.empty();
    }
}
