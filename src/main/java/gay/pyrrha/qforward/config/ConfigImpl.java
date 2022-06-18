package gay.pyrrha.qforward.config;

import com.google.common.base.Charsets;
import com.google.gson.annotations.Expose;
import gay.pyrrha.qforward.api.config.Config;
import gay.pyrrha.qforward.api.network.ForwardingMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public class ConfigImpl implements Config {

    @Expose private String mode = "";
    @Expose private String secret = "";

    @Override
    public ForwardingMode getMode() {
        try {
            return ForwardingMode.valueOf(this.mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ForwardingMode.OFF;
        }
    }

    @Override
    public byte[] getSecret() {
        return this.secret.getBytes(Charsets.UTF_8);
    }

    @Override
    public void invalidMode() {
        this.mode = "OFF";
    }
}
