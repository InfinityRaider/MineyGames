package com.infinityraider.miney_games;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.miney_games.config.Config;
import com.infinityraider.miney_games.content.*;
import com.infinityraider.miney_games.network.chess.*;
import com.infinityraider.miney_games.proxy.*;
import com.infinityraider.miney_games.reference.Reference;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

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
        return ModBlocks.getInstance();
    }

    @Override
    public ModContentRegistry getModItemRegistry() {
        return ModItems.getInstance();
    }

    @Override
    public ModContentRegistry getModTileRegistry() {
        return ModTiles.getInstance();
    }

    @Nullable
    @Override
    public ModContentRegistry getModContainerRegistry() {
        return ModContainers.getInstance();
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        wrapper.registerMessage(MessageChessGameStart.ToClient.class);
        wrapper.registerMessage(MessageChessGameStart.ToServer.class);
        wrapper.registerMessage(MessageChessPlayerReady.ToClient.class);
        wrapper.registerMessage(MessageChessPlayerReady.ToServer.class);
        wrapper.registerMessage(MessageChessPlayerResign.ToClient.class);
        wrapper.registerMessage(MessageChessPlayerResign.ToServer.class);
        wrapper.registerMessage(MessageChessPlayerSet.ToClient.class);
        wrapper.registerMessage(MessageChessPlayerSet.ToServer.class);
        wrapper.registerMessage(MessageSelectSquare.class);
        wrapper.registerMessage(MessageSyncChessMove.class);
    }
}
