package xyz.louiszn.offlinemodefix.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("offlinemodefix.json");

    public static final boolean DEFAULT_OVERRIDE_ONLINE_BEHAVIOR = false;
    public static final boolean DEFAULT_DISABLE_CHAT_CHECKS = true;
    public static final boolean DEFAULT_SKIP_MULTIPLAYER_PREPARING = true;
    public static final boolean DEFAULT_REMOVE_UNSECURE_MESSAGE_TAG = true;
    public static final boolean DEFAULT_FORCE_TAB_HEADS = true;
    public static final boolean DEFAULT_HIDE_UNVERIFIED_TOAST = true;

    public static final Config INSTANCE = load();

    public boolean overrideOnlineBehavior = DEFAULT_OVERRIDE_ONLINE_BEHAVIOR;
    public boolean disableChatChecks = DEFAULT_DISABLE_CHAT_CHECKS;
    public boolean skipMultiplayerPreparing = DEFAULT_SKIP_MULTIPLAYER_PREPARING;
    public boolean removeUnsecureMessageTag = DEFAULT_REMOVE_UNSECURE_MESSAGE_TAG;
    public boolean forceTabHeads = DEFAULT_FORCE_TAB_HEADS;
    public boolean hideUnverifiedToast = DEFAULT_HIDE_UNVERIFIED_TOAST;

    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(this));
        } catch (IOException e) {
            OfflineModeFix.LOGGER.error("Failed to save config", e);
        }
    }

    private static Config load() {
        if (Files.exists(PATH)) {
            try {
                return GSON.fromJson(Files.readString(PATH), Config.class);
            } catch (Exception e) {
                OfflineModeFix.LOGGER.error("Failed to load config, using defaults", e);
            }
        }

        Config cfg = new Config();
        cfg.save();

        return cfg;
    }
}
