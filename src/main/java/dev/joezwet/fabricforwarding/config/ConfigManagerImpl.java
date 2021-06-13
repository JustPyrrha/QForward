package dev.joezwet.fabricforwarding.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.joezwet.fabricforwarding.api.config.Config;
import dev.joezwet.fabricforwarding.api.config.ConfigManager;
import dev.joezwet.fabricforwarding.FabricForwarding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@Environment(EnvType.SERVER)
public class ConfigManagerImpl implements ConfigManager {

    private Config config = new ConfigImpl();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File file = new File(FabricLoader.getInstance().getConfigDirectory(), "fabric-forwarding.json");

    public ConfigManagerImpl() {
        this.load();
    }

    @Override
    public void save() {
        try {
            FileUtils.writeStringToFile(this.file, this.gson.toJson(this.config), Charset.defaultCharset());
        } catch (IOException e) {
            FabricForwarding.LOGGER.error("[Forwarding] Failed to save config.", e);
        }
    }

    @Override
    public void load() {
        try {
            this.file.getParentFile().mkdirs();
            if(!this.file.exists()) {
                FabricForwarding.LOGGER.info("[Forwarding] Failed to find config file, creating one.");
                this.save();
            } else {
                byte[] bytes = Files.readAllBytes(Paths.get(this.file.getPath()));
                this.config = this.gson.fromJson(new String(bytes, Charset.defaultCharset()), ConfigImpl.class);
            }
        } catch (IOException e) {
            FabricForwarding.LOGGER.error("[Forwarding] Failed to load config.", e);
        }
    }

    @Override
    public Config get() {
        return this.config;
    }
}
