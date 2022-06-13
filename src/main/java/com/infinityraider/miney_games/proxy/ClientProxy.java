package com.infinityraider.miney_games.proxy;

import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import com.infinityraider.miney_games.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Function;

public class ClientProxy implements IProxy, IClientProxyBase<Config> {
    @Override
    public Function<ForgeConfigSpec.Builder, Config> getConfigConstructor() {
        return Config.Client::new;
    }

    @Override
    public void registerEventHandlers() {

    }

    @Override
    public void activateRequiredModules() {

    }

    @Override
    public void registerCapabilities() {

    }
}
