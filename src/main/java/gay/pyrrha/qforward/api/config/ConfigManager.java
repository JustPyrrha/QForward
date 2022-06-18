package gay.pyrrha.qforward.api.config;

import gay.pyrrha.qforward.QForward;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public interface ConfigManager {
    static ConfigManager getInstance() {
        return QForward.configManager;
    }
    void save();
    void load();
    Config get();
}
