package xyz.louiszn.offlinemodefix.client.mixin;

//? if >= 26.1 {
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
//? } else {
/*import net.minecraft.client.GuiMessageTag;
*///?}

import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.louiszn.offlinemodefix.client.ClientUtil;
import xyz.louiszn.offlinemodefix.client.Config;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    //? if >= 26.1 {
    @SuppressWarnings("ModifyVariableMayUseName")
    //?}
    @ModifyVariable(
            //? if >= 26.1 {
            method = "addMessage",
            //?} else {
            /*method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            *///?}
            at = @At("HEAD"),
            argsOnly = true
    )
    private GuiMessageTag removeUnsecureMessageTag(GuiMessageTag tag) {
        if (!Config.INSTANCE.removeUnsecureMessageTag) {
            return tag;
        }

        if (!GuiMessageTag.chatNotSecure().equals(tag)) {
            return tag;
        }

        if (Config.INSTANCE.overrideOnlineBehavior || ClientUtil.isUnauthenticatedClient()) {
            return null;
        }

        return tag;
    }
}
