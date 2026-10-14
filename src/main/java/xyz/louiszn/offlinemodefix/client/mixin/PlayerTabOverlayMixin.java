package xyz.louiszn.offlinemodefix.client.mixin;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.louiszn.offlinemodefix.client.Config;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
    @ModifyVariable(
            //? if >= 26.1 {
            method = "extractRenderState",
            name = "showHead",
            //?} else {
            /*method = "render",
            ordinal = 0,
            *///?}
            at = @At("STORE")
    )
    public boolean forceTabHeads(boolean original) {
        return Config.INSTANCE.forceTabHeads;
    }
}
