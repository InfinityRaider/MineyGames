package com.infinityraider.miney_games.client.gui;

import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.content.chess.ContainerChessTable;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChessTableGui extends GuiMineyGame<ContainerChessTable> {
    public static final MenuScreens.ScreenConstructor<ContainerChessTable, ChessTableGui> PROVIDER = ChessTableGui::new;
    public static final ResourceLocation TEXTURE = new ResourceLocation(MineyGames.instance.getModId(), "textures/gui/chess/gui.png");

    public static final IGuiButtonConfig BUTTON_CONFIG_TEXT = new ButtonConfig();
    public static final IGuiButtonConfig BUTTON_CONFIG_ACCEPT = new ButtonConfig(48);
    public static final IGuiButtonConfig BUTTON_CONFIG_DECLINE = new ButtonConfig(60);

    public static final Component WAGER_TEXT = new TranslatableComponent(MineyGames.instance.getModId() + ".gui.button.wager");
    public static final Component PROPOSE_TEXT = new TranslatableComponent(MineyGames.instance.getModId() + ".gui.button.propose");

    public static MenuScreens.ScreenConstructor<ContainerChessTable, ChessTableGui> getProvider() {
        return PROVIDER;
    }

    private final GuiMineyGameButton showWagerButton;
    private final GuiMineyGameButton proposeWagerButton;
    private final GuiMineyGameButton acceptWagerButton;
    private final GuiMineyGameButton declineWagerButton;

    public ChessTableGui(ContainerChessTable menu, Inventory inv, Component title) {
        super(menu, inv, title);
        // set image dimensions
        this.imageWidth = 256;
        this.imageHeight = 256;
        this.inventoryLabelY = this.imageHeight - 119;
        // init buttons
        this.showWagerButton = new GuiMineyGameButton(7, 59, 48, 12, BUTTON_CONFIG_TEXT, WAGER_TEXT, this::onShowWagerButtonPress);
        this.proposeWagerButton = new GuiMineyGameButton(7, 121, 48, 12, BUTTON_CONFIG_TEXT, PROPOSE_TEXT, this::onProposeButtonPress);
        this.acceptWagerButton = new GuiMineyGameButton(143, 121, 12, 12, BUTTON_CONFIG_ACCEPT, this::onAcceptWagerButtonPress);
        this.declineWagerButton = new GuiMineyGameButton(157, 121, 12, 12, BUTTON_CONFIG_DECLINE, this::onDeclineWagerButtonPress);
    }

    protected void init() {
        super.init();
        this.addRenderableWidget(this.showWagerButton);
        this.addRenderableWidget(this.proposeWagerButton);
        this.addRenderableWidget(this.acceptWagerButton);
        this.addRenderableWidget(this.declineWagerButton);
    }

    @Override
    protected void renderBg(PoseStack transforms, float partialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        // background
        this.blit(transforms, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    protected void onShowWagerButtonPress() {

    }

    protected void onProposeButtonPress() {

    }

    protected void onAcceptWagerButtonPress() {

    }

    protected void onDeclineWagerButtonPress() {

    }

    private static class ButtonConfig implements IGuiButtonConfig {
        private static final UV IDLE = (BaseUV) () -> 0;
        private static final UV PRESSED = (BaseUV) () -> 12;
        private static final UV DISABLED = (BaseUV) () -> 24;
        private static final UV HOVERED = (BaseUV) () -> 36;

        private final Optional<UV> overlay;

        private ButtonConfig() {
            this.overlay = Optional.empty();
        }

        private ButtonConfig(int overlayU1) {
            this.overlay = Optional.of((BaseUV) () -> overlayU1);
        }

        @Override
        public ResourceLocation parentTexture() {
            return TEXTURE;
        }

        @Override
        public UV idleUV() {
            return IDLE;
        }

        @Override
        public UV pressedUV() {
            return PRESSED;
        }

        @Override
        public UV disabledUV() {
            return DISABLED;
        }

        @Override
        public UV hoverUV() {
            return HOVERED;
        }

        @Override
        public Optional<UV> overlayUV() {
            return this.overlay;
        }

        private interface BaseUV extends UV {
            @Override
            default int u2() {
                return this.u1() + 12;
            }

            @Override
            default int v1() {
                return 244;
            }

            @Override
            default int v2() {
                return 244;
            }
        }
    }
}
