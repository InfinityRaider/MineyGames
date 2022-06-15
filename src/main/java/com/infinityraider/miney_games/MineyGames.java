package com.infinityraider.miney_games;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.miney_games.config.Config;
import com.infinityraider.miney_games.content.ModContent;
import com.infinityraider.miney_games.proxy.ClientProxy;
import com.infinityraider.miney_games.proxy.IProxy;
import com.infinityraider.miney_games.proxy.ServerProxy;
import com.infinityraider.miney_games.reference.Reference;
import net.minecraftforge.fml.common.Mod;

@Mod(Reference.MOD_ID)
public class MineyGames extends InfinityMod<IProxy, Config> {
    public static MineyGames instance;

    public MineyGames() {
        super();
    }

    @Override
    public String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    protected void onModConstructed() {
        instance = this;
    }

    @Override
    protected IProxy createClientProxy() {
        return new ClientProxy();
    }

    @Override
    protected IProxy createServerProxy() {
        return new ServerProxy();
    }

    @Override
    public ModContentRegistry getModBlockRegistry() {
        return ModContent.getInstance();
    }

    @Override
    public ModContentRegistry getModItemRegistry() {
        return ModContent.getInstance();
    }

    @Override
    public ModContentRegistry getModTileRegistry() {
        return ModContent.getInstance();
    }
}
