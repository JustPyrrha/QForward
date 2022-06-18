package gay.pyrrha.qforward.proxy;

import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gay.pyrrha.qforward.QForward;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Environment(EnvType.SERVER)
public class Velocity {
    public static final int MODERN_FORWARDING_WITH_KEY = 2;
    public static final int MAX_SUPPORTED_FORWARDING_VERSION = 2;
    public static final Identifier PLAYER_INFO_CHANNEL = new Identifier("velocity", "player_info");

    public static boolean checkIntegrity(final PacketByteBuf buf) {
        final byte[] sig = new byte[32];
        buf.readBytes(sig);

        final byte[] dat = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), dat);

        try {
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(QForward.config.forwardingSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            final byte[] keySig = mac.doFinal(dat);
            if(!MessageDigest.isEqual(sig, keySig)) {
                return false;
            }
        } catch (final InvalidKeyException | NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        return true;
    }

    public static InetAddress readAddress(final PacketByteBuf buf) {
        return InetAddresses.forString(buf.readString(Short.MAX_VALUE));
    }

    public static GameProfile createProfile(final PacketByteBuf buf) {
        final GameProfile profile = new GameProfile(buf.readUuid(), buf.readString(16));
        readProps(buf, profile);
        return profile;
    }

    private static void readProps(final PacketByteBuf buf, final GameProfile profile) {
        final int props = buf.readVarInt();
        for(int i = 0; i < props; i++) {
            final String name = buf.readString(Short.MAX_VALUE);
            final String value = buf.readString(Short.MAX_VALUE);
            final String sig = buf.readBoolean() ? buf.readString(Short.MAX_VALUE) : null;
            profile.getProperties().put(name, new Property(name, value, sig));
        }
    }

    public static PlayerPublicKey.Data readForwardedKey(PacketByteBuf buf) {
        return new PlayerPublicKey.Data(buf);
    }
}
