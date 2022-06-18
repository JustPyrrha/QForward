package gay.pyrrha.qforward.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.pyrrha.qforward.QForward;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.quiltmc.loader.api.QuiltLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Environment(EnvType.SERVER)
public class ModConfig {
    public static final Codec<ModConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("enable_forwarding").forGetter(config -> config.enableForwarding),
            Codec.STRING.fieldOf("forwarding_secret").forGetter(config -> config.forwardingSecret)
    ).apply(instance, ModConfig::new));

    private boolean enableForwarding;
    private final String forwardingSecret;

    public ModConfig(boolean enableForwarding, String forwardingSecret) {
        this.enableForwarding = enableForwarding;
        this.forwardingSecret = forwardingSecret;
    }

    public ModConfig() {
        this(false, "");
    }

    public boolean enableForwarding() {
        return this.enableForwarding;
    }
    public void enableForwarding(boolean value) {
        this.enableForwarding = value;
    }

    public String forwardingSecret() {
        return this.forwardingSecret;
    }

    public static ModConfig load() {
        var path = QuiltLoader.getConfigDir().resolve("qforward.json");
        if(!Files.exists(path)) {
            var config = new ModConfig();
            Optional<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, config).result();
            if(result.isPresent()) {
                try {
                    Files.writeString(path, new GsonBuilder().setPrettyPrinting().create().toJson(result.get()));
                } catch (IOException e) {
                    QForward.LOGGER.warn("[QForward] Failed saving default config.", e);
                }
            } else {
                QForward.LOGGER.warn("[QForward] Failed saving default config.");
            }
            return new ModConfig();
        } else {
            try {
                var raw = Files.readString(path);
                var json = JsonParser.parseString(raw);
                var result = CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);
                var error = result.error();
                error.ifPresent(e -> QForward.LOGGER.warn("[QForward] Failed loading config: {}", e.message()));
                return result.result().orElseGet(ModConfig::new);
            } catch (IOException e) {
                QForward.LOGGER.warn("[QForward] Failed loading config.", e);
                return new ModConfig();
            }
        }
    }
}
