package xyz.louiszn.offlinemodefix.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public final class ConfigScreen {
    private ConfigScreen() {}

    public static Screen create(Screen parent) {
        ConfigBuilder config = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Offline Mode Fix"))
                .setSavingRunnable(Config.INSTANCE::save);

        ConfigCategory category = config.getOrCreateCategory(Component.literal("General"));
        ConfigEntryBuilder entry = config.entryBuilder();

        category.addEntry(
                entry.startBooleanToggle(Component.literal("Override online-mode behavior"),
                                         Config.INSTANCE.overrideOnlineBehavior)
                        .setTooltip(Component.literal("Apply offline-mode fixes on online-mode servers as well."),
                                    Component.literal("Warning: May cause unexpected behavior.").withStyle(ChatFormatting.RED))
                        .setDefaultValue(Config.DEFAULT_OVERRIDE_ONLINE_BEHAVIOR)
                        .setSaveConsumer(v -> Config.INSTANCE.overrideOnlineBehavior = v)
                        .build()
        );

        category.addEntry(
                entry.startBooleanToggle(Component.literal("Disable chat checks"),
                                         Config.INSTANCE.disableChatChecks)
                        .setTooltip(Component.literal("Disable chat blocklist checks that may cause lag spikes."))
                        .setDefaultValue(Config.DEFAULT_DISABLE_CHAT_CHECKS)
                        .setSaveConsumer(v -> Config.INSTANCE.disableChatChecks = v)
                        .build()
        );

        category.addEntry(
                entry.startBooleanToggle(Component.literal("Skip multiplayer preparation"),
                                         Config.INSTANCE.skipMultiplayerPreparing)
                        .setDefaultValue(Config.DEFAULT_SKIP_MULTIPLAYER_PREPARING)
                        .setSaveConsumer(v -> Config.INSTANCE.skipMultiplayerPreparing = v)
                        .build()
        );

        category.addEntry(
                entry.startBooleanToggle(Component.literal("Remove the unverified message indicator"),
                                         Config.INSTANCE.removeUnsecureMessageTag)
                        .setDefaultValue(Config.DEFAULT_REMOVE_UNSECURE_MESSAGE_TAG)
                        .setSaveConsumer(v -> Config.INSTANCE.removeUnsecureMessageTag = v)
                        .build()
        );

        category.addEntry(
                entry.startBooleanToggle(Component.literal("Force player heads in the player list"),
                                         Config.INSTANCE.forceTabHeads)
                        .setDefaultValue(Config.DEFAULT_FORCE_TAB_HEADS)
                        .setSaveConsumer(v -> Config.INSTANCE.forceTabHeads = v)
                        .build()
        );

        category.addEntry(
                entry.startBooleanToggle(Component.literal("Hide unverified chat warnings"),
                                         Config.INSTANCE.hideUnverifiedToast)
                        .setDefaultValue(Config.DEFAULT_HIDE_UNVERIFIED_TOAST)
                        .setSaveConsumer(v -> Config.INSTANCE.hideUnverifiedToast = v)
                        .build()
        );

        return config.build();
    }
}
