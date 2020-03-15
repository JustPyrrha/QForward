package me.joezwet.fabricforwarding;

import me.joezwet.fabricforwarding.api.config.Config;
import me.joezwet.fabricforwarding.api.config.ConfigManager;
import me.joezwet.fabricforwarding.api.network.ForwardingMode;
import me.joezwet.fabricforwarding.config.ConfigManagerImpl;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.SERVER)
public class FabricForwarding implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static ConfigManager configManager = new ConfigManagerImpl();

    @Override
    public void onInitializeServer() {
        if(Config.getInstance().getMode().equals(ForwardingMode.MODERN) && Config.getInstance().getSecret().length == 0) {
            LOGGER.fatal("Modern IP forwarding enabled but no secret provided, please enter the same secret as the one in your proxy's config.");
        }
    }
}
