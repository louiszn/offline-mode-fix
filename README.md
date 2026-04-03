# Offline Mode Fix

A lightweight, plug-and-play Fabric mod that fixes the random chat lag spikes, annoying UI warnings, and texture loading crashes when playing on offline-mode Minecraft servers.

## Features
* **Zero Chat Lag:** Prevents the random network timeout lag spikes that trigger after periods of inactive chat on offline servers.
* **Clean UI:** Removes the ugly red/gray "Not Secure" sidebars from player messages, restoring the classic chat look.
* **No Annoying Toasts:** Hides the "Unverified Server" popup that appears every time you join the world.
* **No Texture Crashes:** Stops the log spam and client crashes caused by strict signature validation on custom offline skins.
* **Smart Premium Compatibility:** Automatically detects if you are using a premium account and lets vanilla checks run normally. You can safely leave this mod installed when playing on premium servers like Hypixel.

## Installation
1. Install the Fabric Loader: https://fabricmc.net/
2. Download the correct `.jar` for your Minecraft version from the releases page.
3. Drop the `.jar` into your `.minecraft/mods` folder.

## Building from Source
This project uses **Stonecutter** to seamlessly support multiple Minecraft versions from a single unified codebase. You will need Java 21+ and Git installed on your system.

1. Clone the repository:

   ```bash
   git clone https://github.com/louiszn/offline-mode-fix
   cd offline-mode-fix
   ```

2. Build the project using the Gradle wrapper. To compile all supported Minecraft versions at once, run:

   * **Windows:**

     ```cmd
     gradlew buildAndCollect
     ```

   * **Linux / macOS:**

     ```bash
     ./gradlew buildAndCollect
     ```

3. Once the build process finishes, you will find the compiled `.jar` files for every version cleanly organized in the `/versions/xxx/build/libs/` directory.

### IDE Setup for Development (IntelliJ IDEA)
Because Stonecutter relies on dynamic comments to switch code between mapping versions:
1. Install the **Stonecutter Dev** plugin by KikuGie from the IntelliJ Plugins Marketplace for proper syntax highlighting and error resolution.
2. Open the Gradle tab, navigate to `Tasks -> stonecutter`, and double-click **`Set active project to xxx`** to swap your active workspace to a specific Minecraft update (e.g., `1.21.11`).

## How it works (Technical)
Offline-mode servers suffer from several distinct client-side issues introduced in different Minecraft updates:

1. **1.16.4 (The Social Manager):** Minecraft checks every incoming message against the `PlayerSocialManager` blocklist. In offline mode, looking up unauthenticated Version 3 UUIDs causes unnecessary synchronous network requests and processing delays.
2. **1.19+ (Chat Signing):** Minecraft attempts to fetch and refresh cryptographic public keys from Mojang's API to verify chat signatures. On offline servers, these background refreshes fail, causing random lag spikes (often triggering after ~30 seconds of inactive chat) and forcing the game to flag all messages as "unsecure" (UI warnings).
3. **1.20.2 (Authlib Strict Validation):** The game enforces strict Base64 signature validation for player textures (skins/capes). Dummy signatures provided by offline skin plugins (like `BUILDER_APPALLED_PUMPKIN`) instantly trigger `IllegalArgumentException` log spam and texture loading failures.

This mod uses surgical Mixins to:
1. Detect offline (Version 3) UUIDs to instantly bypass the `isBlocked` and `isTextFilteringEnabled` network checks.
2. Intercept the chat GUI renderer to hide the "Not Secure" tag.
3. Block the specific "Unverified Server" toast from being added to the screen via the `ToastManager` / `ToastComponent`.
4. Bypass `YggdrasilServicesKeyInfo` strict Base64 property validation if a dummy signature is detected.

## Contributing
Contributions are always welcome. If you have an idea for an improvement or find a bug, please open an issue first to discuss what you would like to change. Pull requests are greatly appreciated.
