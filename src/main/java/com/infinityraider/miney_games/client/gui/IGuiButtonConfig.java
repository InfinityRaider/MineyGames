package com.infinityraider.miney_games.client.gui;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface IGuiButtonConfig {
    ResourceLocation parentTexture();

    UV idleUV();

    UV pressedUV();

    UV disabledUV();

    UV hoverUV();

    Optional<UV> overlayUV();

    interface UV {
        int u1();

        int v1();

        int u2();

        int v2();

        default int edgeWidth() {
            return 1;
        }

        default int edgeHeight() {
            return 1;
        }
    }
}
