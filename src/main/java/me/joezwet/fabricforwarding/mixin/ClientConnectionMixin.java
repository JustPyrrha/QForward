package me.joezwet.fabricforwarding.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.joezwet.fabricforwarding.api.network.ClientConnectionBridge;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.SocketAddress;
import java.util.UUID;

@Environment(EnvType.SERVER)
@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements ClientConnectionBridge {

    @Shadow private SocketAddress address;
    private UUID spoofedUUID;
    private Property[] spoofedProfile;
    private GameProfile spoofedGameProfile;

    @Override
    public void setAddress(SocketAddress address) {
        this.address = address;
    }

    @Override
    public UUID getSpoofedUUID() {
        return this.spoofedUUID;
    }

    @Override
    public void setSpoofedUUID(UUID uuid) {
        this.spoofedUUID = uuid;
    }

    @Override
    public Property[] getSpoofedProfile() {
        return this.spoofedProfile;
    }

    @Override
    public void setSpoofedProfile(Property[] profile) {
        this.spoofedProfile = profile;
    }
}
