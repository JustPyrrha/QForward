package dev.joezwet.fabricforwarding.api.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.net.SocketAddress;
import java.util.UUID;

@Environment(EnvType.SERVER)
public interface ClientConnectionBridge {
    void setAddress(SocketAddress address);
    UUID getSpoofedUUID();
    void setSpoofedUUID(UUID uuid);
    Property[] getSpoofedProfile();
    void setSpoofedProfile(Property[] profile);
}
