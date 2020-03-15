package me.joezwet.fabricforwarding.api.config;

import me.joezwet.fabricforwarding.FabricForwarding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public interface ConfigManager {

    static ConfigManager getInstance() {
        return FabricForwarding.configManager;
    }

    void save();
    void load();
    Config get();
}
