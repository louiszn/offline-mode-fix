# Offline Mode Fix

> [!NOTE]
> Active development and source code for this project are hosted on [Codeberg](https://codeberg.org/louiszn/offline-mode-fix).
>
> If you are reading this on another platform, you are viewing a read-only mirror. Please open all issues, pull requests on the official Codeberg repository.

A lightweight client-side Fabric mod that fixes chat lag spikes and UI issues on offline-mode Minecraft servers.

## Features

The mod aims to fix some unnecessary or broken behaviors in offline-mode clients, as well as restore some features normally exclusive to online-mode:
- Reduced chat lag - skip player blocklist checks that cause lag spikes.
- Fewer "Unverified Server" warnings - hide first-join toast and message tag indicator.
- Restored player heads - Bring back player heads in the player list.

And most features can be configured in-game with Mod Menu easily!

## Compatibility

The mod is designed to preserve normal behavior on online-mode servers so you can leave the mod installed without any issues. If necessary, you can enable `overrideOnlineModeBehavior`, but be aware that it may cause unexpected behavior.

It also **DOES NOT**:
- Act like an authentication method.
- Reimplement all online-mode exclusive features.

It's a client-side mod! Please don't use it on servers.

## Building Requirements

- Git
- Java 25 or later
- IntelliJ IDEA (recommended)
- [Stonecutter Dev](https://plugins.jetbrains.com/plugin/25044-stonecutter-dev) and [Minecraft Development](https://plugins.jetbrains.com/plugin/8327-minecraft-development) for IntelliJ IDEA

## Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request on [Codeberg](https://codeberg.org/louiszn/offline-mode-fix).

This project supports multiple Minecraft versions using [Stonecutter](https://codeberg.org/stonecutter/stonecutter). Please make sure your changes work across the relevant versions before submitting a pull request.
