package com.infinityraider.miney_games.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
public class GuiButtonMineyGame extends Button {
    private static final Component NO_TEXT = new TextComponent("");
    private final IGuiButtonConfig config;

    private final int x0;
    private final int y0;

    public GuiButtonMineyGame(int x, int y, int w, int h, IGuiButtonConfig config, Runnable callback) {
        this(x, y, w, h, config, btn -> callback.run());
    }

    public GuiButtonMineyGame(int x, int y, int w, int h, IGuiButtonConfig config, OnPress callback) {
        this(x, y, w, h, config, callback, NO_TOOLTIP);
    }

    public GuiButtonMineyGame(int x, int y, int w, int h, IGuiButtonConfig config, Component text, Runnable callback) {
        this(x, y, w, h, config, text, btn -> callback.run());
    }

    public GuiButtonMineyGame(int x, int y, int w, int h, IGuiButtonConfig config, Component text, OnPress callback) {
        this(x, y, w, h, config, text, callback, NO_TOOLTIP);
    }

    public GuiButtonMineyGame(int x, int y, int w, int h, IGuiButtonConfig config, OnPress callback, OnTooltip tooltip) {
        this(x, y, w, h, config, NO_TEXT, callback, tooltip);
    }

    public GuiButtonMineyGame(int x, int y, int w, int h, IGuiButtonConfig config, Component text, Runnable callback, OnTooltip tooltip) {
        this(x, y, w, h, config, text, btn -> callback.run(), tooltip);
    }

    public GuiButtonMineyGame(int x, int y, int w, int h, IGuiButtonConfig config, Component text, OnPress callback, OnTooltip tooltip) {
        super(x, y, w, h, text, callback, tooltip);
        this.config = config;
        this.x0 = x;
        this.y0 = y;
    }

    public GuiButtonMineyGame hide() {
        this.visible = false;
        return this;
    }

    public GuiButtonMineyGame unhide() {
        this.visible = true;
        return this;
    }

    public GuiButtonMineyGame enable() {
        this.active = true;
        return this;
    }

    public GuiButtonMineyGame disable() {
        this.active = false;
        return this;
    }

    public ButtonState getState() {
        return switch (this.getYImage(this.isHoveredOrFocused())) {
            case 0 -> ButtonState.DISABLED;
            case 2 -> ButtonState.HOVERED;
            default -> ButtonState.IDLE;
        };
    }

    public IGuiButtonConfig.UV getUV() {
        return this.getState().uv(this.config);
    }

    public GuiButtonMineyGame relative(AbstractContainerScreen<?> screen) {
        this.x = this.x0 + screen.getGuiLeft();
        this.y = this.y0 + screen.getGuiTop();
        return this;
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
        this.blit(transforms);
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

    protected void blit(PoseStack transforms) {
        this.blit(transforms, this.getUV());
    }

    protected void blit(PoseStack transforms, IGuiButtonConfig.UV uv) {
        int du = uv.edgeWidth();
        int dv = uv.edgeHeight();
        // top left pixel
        blit(transforms, this.x, this.y, du, dv, uv.u1(), uv.v1(), du, dv, 256, 256);
        // top edge
        blit(transforms, this.x + du, this.y, this.width - 2*du, dv, uv.u1() + du, uv.v1(), uv.u2() - uv.u1() - 2*du, dv, 256, 256);
        // left edge
        blit(transforms, this.x, this.y + dv, du, this.height - 2*dv, uv.u1(), uv.v1() + dv, du, uv.v2() - uv.v1() - 2*dv, 256, 256);
        // bottom left pixel
        blit(transforms, this.x, this.y + this.height - dv, du, dv, uv.u1(), uv.v2() - dv, du, dv, 256, 256);
        // bottom edge
        blit(transforms, this.x + du, this.y + this.height - dv, this.width - 2*du, dv, uv.u1() + du, uv.v2() - dv, uv.u2() - uv.u1() - 2*du, dv, 256, 256);
        // top right pixel
        blit(transforms, this.x + this.width - du, this.y, du, dv, uv.u2() - du, uv.v1(), du, dv, 256, 256);
        // right edge
        blit(transforms, this.x + this.width - du, this.y + dv, du, this.height - 2*dv, uv.u2() - du, uv.v1() + dv, du, uv.v2() - uv.v1() - 2*dv, 256, 256);
        // bottom right pixel
        blit(transforms, this.x + this.width - du, this.y + this.height - dv, du, dv, uv.u2() - du, uv.v2() - dv, du, dv, 256, 256);
        // centre
        blit(transforms, this.x + du, this.y + dv, this.width - 2*du, this.height - 2*dv, uv.u1() + du, uv.v1() + dv, uv.u2() - uv.u1() - 2*du, uv.v2() - uv.v1() - 2*dv, 256, 256);
    }
}
