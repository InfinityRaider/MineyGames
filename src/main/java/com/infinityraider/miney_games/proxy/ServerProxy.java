package com.infinityraider.miney_games.proxy;

import com.infinityraider.infinitylib.proxy.base.IServerProxyBase;
import com.infinityraider.miney_games.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Function;

public class ServerProxy implements IProxy, IServerProxyBase<Config> {
    @Override
    public Function<ForgeConfigSpec.Builder, Config> getConfigConstructor() {
        return Config.Server::new;
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
