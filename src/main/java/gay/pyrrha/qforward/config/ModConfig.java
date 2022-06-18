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
            Codec.STRING.fieldOf("forwarding_secret_file").forGetter(config -> config.forwardingSecretFile)
    ).apply(instance, ModConfig::new));

    private boolean enableForwarding;
    private final String forwardingSecretFile;
    private String forwardingSecret;

    public ModConfig(boolean enableForwarding, String forwardingSecretFile) {
        this.enableForwarding = enableForwarding;
        this.forwardingSecretFile = forwardingSecretFile;
    }

    public ModConfig() {
        this(false, "forwarding.secret");
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

    private void forwardingSecret(String value) {
        this.forwardingSecret = value;
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
                var config = result.result().orElseGet(ModConfig::new);
                config.forwardingSecret(readForwardingSecret(config));
                return config;
            } catch (IOException e) {
                QForward.LOGGER.warn("[QForward] Failed loading config.", e);
                return new ModConfig();
            }
        }
    }

    private static String readForwardingSecret(ModConfig config) {
        if(config.forwardingSecretFile.equals("")) return "";
        if(config.forwardingSecretFile.startsWith("env:")) {
            var envVar = config.forwardingSecretFile.substring("env:".length()).toUpperCase();
            var secret = System.getenv(envVar);
            if(secret == null) return "";
            return secret;
        }

        var path = QuiltLoader.getConfigDir().resolve(config.forwardingSecretFile);
        if(!Files.exists(path)) {
            try {
                Files.writeString(path, "");
                QForward.LOGGER.warn("[QForward] Created missing forwarding secret file, ");
            } catch (IOException e) {
                QForward.LOGGER.warn("[QForward] Failed creating default forwarding secret file.", e);
            }
        } else {
            try {
                return Files.readString(path);
            } catch (IOException e) {
                QForward.LOGGER.warn("[QForward] Failed reading forwarding secret file.", e);
            }
        }
        return "";
    }
}
