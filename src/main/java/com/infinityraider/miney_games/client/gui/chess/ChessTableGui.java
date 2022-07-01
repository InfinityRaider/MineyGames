package com.infinityraider.miney_games.client.gui.chess;

import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.client.gui.GuiMineyGame;
import com.infinityraider.miney_games.client.gui.GuiButtonMineyGame;
import com.infinityraider.miney_games.client.gui.IGuiButtonConfig;
import com.infinityraider.miney_games.content.chess.ChessGameWrapper;
import com.infinityraider.miney_games.content.chess.ContainerChessTable;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import com.infinityraider.miney_games.core.PlayerState;
import com.infinityraider.miney_games.network.chess.MessageReadyChessPlayer;
import com.infinityraider.miney_games.network.chess.MessageSetChessPlayer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChessTableGui extends GuiMineyGame<ContainerChessTable, TileChessTable, ChessGameWrapper> {
    public static final MenuScreens.ScreenConstructor<ContainerChessTable, ChessTableGui> PROVIDER = ChessTableGui::new;
    public static final ResourceLocation TEXTURE = new ResourceLocation(MineyGames.instance.getModId(), "textures/gui/chess/gui.png");

    public static final int P1_X = 51 - 26 + 7;
    public static final int P2_X = 51 - 26 + 120;
    public static final int P_Y = 75 - 8 + 16;

    public static final int Y_SPLIT_1 = 103;
    public static final int Y_SPLIT_2 = 236;

    public static final IGuiButtonConfig BUTTON_CONFIG_TEXT = new ButtonConfig();
    public static final IGuiButtonConfig BUTTON_CONFIG_ACCEPT = new ButtonConfig(48);
    public static final IGuiButtonConfig BUTTON_CONFIG_DECLINE = new ButtonConfig(60);

    public static final Component JOIN_TEXT = new TranslatableComponent(MineyGames.instance.getModId() + ".gui.button.join");
    public static final Component LEAVE_TEXT = new TranslatableComponent(MineyGames.instance.getModId() + ".gui.button.leave");
    public static final Component READY_TEXT = new TranslatableComponent(MineyGames.instance.getModId() + ".gui.button.ready");
    public static final Component START_TEXT = new TranslatableComponent(MineyGames.instance.getModId() + ".gui.button.start");
    public static final Component RESIGN_TEXT = new TranslatableComponent(MineyGames.instance.getModId() + ".gui.button.resign");
    public static final Component DRAW_TEXT = new TranslatableComponent(MineyGames.instance.getModId() + ".gui.button.draw");
    public static final Component WAGER_TEXT = new TranslatableComponent(MineyGames.instance.getModId() + ".gui.button.wager");
    public static final Component PROPOSE_TEXT = new TranslatableComponent(MineyGames.instance.getModId() + ".gui.button.propose");

    public static MenuScreens.ScreenConstructor<ContainerChessTable, ChessTableGui> getProvider() {
        return PROVIDER;
    }

    private final GuiButtonMineyGame joinLeaveWhiteButton;
    private final GuiButtonMineyGame joinLeaveBlackButton;
    private final GuiButtonMineyGame readyButton;
    private final GuiButtonMineyGame startButton;
    private final GuiButtonMineyGame resignButton;
    private final GuiButtonMineyGame drawButton;
    private final GuiButtonMineyGame acceptDrawButton;
    private final GuiButtonMineyGame declineDrawButton;
    private final GuiButtonMineyGame showWagerButton;
    private final GuiButtonMineyGame proposeWagerButton;
    private final GuiButtonMineyGame acceptWagerButton;
    private final GuiButtonMineyGame declineWagerButton;

    private boolean wagering;

    public ChessTableGui(ContainerChessTable menu, Inventory inv, Component title) {
        super(menu, inv, title);
        // set image dimensions
        this.imageWidth = 176;
        this.imageHeight = 240;
        this.inventoryLabelY = this.imageHeight - 94;
        // init buttons
        this.joinLeaveWhiteButton = new GuiButtonMineyGame(6, 89, 51, 12, BUTTON_CONFIG_TEXT, JOIN_TEXT, this::onJoinLeaveWhiteButtonPress);
        this.joinLeaveBlackButton = new GuiButtonMineyGame(119, 89, 51, 12, BUTTON_CONFIG_TEXT, JOIN_TEXT, this::onJoinLeaveBlackButtonPress);
        this.readyButton = new GuiButtonMineyGame(61, 16, 54, 12, BUTTON_CONFIG_TEXT, READY_TEXT, this::onReadyButtonPress);
        this.startButton = new GuiButtonMineyGame(61, 32, 54, 12, BUTTON_CONFIG_TEXT, START_TEXT, this::onStartButtonPress);
        this.resignButton = new GuiButtonMineyGame(61, 48, 54, 12, BUTTON_CONFIG_TEXT, RESIGN_TEXT, this::onResignButtonPress);
        this.drawButton = new GuiButtonMineyGame(75, 64, 26, 12, BUTTON_CONFIG_TEXT, DRAW_TEXT, this::onDrawButtonPress);
        this.acceptDrawButton = new GuiButtonMineyGame(61, 64, 12, 12, BUTTON_CONFIG_ACCEPT, this::onAcceptDrawButtonPress);
        this.declineDrawButton = new GuiButtonMineyGame(103, 64, 12, 12, BUTTON_CONFIG_DECLINE, this::onDeclineDrawButtonPress);
        this.showWagerButton = new GuiButtonMineyGame(62, 89, 52, 12, BUTTON_CONFIG_TEXT, WAGER_TEXT, this::onShowWagerButtonPress);
        this.proposeWagerButton = new GuiButtonMineyGame(95, 144, 46, 12, BUTTON_CONFIG_TEXT, PROPOSE_TEXT, this::onProposeButtonPress);
        this.acceptWagerButton = new GuiButtonMineyGame(143, 144, 12, 12, BUTTON_CONFIG_ACCEPT, this::onAcceptWagerButtonPress);
        this.declineWagerButton = new GuiButtonMineyGame(157, 144, 12, 12, BUTTON_CONFIG_DECLINE, this::onDeclineWagerButtonPress);
        // status variables
        this.wagering = false;
    }

    @Override
    public void updateGuiState() {
        Player player = this.getPlayer();
        ChessGameWrapper game = this.getGame();
        // null check
        if(game == null || !this.isValid()) {
            this.toggleAllButtons(false);
            return;
        }
        // player 1 join button
        this.updateJoinLeaveButton(this.joinLeaveWhiteButton, this.hasPlayer1(), this.isPlayer1(), game.isRunning());
        // player 2 join button
        this.updateJoinLeaveButton(this.joinLeaveBlackButton, this.hasPlayer2(), this.isPlayer2(), game.isRunning());
        // ready button
        this.updateReadyButton();
        // start button
        this.updateStartButton();
    }

    protected void init() {
        super.init();
        this.addRenderableWidget(this.joinLeaveWhiteButton.relative(this));
        this.addRenderableWidget(this.joinLeaveBlackButton.relative(this));
        this.addRenderableWidget(this.readyButton.relative(this)).disable();
        this.addRenderableWidget(this.startButton.relative(this)).disable();
        this.addRenderableWidget(this.resignButton.relative(this)).disable();
        this.addRenderableWidget(this.drawButton.relative(this)).disable();
        this.addRenderableWidget(this.acceptDrawButton.relative(this)).disable();
        this.addRenderableWidget(this.declineDrawButton.relative(this)).disable();
        this.addRenderableWidget(this.showWagerButton.relative(this));
        this.addRenderableWidget(this.proposeWagerButton.relative(this).disable().hide());
        this.addRenderableWidget(this.acceptWagerButton.relative(this)).disable().hide();
        this.addRenderableWidget(this.declineWagerButton.relative(this).disable()).hide();
        this.disablePlayerInventorySlots();
        this.updateGuiState();
    }

    public boolean isGameRunning() {
        ChessGameWrapper game = this.getGame();
        return game != null && this.isValid() && game.isRunning();
    }

    public boolean isPlayer() {
        return this.isPlayer1() || this.isPlayer2();
    }

    public boolean isPlayer1() {
        ChessGameWrapper game = this.getGame();
        return game != null && this.isValid() && game.isPlayer1(this.getPlayer());
    }

    public boolean isPlayer2() {
        ChessGameWrapper game = this.getGame();
        return game != null && this.isValid() && game.isPlayer2(this.getPlayer());
    }

    public boolean hasPlayer1() {
        ChessGameWrapper game = this.getGame();
        return game == null || !this.isValid() || game.getPlayer1().hasPlayer();
    }

    public boolean hasPlayer2() {
        ChessGameWrapper game = this.getGame();
        return game == null || !this.isValid() || game.getPlayer2().hasPlayer();
    }

    public UUID getPlayer1() {
        ChessGameWrapper game = this.getGame();
        return (game != null && this.isValid()) ? game.getPlayer1().getPlayerId() : Util.NIL_UUID;
    }

    public UUID getPlayer2() {
        ChessGameWrapper game = this.getGame();
        return (game != null && this.isValid()) ? game.getPlayer2().getPlayerId() : Util.NIL_UUID;
    }

    public PlayerState getPlayer1State() {
        ChessGameWrapper game = this.getGame();
        return (game != null && this.isValid()) ? game.getPlayer1().getState() : PlayerState.EMPTY;
    }

    public PlayerState getPlayer2State() {
        ChessGameWrapper game = this.getGame();
        return (game != null && this.isValid()) ? game.getPlayer2().getState() : PlayerState.EMPTY;
    }

    @Override
    public void render(PoseStack transforms, int mouseX, int mouseY, float partialTick) {
        super.render(transforms, mouseX, mouseY, partialTick);
        // draw players
        if(this.hasPlayer1()) {
            if(this.isPlayer1()) {
                this.drawPlayerInGui(P1_X, P_Y, mouseX, mouseY);
            } else {
                this.drawPlayerInGui(this.getPlayer1(), P1_X, P_Y, mouseX, mouseY);
            }
            if(this.getPlayer1State().isReady()) {
                // draw ready mark
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, TEXTURE);
                this.blit(transforms, this.leftPos + 46, this.topPos + 17, 49, 245, 10, 10);
            }
        }
        if(this.hasPlayer2()) {
            if(this.isPlayer2()) {
                this.drawPlayerInGui(P2_X, P_Y, mouseX, mouseY);
            } else {
                this.drawPlayerInGui(this.getPlayer2(), P2_X, P_Y, mouseX, mouseY);
            }
            if(this.getPlayer2State().isReady()) {
                // draw ready mark
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, TEXTURE);
                this.blit(transforms, this.leftPos + 158, this.topPos + 17, 49, 245, 10, 10);
            }
        }
    }

    @Override
    protected void renderBg(PoseStack transforms, float partialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        if(this.wagering) {
            // full texture
            this.blit(transforms, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
        } else {
            // exclude wagering
            this.blit(transforms, relX, relY, 0, 0, this.imageWidth, Y_SPLIT_1);
            this.blit(transforms, relX, relY + Y_SPLIT_1, 0, Y_SPLIT_2, this.imageWidth, 6);
        }
    }

    @Override
    protected void renderLabels(PoseStack transforms, int mouseX, int mouseY) {
        if(this.wagering) {
            super.renderLabels(transforms, mouseX, mouseY);
        } else {
            this.font.draw(transforms, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 4210752);
        }
    }

    protected void onShowWagerButtonPress() {
        this.wagering = !this.wagering;
        if(this.wagering) {
            this.proposeWagerButton.unhide();
            this.acceptWagerButton.unhide();
            this.declineWagerButton.unhide();
            this.enablePlayerInventorySlots();
        } else {
            this.proposeWagerButton.hide();
            this.acceptWagerButton.hide();
            this.declineWagerButton.hide();
            this.disablePlayerInventorySlots();
        }
    }

    protected void onJoinLeaveWhiteButtonPress() {
        if(this.hasPlayer1()) {
            if(this.isPlayer1() && !this.isGameRunning()) {
                // remove player
                new MessageSetChessPlayer.ToServer(this.getTile(), true).sendToServer();
            }
        } else {
            new MessageSetChessPlayer.ToServer(this.getTile(), true, this.getPlayer().getUUID()).sendToServer();
        }
    }

    protected void onJoinLeaveBlackButtonPress() {
        if(this.hasPlayer2()) {
            if(this.isPlayer2() && !this.isGameRunning()) {
                // remove player
                new MessageSetChessPlayer.ToServer(this.getTile(), false).sendToServer();
            }
        } else {
            new MessageSetChessPlayer.ToServer(this.getTile(), false, this.getPlayer().getUUID()).sendToServer();
        }
    }

    protected void onReadyButtonPress() {
        if(this.isPlayer1()) {
            PlayerState state = this.getPlayer1State();
            if (state.isReady()) {
                new MessageReadyChessPlayer.ToServer(this.getTile(), true, false).sendToServer();
            } else if (state.isPreparing()) {
                new MessageReadyChessPlayer.ToServer(this.getTile(), true, true).sendToServer();
            }
        }
        if(this.isPlayer2()) {
            PlayerState state = this.getPlayer2State();
            if(state.isReady()) {
                new MessageReadyChessPlayer.ToServer(this.getTile(), false, false).sendToServer();
            } else if(state.isPreparing()) {
                new MessageReadyChessPlayer.ToServer(this.getTile(), false, true).sendToServer();
            }
        }
    }

    protected void onStartButtonPress() {

    }

    protected void onResignButtonPress() {

    }

    protected void onDrawButtonPress() {

    }

    protected void onAcceptDrawButtonPress() {

    }

    protected void onDeclineDrawButtonPress() {

    }

    protected void onProposeButtonPress() {

    }

    protected void onAcceptWagerButtonPress() {

    }

    protected void onDeclineWagerButtonPress() {

    }

    protected void updateJoinLeaveButton(GuiButtonMineyGame button, boolean hasPlayer, boolean isPlayer, boolean gameRunning) {
        if(hasPlayer) {
            if(isPlayer) {
                button.enable().setMessage(LEAVE_TEXT);
                if(gameRunning) {
                    button.disable();
                }
            } else {
                button.disable().setMessage(JOIN_TEXT);
            }
        } else {
            button.enable().setMessage(JOIN_TEXT);
        }
    }

    protected void updateReadyButton() {
        if(this.isPlayer()) {
            PlayerState state = this.isPlayer1() ? this.getPlayer1State() : this.getPlayer2State();
            if(state.isPreparing() || state.isReady()) {
                this.readyButton.enable();
            } else {
                this.readyButton.disable();
            }
        } else {
            this.readyButton.disable();
        }
    }

    protected void updateStartButton() {
        if(this.isPlayer()) {
            if(this.getPlayer1State().isReady() && this.getPlayer2State().isReady()) {
                this.startButton.enable();
                return;
            }
        }
        this.startButton.disable();
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
                return 256;
            }
        }
    }
}
