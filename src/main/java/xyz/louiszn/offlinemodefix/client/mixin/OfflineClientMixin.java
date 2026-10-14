package xyz.louiszn.offlinemodefix.client.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import xyz.louiszn.offlinemodefix.client.ClientUtil;
import xyz.louiszn.offlinemodefix.client.Config;

import java.util.UUID;

@Mixin(Minecraft.class)
public class OfflineClientMixin {
    @Inject(method = "isBlocked", at = @At("HEAD"), cancellable = true)
    public void disablePlayerBlocklistCheck(UUID uuid, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.INSTANCE.disableChatChecks) {
            return;
        }

        if (Config.INSTANCE.overrideOnlineBehavior || ClientUtil.isUnauthenticatedUUID(uuid)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isTextFilteringEnabled", at = @At("HEAD"), cancellable = true)
    public void disableTextFiltering(CallbackInfoReturnable<Boolean> cir) {
        if (!Config.INSTANCE.disableChatChecks) {
            return;
        }

        if (Config.INSTANCE.overrideOnlineBehavior || ClientUtil.isUnauthenticatedClient()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "prepareForMultiplayer", at = @At("HEAD"), cancellable = true)
    public void skipPrepareForOnlineMultiplayer(CallbackInfo ci) {
        if (!Config.INSTANCE.skipMultiplayerPreparing) {
            return;
        }

        if (Config.INSTANCE.overrideOnlineBehavior || ClientUtil.isUnauthenticatedClient()) {
            ci.cancel();
        }
    }
}
