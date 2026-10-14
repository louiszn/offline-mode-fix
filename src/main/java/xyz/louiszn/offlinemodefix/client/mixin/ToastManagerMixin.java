package xyz.louiszn.offlinemodefix.client.mixin;

import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
//? if >=1.21.2 {
import net.minecraft.client.gui.components.toasts.ToastManager;
//?} else {
/*import net.minecraft.client.gui.components.toasts.ToastComponent;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.louiszn.offlinemodefix.client.Config;

//? if >=1.21.2 {
@Mixin(ToastManager.class)
//?} else {
/*@Mixin(ToastComponent.class)
*///?}
public class ToastManagerMixin {
    @Inject(method = "addToast", at = @At("HEAD"), cancellable = true)
    public void blockUnsecureServerToast(Toast toast, CallbackInfo ci) {
        if (Config.INSTANCE.hideUnverifiedToast && toast instanceof SystemToast systemToast) {
            if (systemToast.getToken() == SystemToast.SystemToastId.UNSECURE_SERVER_WARNING) {
                ci.cancel();
            }
        }
    }
}
