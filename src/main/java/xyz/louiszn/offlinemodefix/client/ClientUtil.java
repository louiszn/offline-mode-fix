package xyz.louiszn.offlinemodefix.client;

import net.minecraft.client.Minecraft;

import java.util.UUID;

//? if >= 1.21.11 {
import net.minecraft.util.Util;
//?} else {
/*import net.minecraft.Util;
 *///?}

public class ClientUtil {
    public static boolean isUnauthenticatedClient() {
        return isUnauthenticatedUUID(Minecraft.getInstance().getUser().getProfileId());
    }

    public static boolean isUnauthenticatedUUID(UUID uuid) {
        return uuid == null || uuid.version() == 3 || uuid.equals(Util.NIL_UUID);
    }
}
