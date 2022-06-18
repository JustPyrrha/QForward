package gay.pyrrha.qforward.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gay.pyrrha.qforward.api.config.Config;
import gay.pyrrha.qforward.api.config.ConfigManager;
import gay.pyrrha.qforward.QForward;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import org.quiltmc.loader.api.QuiltLoader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@Environment(EnvType.SERVER)
public class ConfigManagerImpl implements ConfigManager {

    private Config config = new ConfigImpl();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File oldFile = new File(QuiltLoader.getConfigDir().toFile(), "fabric-forwarding.json");
    private File file = new File(QuiltLoader.getConfigDir().toFile(), "qforward.json");

    public ConfigManagerImpl() {
        this.load();
    }

    @Override
    public void save() {
        try {
            FileUtils.writeStringToFile(this.file, this.gson.toJson(this.config), Charset.defaultCharset());
        } catch (IOException e) {
            QForward.LOGGER.error("[QForward] Failed to save config.", e);
        }
    }

    @Override
    public void load() {
        try {
            if(this.oldFile.exists()) {
                this.oldFile.renameTo(this.file);
            }
            this.file.getParentFile().mkdirs();
            if(!this.file.exists()) {
                QForward.LOGGER.info("[QForward] Failed to find config file, creating one.");
                this.save();
            } else {
                byte[] bytes = Files.readAllBytes(Paths.get(this.file.getPath()));
                this.config = this.gson.fromJson(new String(bytes, Charset.defaultCharset()), ConfigImpl.class);
            }
        } catch (IOException e) {
            QForward.LOGGER.error("[QForward] Failed to load config.", e);
        }
    }

    @Override
    public Config get() {
        return this.config;
    }
}
