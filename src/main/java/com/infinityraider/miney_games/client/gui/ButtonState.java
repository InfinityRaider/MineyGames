package com.infinityraider.miney_games.client.gui;

import java.util.function.Function;

public enum ButtonState {
    IDLE(IGuiButtonConfig::idleUV),
    PRESSED(IGuiButtonConfig::pressedUV),
    DISABLED(IGuiButtonConfig::disabledUV),
    HOVERED(IGuiButtonConfig::hoverUV);

    private final Function<IGuiButtonConfig, IGuiButtonConfig.UV> uvFunction;

    ButtonState(Function<IGuiButtonConfig, IGuiButtonConfig.UV> uvFunction) {
        this.uvFunction = uvFunction;
    }

    public IGuiButtonConfig.UV uv(IGuiButtonConfig config) {
        return this.uvFunction.apply(config);
    }
}
