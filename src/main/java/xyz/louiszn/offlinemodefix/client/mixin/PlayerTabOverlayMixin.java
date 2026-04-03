package xyz.louiszn.offlinemodefix.client.mixin;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
    @ModifyVariable(
            //? if >= 26.1 {
            method = "extractRenderState",
            //?} else {
            /*method = "render",
            *///?}
            at = @At("STORE"), ordinal = 0
    )
    public boolean forceHeadDisplay(boolean original) {
        return true;
    }
}
