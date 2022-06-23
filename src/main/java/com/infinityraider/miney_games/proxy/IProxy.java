package com.infinityraider.miney_games.proxy;

import com.infinityraider.infinitylib.container.IInfinityContainerMenuType;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import com.infinityraider.miney_games.config.Config;
import com.infinityraider.miney_games.content.chess.ContainerChessTable;

public interface IProxy extends IProxyBase<Config> {
    default IInfinityContainerMenuType.IGuiFactory<ContainerChessTable> chessGuiFactory() {
        return null;
    }
}
