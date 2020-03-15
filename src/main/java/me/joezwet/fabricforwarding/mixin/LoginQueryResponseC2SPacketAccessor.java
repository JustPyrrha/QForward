package me.joezwet.fabricforwarding.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.SERVER)
@Mixin(LoginQueryResponseC2SPacket.class)
public interface LoginQueryResponseC2SPacketAccessor {
    @Accessor
    int getQueryId();

    @Accessor
    void setQueryId(int queryId);

    @Accessor
    PacketByteBuf getResponse();

    @Accessor
    void setResponse(PacketByteBuf response);
}
