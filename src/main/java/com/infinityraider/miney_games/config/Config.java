package com.infinityraider.miney_games.config;

import com.infinityraider.infinitylib.config.ConfigurationHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public abstract class Config implements ConfigurationHandler.SidedModConfig {
    private final ForgeConfigSpec.BooleanValue debug;

    protected Config(ForgeConfigSpec.Builder builder) {
        builder.push("Debug");
        this.debug = builder.comment("Set to true if you wish to enable debug mode.")
                .define("debug", false);
        builder.pop();
    }

    public boolean isDebugEnabled() {
        return this.debug.get();
    }


    public static class Client extends Config {
        public Client(ForgeConfigSpec.Builder builder) {
            super(builder);
        }

        @Override
        public ModConfig.Type getSide() {
            return ModConfig.Type.CLIENT;
        }
    }

    public static class Server extends Config {
        public Server(ForgeConfigSpec.Builder builder) {
            super(builder);
        }

        @Override
        public ModConfig.Type getSide() {
            return ModConfig.Type.SERVER;
        }
    }
}
