package me.joezwet.fabricforwarding.api.config;

import me.joezwet.fabricforwarding.api.network.ForwardingMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public interface Config {

    static Config getInstance() {
        return ConfigManager.getInstance().get();
    }

    ForwardingMode getMode();
    byte[] getSecret();
}
