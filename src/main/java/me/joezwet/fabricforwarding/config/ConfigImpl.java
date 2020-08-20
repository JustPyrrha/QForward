package me.joezwet.fabricforwarding.config;

import com.google.common.base.Charsets;
import com.google.gson.annotations.Expose;
import me.joezwet.fabricforwarding.api.config.Config;
import me.joezwet.fabricforwarding.api.network.ForwardingMode;
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
