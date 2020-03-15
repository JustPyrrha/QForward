package me.joezwet.fabricforwarding.mixin;


import me.joezwet.fabricforwarding.api.config.Config;
import me.joezwet.fabricforwarding.api.network.ForwardingMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.SERVER)
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "getNetworkCompressionThreshold", at = @At("RETURN"), cancellable = true)
    public void getNetworkCompressionThreshold(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Config.getInstance().getMode().equals(ForwardingMode.OFF) ? 256 : -1);
    }
}
