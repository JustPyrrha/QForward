package me.joezwet.fabricforwarding.api.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public enum ForwardingMode {
    /**
     * No forwarding
     */
    OFF,
    /**
     * Bungeecord-style legacy forwarding
     */
    LEGACY,
    /**
     * Modern forwarding, used by Velocity
     */
    MODERN
}
