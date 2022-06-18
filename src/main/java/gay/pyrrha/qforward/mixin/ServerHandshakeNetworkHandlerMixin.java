package gay.pyrrha.qforward.mixin;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import gay.pyrrha.qforward.api.config.Config;
import gay.pyrrha.qforward.api.network.ClientConnectionBridge;
import gay.pyrrha.qforward.api.network.ForwardingMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Environment(EnvType.SERVER)
@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {

    @Shadow @Final private ClientConnection connection;

    private static final Gson gson = new Gson();

    @Inject(method = "onHandshake", at = @At("HEAD"))
    private void onHandshake(HandshakeC2SPacket packet, CallbackInfo info) {
        if(Config.getInstance().getMode().equals(ForwardingMode.MODERN) && packet.getIntendedState().equals(NetworkState.LOGIN)) {
            String[] addressSplit = packet.address.split("\00\\|", 2)[0].split("\00");

            if(addressSplit.length == 3 || addressSplit.length == 4) {
                packet.address = addressSplit[0];
                ((ClientConnectionBridge)this.connection).setAddress(new InetSocketAddress(addressSplit[1],
                        ((InetSocketAddress)this.connection.getAddress()).getPort()));
                ((ClientConnectionBridge)this.connection).setSpoofedUUID(UUIDTypeAdapter.fromString(addressSplit[2]));

                if(addressSplit.length == 4) {
                    ((ClientConnectionBridge)this.connection).setSpoofedProfile(gson.fromJson(addressSplit[3], Property[].class));
                }
            }
        } else {
            Text text = new LiteralText("If you wish to use IP forwarding, please enable it in your proxy's config.");
            this.connection.send(new DisconnectS2CPacket(text));
            this.connection.disconnect(text);
        }
    }
}
