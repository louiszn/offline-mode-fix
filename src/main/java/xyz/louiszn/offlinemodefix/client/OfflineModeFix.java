package xyz.louiszn.offlinemodefix.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OfflineModeFix implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("OfflineModeFix");
    public static final String VERSION = /*$ mod_version*/ "0.1.0";
    public static final String MINECRAFT = /*$ minecraft*/ "1.20.6";

    @Override
    public void onInitializeClient() {
        LOGGER.info("Hello Fabric world! " + MINECRAFT);
    }

    /**
     * Adapts to the {@link ResourceLocation} changes introduced in 1.21.
     */
    public static ResourceLocation id(String namespace, String path) {
        //? if <1.21 {
        return new ResourceLocation(namespace, path);
         //?} else
        //return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
