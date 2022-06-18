package gay.pyrrha.qforward;

import gay.pyrrha.qforward.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.server.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.SERVER)
public class QForward implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(QForward.class);
    public static ModConfig config;

    @Override
    public void onInitializeServer(ModContainer mod) {
        config = ModConfig.load();
        if(config.enableForwarding() && config.forwardingSecret().length() == 0) {
            LOGGER.error("[QForward] Modern IP forwarding enabled but no secret provided, please enter the same secret as the one in your proxy's config. Disabling forwarding.");
            config.enableForwarding(false);
            return;
        }
        LOGGER.info("[QForward] Ready.");
    }
}
