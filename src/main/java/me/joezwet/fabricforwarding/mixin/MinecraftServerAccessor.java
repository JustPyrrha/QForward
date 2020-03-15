package me.joezwet.fabricforwarding.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.SERVER)
@Mixin({MinecraftServer.class})
public interface MinecraftServerAccessor {
    @Accessor
    boolean isOnlineMode();
}
