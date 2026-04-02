# Offline Mode Fix

A lightweight, plug-and-play Fabric mod that fixes the massive chat lag spikes and annoying UI warnings when playing on offline-mode Minecraft servers.

## Features
* **Zero Chat Lag:** Prevents the 30-second API timeout lag spikes that happen when sending or receiving messages on offline servers.
* **Clean UI:** Removes the ugly red/gray "Not Secure" sidebars from player messages, restoring the classic 1.18 chat look.
* **No Annoying Toasts:** Hides the "Unverified Server" popup that appears every time you join the world.
* **Smart Premium Compatibility:** Automatically detects if you are using a premium account and lets vanilla checks run normally. You can safely leave this mod installed when playing on premium servers like Hypixel.

## Installation
1. Install the [Fabric Loader](https://fabricmc.net/).
2. Download the correct `.jar` for your Minecraft version from the releases page.
3. Drop the `.jar` into your `.minecraft/mods` folder.

## Building from Source
This project uses **Stonecutter** to seamlessly support multiple Minecraft versions (1.21.1 through 1.21.11) from a single unified codebase. You will need Java 21 and Git installed on your system.
Here is the corrected version of your markdown. 

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
Because Stonecutter relies on dynamic comments (`//? if`) to switch code between mapping versions:
1. Install the **Stonecutter Dev** plugin by KikuGie from the IntelliJ Plugins Marketplace for proper syntax highlighting and error resolution.
2. Open the Gradle tab, navigate to `Tasks -> stonecutter`, and double-click **`Set active project to <version>`** to swap your active workspace to a specific Minecraft update (e.g., `1.21.11`).

## How it works (Technical)
In 1.19+, Minecraft introduced Chat Reporting. Offline clients fail to fetch cryptographic keys from Mojang's API, causing hanging web requests (lag) and triggering the game to flag all messages as "unsecure" (UI warnings).

This mod uses surgical Mixins to:
1. Detect offline (Version 3) UUIDs to safely bypass the hanging network calls.
2. Intercept the chat GUI renderer to hide the "Not Secure" tag.
3. Block the specific "Unverified Server" toast from being added to the screen via the `ToastManager` / `ToastComponent`.

## Contributing
Contributions are always welcome. If you have an idea for an improvement or find a bug, please open an issue first to discuss what you would like to change. Pull requests are greatly appreciated.
