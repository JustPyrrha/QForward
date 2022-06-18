package gay.pyrrha.qforward.api.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public enum ForwardingMode {
    /**
     * No forwarding
     */
    OFF,
    /**
     * Modern forwarding, used by Velocity
     */
    MODERN
}
