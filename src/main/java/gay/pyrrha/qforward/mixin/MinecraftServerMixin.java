package gay.pyrrha.qforward.mixin;

import gay.pyrrha.qforward.QForward;
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
        if(QForward.config.enableForwarding())
            cir.setReturnValue(-1);
    }
}
