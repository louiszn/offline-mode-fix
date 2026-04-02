package xyz.louiszn.offlinemodefix.client.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >= 1.21.11 {
/*import net.minecraft.util.Util;
*///?} else {
import net.minecraft.Util;
//?}


import java.util.UUID;

/**
 * This Mixin intercepts the core Minecraft client class to bypass network-dependent
 * chat checks. This prevents the massive 30-second lag spikes that occur on
 * offline servers when the client fails to reach Mojang/Microsoft API servers.
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {
    /**
     * Intercepts the player block list check.
     * Normally, this checks the playerSocialManager, which makes a hanging web request
     * in offline mode. By cancelling at HEAD and returning false, we tell the game
     * "no players are blocked," instantly allowing the chat message to render.
     */
    @Inject(method = "isBlocked", at = @At("HEAD"), cancellable = true)
    public void bypassIsBlocked(UUID uUID, CallbackInfoReturnable<Boolean> cir) {
        // Bypass the network check if:
        // 1. The UUID is null
        // 2. The UUID is Version 3 (Offline Mode Player)
        // 3. The UUID is NIL_UUID (System Messages / Server Console)
        if (uUID == null || uUID.version() == 3 || uUID.equals(Util.NIL_UUID)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Intercepts the text filtering (profanity filter) status check.
     * Normally, this checks userProperties, which also tries to fetch data from
     * Mojang's servers. By returning false, we force the client to think the filter
     * is disabled, preventing any secondary offline-mode lag spikes.
     */
    @Inject(method = "isTextFilteringEnabled", at = @At("HEAD"), cancellable = true)
    public void bypassTextFiltering(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    /**
     * Intercepts the multiplayer preparation phase when joining a server.
     * Normally, this tries to download your blocklist and fetch cryptographic
     * chat signing keys from Mojang's API. Cancelling this stops 401 Unauthorized
     * background errors and prevents additional network hangs.
     */
    @Inject(method = "prepareForMultiplayer", at = @At("HEAD"), cancellable = true)
    public void bypassPrepareForMultiplayer(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;

        // Check if YOUR local account is an offline/cracked account (Version 3 UUID)
        if (client.getUser().getProfileId().version() == 3) {
            // Cancel the Mojang API check to prevent the 401 Unauthorized spam
            ci.cancel();
        }
    }
}
