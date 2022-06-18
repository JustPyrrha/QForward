package gay.pyrrha.qforward.mixin;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gay.pyrrha.qforward.api.config.Config;
import gay.pyrrha.qforward.api.network.ClientConnectionBridge;
import gay.pyrrha.qforward.api.network.ForwardingMode;
import gay.pyrrha.qforward.proxy.Velocity;
import io.netty.buffer.Unpooled;
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

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.UUID;

@Environment(EnvType.SERVER)
@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {
    @Shadow @Final private static Random RANDOM;
    @Shadow @Final private MinecraftServer server;
    @Shadow @Final public ClientConnection connection;
    @Shadow private GameProfile profile;
    private int velocityLoginMsgId = -1;
    @Shadow public abstract void disconnect(Text reason);
    @Shadow public abstract void acceptPlayer();

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
            LoginQueryRequestS2CPacket p = new LoginQueryRequestS2CPacket(buf);
            this.connection.send(p);
            info.cancel();
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
