package dev.joezwet.fabricforwarding.mixin;

import dev.joezwet.fabricforwarding.api.config.Config;
import dev.joezwet.fabricforwarding.api.network.ForwardingMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.SERVER)
@Mixin({HandshakeC2SPacket.class})
public abstract class HandshakeC2SPacketMixin implements Packet<ServerHandshakePacketListener> {

    @Redirect(
            method = "read(Lnet/minecraft/network/PacketByteBuf;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readString(I)Ljava/lang/String;")
    )
    public String read(final PacketByteBuf buf, final int value) {

        if(Config.getInstance().getMode().equals(ForwardingMode.LEGACY)) {
            return buf.readString(255);
        }

        return buf.readString(Short.MAX_VALUE);
    }
}
