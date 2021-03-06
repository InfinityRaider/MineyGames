package com.infinityraider.miney_games.proxy;

import com.infinityraider.infinitylib.container.IInfinityContainerMenuType;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import com.infinityraider.miney_games.client.gui.GuiMineyGame;
import com.infinityraider.miney_games.client.gui.chess.ChessTableGui;
import com.infinityraider.miney_games.client.handler.ModelHandler;
import com.infinityraider.miney_games.config.Config;
import com.infinityraider.miney_games.content.chess.ContainerChessTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy, IClientProxyBase<Config> {
    @Override
    public Function<ForgeConfigSpec.Builder, Config> getConfigConstructor() {
        return Config.Client::new;
    }

    @Override
    public void registerEventHandlers() {

    }

    @Override
    public void registerModBusEventHandlers(IEventBus bus) {
        IProxy.super.registerModBusEventHandlers(bus);
        bus.addListener(ModelHandler.getInstance()::registerSpecialModels);
    }

    @Override
    public void activateRequiredModules() {

    }

    @Override
    public void registerCapabilities() {

    }

    @Override
    public IInfinityContainerMenuType.IGuiFactory<ContainerChessTable> chessGuiFactory() {
        return new IInfinityContainerMenuType.IGuiFactory<ContainerChessTable>() {
            @Override
            @SuppressWarnings("unchecked")
            public MenuScreens.ScreenConstructor<ContainerChessTable, ChessTableGui> getGuiScreenProvider() {
                return ChessTableGui.getProvider();
            }
        };
    }

    @Override
    public void updateMineyGameGui() {
        Screen screen = Minecraft.getInstance().screen;
        if(screen instanceof GuiMineyGame) {
            ((GuiMineyGame<?,?,?>) screen).updateGuiState();
        }
    }
}
