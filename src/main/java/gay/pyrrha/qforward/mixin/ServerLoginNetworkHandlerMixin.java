package gay.pyrrha.qforward.mixin;

import com.mojang.authlib.GameProfile;
import gay.pyrrha.qforward.QForward;
import gay.pyrrha.qforward.proxy.Velocity;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.random.RandomGenerator;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Environment(EnvType.SERVER)
@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {
    @Shadow @Final private static RandomGenerator RANDOM;
    @Shadow @Final public ClientConnection connection;
    @Shadow @Final MinecraftServer server;
    @Shadow GameProfile profile;
    @Shadow private @Nullable PlayerPublicKey field_39023;
    private int velocityLoginMsgId = -1;
    @Shadow public abstract void disconnect(Text reason);
    @Shadow public abstract void acceptPlayer();

    @Inject(method = "onHello",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;state:Lnet/minecraft/server/network/ServerLoginNetworkHandler$State;",
                    ordinal = 3,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void onHelloVelocity(LoginHelloC2SPacket packet, CallbackInfo info) {
        if(QForward.config.enableForwarding()) {
            this.velocityLoginMsgId = RANDOM.nextInt();
            var buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeByte(Velocity.MAX_SUPPORTED_FORWARDING_VERSION);
            var pak = new LoginQueryRequestS2CPacket(this.velocityLoginMsgId, Velocity.PLAYER_INFO_CHANNEL, buf);
            this.connection.send(pak);
            info.cancel();
        }
    }

    @Inject(method = "onQueryResponse", at = @At("HEAD"), cancellable = true)
    private void onQueryResponse(LoginQueryResponseC2SPacket packet, CallbackInfo info) {
        if(QForward.config.enableForwarding() && packet.queryId == this.velocityLoginMsgId) {
            PacketByteBuf buf = packet.getResponse();
            if(buf == null) {
                this.disconnect(Text.literal("This server requires you to join via a proxy."));
                info.cancel();
            }
            if(!Velocity.checkIntegrity(buf)) {
                this.disconnect(Text.literal("Unable to verify player details."));
                info.cancel();
            }
            var version = buf.readVarInt();
            if(version > Velocity.MAX_SUPPORTED_FORWARDING_VERSION) {
                throw new IllegalStateException("Unsupported forwarding versions %s, wanted up to %s.".formatted(version, Velocity.MAX_SUPPORTED_FORWARDING_VERSION));
            }
            var listening = this.connection.getAddress();
            var port = 0;
            if(listening instanceof InetSocketAddress) {
                port = ((InetSocketAddress) listening).getPort();
            }
            this.connection.address = new InetSocketAddress(Velocity.readAddress(buf), port);
            this.profile = Velocity.createProfile(buf);

            if(version >= Velocity.MODERN_FORWARDING_WITH_KEY) {
                final var key = Velocity.readForwardedKey(buf);
                if(this.field_39023 == null) {
                    try {
                        this.field_39023 = PlayerPublicKey.createValidated(this.server.getSignatureValidator(), key);
                    } catch (NetworkEncryptionException e) {
                        this.disconnect(Text.literal("Failed to validate forwarded player key."));
                    }
                }
            }
            this.server.execute(() -> {
                try {
                    this.acceptPlayer();
                } catch (Exception e) {
                    this.disconnect(Text.literal("Failed to verify username."));
                }
            });
            info.cancel();
        }
    }
}
