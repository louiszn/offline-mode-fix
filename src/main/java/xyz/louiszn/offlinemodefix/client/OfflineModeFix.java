package xyz.louiszn.offlinemodefix.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OfflineModeFix implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("OfflineModeFix");
    public static final String VERSION = /*$ mod_version*/ "1.0.1";
    public static final String MINECRAFT = /*$ minecraft*/ "26.3";

    @Override
    public void onInitializeClient() {
        LOGGER.info("Loaded version {} for Minecraft {}", VERSION, MINECRAFT);
    }
}
