package xyz.louiszn.offlinemodefix.client.mixin;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilServicesKeyInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(YggdrasilServicesKeyInfo.class)
public class YggdrasilMixin {
    /**
     * Intercepts the property validation (used for skins/textures).
     * If the signature is a "dummy" string like BUILDER_APPALLED_PUMPKIN,
     * this method normally crashes attempting to Base64 decode it.
     * We force it to return true to allow the texture to load anyway.
     */
    @Inject(method = "validateProperty", at = @At("HEAD"), cancellable = true)
    private void bypassSignatureValidation(Property property, CallbackInfoReturnable<Boolean> cir) {
        String signature = property.signature();

        // If the signature is obviously not Base64 (contains underscores or non-base64 chars)
        // or if you just want to allow all textures in offline mode:
        if (signature != null && (signature.contains("_") || signature.contains(" "))) {
            cir.setReturnValue(true);
        }
    }
}
