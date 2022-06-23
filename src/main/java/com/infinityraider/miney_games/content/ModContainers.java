package com.infinityraider.miney_games.content;

import com.infinityraider.infinitylib.container.InfinityContainerMenuType;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;
import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.content.chess.ContainerChessTable;
import com.infinityraider.miney_games.reference.Names;

public final class ModContainers extends ModContentRegistry {
    private static final ModContainers INSTANCE = new ModContainers();

    public static ModContainers getInstance() {
        return INSTANCE;
    }

    public final RegistryInitializer<InfinityContainerMenuType<ContainerChessTable>> CHESS_TABLE_MENU_TYPE;

    private ModContainers() {
        this.CHESS_TABLE_MENU_TYPE = this.menuType(() ->
                InfinityContainerMenuType.builder(Names.CHESS_TABLE, (id, inv, data) -> new ContainerChessTable(id, inv, data.readBlockPos()))
                        .setGuiFactory(MineyGames.instance.proxy().chessGuiFactory())
                        .build()
        );
    }
}
