package me.joezwet.fabricforwarding.mixin;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.buffer.Unpooled;
import me.joezwet.fabricforwarding.api.config.Config;
import me.joezwet.fabricforwarding.api.network.ForwardingMode;
import me.joezwet.fabricforwarding.api.network.ClientConnectionBridge;
import me.joezwet.fabricforwarding.proxy.Velocity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.network.PacketByteBuf;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.UUID;

@Environment(EnvType.SERVER)
@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {

    @Shadow @Final private MinecraftServer server;
    @Shadow @Final public ClientConnection connection;
    @Shadow private GameProfile profile;
    @Shadow public abstract void disconnect(Text reason);

    @Shadow public abstract void acceptPlayer();

    @Shadow @Final private static Random RANDOM;
    private int velocityLoginMsgId = -1;

    @Inject(method = "onHello",
        at = @At(
                value = "FIELD",
                target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;profile:Lcom/mojang/authlib/GameProfile;",
                opcode = Opcodes.PUTFIELD,
                ordinal = 0,
                shift = At.Shift.AFTER
        )
    )
    private void onHelloBungee(LoginHelloC2SPacket packet, CallbackInfo info) {
        if(Config.getInstance().getMode().equals(ForwardingMode.LEGACY) && !this.server.isOnlineMode()) {
            final UUID uuid;
            if(((ClientConnectionBridge)this.connection).getSpoofedUUID() != null) {
                uuid = ((ClientConnectionBridge)this.connection).getSpoofedUUID();
            } else {
                uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.profile.getName()).getBytes(Charsets.UTF_8));
            }

            this.profile = new GameProfile(uuid, this.profile.getName());

            if(((ClientConnectionBridge)this.connection).getSpoofedProfile() != null) {
                for(final Property p : ((ClientConnectionBridge)this.connection).getSpoofedProfile()) {
                    this.profile.getProperties().put(p.getName(), p);
                }
            }
        }
    }

    @Inject(method = "onHello",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;state:Lnet/minecraft/server/network/ServerLoginNetworkHandler$State;",
                    ordinal = 2,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void onHelloVelocity(LoginHelloC2SPacket packet, CallbackInfo info) {
        if(Config.getInstance().getMode().equals(ForwardingMode.MODERN)) {
            this.velocityLoginMsgId = RANDOM.nextInt();
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeVarInt(velocityLoginMsgId);
            buf.writeIdentifier(Velocity.PLAYER_INFO_CHANNEL);
            //buf.writeBytes(Unpooled.EMPTY_BUFFER);
            LoginQueryRequestS2CPacket p = new LoginQueryRequestS2CPacket();
            try {
                p.read(buf);
                this.connection.send(p);
                info.cancel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Inject(method = "onQueryResponse", at = @At("HEAD"), cancellable = true)
    private void onQueryResponse(LoginQueryResponseC2SPacket packet, CallbackInfo info) {
        if(Config.getInstance().getMode().equals(ForwardingMode.MODERN) && packet.queryId == this.velocityLoginMsgId) {
            PacketByteBuf buf = packet.response;
            if(buf == null) {
                this.disconnect(new LiteralText("This server requires you to join via a proxy."));
                info.cancel();
            }
            if(!Velocity.checkIntegrity(buf)) {
                this.disconnect(new LiteralText("Unable to verify player details."));
                info.cancel();
            }
            ((ClientConnectionBridge)this.connection).setAddress(new InetSocketAddress(Velocity.readAddress(buf), ((InetSocketAddress)this.connection.getAddress()).getPort()));
            this.profile = Velocity.createProfile(buf);
            this.server.execute(() -> {
                try {
                    this.acceptPlayer();
                } catch (Exception e) {
                    this.disconnect(new LiteralText("Failed to verify username."));
                }
            });
            info.cancel();
        }
    }
}
