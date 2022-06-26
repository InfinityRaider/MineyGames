package com.infinityraider.miney_games.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuiMineyGameButton extends Button {
    private static final Component NO_TEXT = new TextComponent("");
    private final IGuiButtonConfig config;

    public GuiMineyGameButton(int x, int y, int w, int h, IGuiButtonConfig config, Runnable callback) {
        this(x, y, w, h, config, btn -> callback.run());
    }

    public GuiMineyGameButton(int x, int y, int w, int h, IGuiButtonConfig config, OnPress callback) {
        this(x, y, w, h, config, callback, NO_TOOLTIP);
    }

    public GuiMineyGameButton(int x, int y, int w, int h, IGuiButtonConfig config, Component text, Runnable callback) {
        this(x, y, w, h, config, text, btn -> callback.run());
    }

    public GuiMineyGameButton(int x, int y, int w, int h, IGuiButtonConfig config, Component text, OnPress callback) {
        this(x, y, w, h, config, text, callback, NO_TOOLTIP);
    }

    public GuiMineyGameButton(int x, int y, int w, int h, IGuiButtonConfig config, OnPress callback, OnTooltip tooltip) {
        this(x, y, w, h, config, NO_TEXT, callback, tooltip);
    }

    public GuiMineyGameButton(int x, int y, int w, int h, IGuiButtonConfig config, Component text, Runnable callback, OnTooltip tooltip) {
        this(x, y, w, h, config, text, btn -> callback.run(), tooltip);
    }

    public GuiMineyGameButton(int x, int y, int w, int h, IGuiButtonConfig config, Component text, OnPress callback, OnTooltip tooltip) {
        super(x, y, w, h, text, callback, tooltip);
        this.config = config;
    }

    public GuiMineyGameButton hide() {
        this.visible = true;
        return this;
    }

    public GuiMineyGameButton unhide() {
        this.visible = false;
        return this;
    }

    public GuiMineyGameButton enable() {
        this.active = true;
        return this;
    }

    public GuiMineyGameButton disable() {
        this.active = false;
        return this;
    }

    public ButtonState getState() {
        // TODO: clicked status
        switch(this.getYImage(this.isHoveredOrFocused())) {
            case 0: return ButtonState.DISABLED;
            case 1: return ButtonState.IDLE;
            case 2: return ButtonState.HOVERED;
        }
        return ButtonState.IDLE;
    }

    public IGuiButtonConfig.UV getUV() {
        return this.getState().uv(this.config);
    }

    @Override
    public void renderButton(PoseStack transforms, int mouseX, int mouseY, float partialTick) {
        // set rendering parameters
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.config.parentTexture());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        // draw the button
        this.blit(transforms, this.getUV());
        // draw the background
        this.renderBg(transforms, Minecraft.getInstance(), mouseX, mouseY);
        // draw the overlay
        this.config.overlayUV().ifPresent(overlay -> {
            transforms.pushPose();
            // translate to avoid Z-fighting
            transforms.translate(0, 0, 0.001);
            this.blit(transforms, overlay);
            transforms.popPose();
        });
        // draw the text
        Component text = this.getMessage();
        if(text != NO_TEXT) {
            drawCenteredString(transforms, Minecraft.getInstance().font, text,
                    this.x + this.width / 2, this.y + (this.height - 8) / 2,
                    getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24
            );
        }
        // draw the tooltip
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(transforms, mouseX, mouseY);
        }
    }

    protected void blit(PoseStack transforms, IGuiButtonConfig.UV uv) {
        blit(transforms, this.x, this.y, this.width, this.height, uv.u1(), uv.v1(), uv.u2() - uv.u1(), uv.v2() - uv.v1(), 256, 256);
    }
}
