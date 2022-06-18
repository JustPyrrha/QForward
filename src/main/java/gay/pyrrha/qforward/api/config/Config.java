package gay.pyrrha.qforward.api.config;

import gay.pyrrha.qforward.api.network.ForwardingMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public interface Config {
    static Config getInstance() {
        return ConfigManager.getInstance().get();
    }
    ForwardingMode getMode();
    byte[] getSecret();
    void invalidMode();
}
