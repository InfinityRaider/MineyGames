package com.infinityraider.miney_games.client.gui;

import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.content.chess.ContainerChessTable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChessTableGui extends AbstractContainerScreen<ContainerChessTable> {
    private static final MenuScreens.ScreenConstructor<ContainerChessTable, ChessTableGui> PROVIDER = ChessTableGui::new;
    public static final ResourceLocation TEXTURE = new ResourceLocation(MineyGames.instance.getModId(), "textures/gui/chess/gui.png");

    public static MenuScreens.ScreenConstructor<ContainerChessTable, ChessTableGui> getProvider() {
        return PROVIDER;
    }

    public ChessTableGui(ContainerChessTable menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {

    }
}
