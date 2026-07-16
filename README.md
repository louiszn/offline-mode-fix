# Offline Mode Fix

> [!NOTE]
> Active development and source code for this project are hosted on [Codeberg](https://codeberg.org/louiszn/offline-mode-fix).
>
> If you are reading this on another platform, you are viewing a read-only mirror. Please open all issues, pull requests on the official Codeberg repository.

## About

A lightweight, plug-and-play Fabric mod that fixes several client-side issues commonly encountered when playing on offline-mode Minecraft servers.

It reduces chat-related network delays, cleans up insecure chat indicators, restores player heads in the tab list, suppresses the unverified-server warning, and improves compatibility with malformed texture signatures used by some offline skin systems.

## Features

* **Reduced Chat Lag:** Bypasses unnecessary player blocklist checks for offline-mode UUIDs and disables client-side text filtering checks that may depend on account services.
* **Offline Login Fixes:** Skips multiplayer account-service preparation when the local player is using a Version 3 offline-mode UUID, preventing unnecessary authentication requests and related errors.
* **Clean Chat UI:** Removes chat message sidebar tags, including the red and gray "Not Secure" indicators shown on unsigned messages.
* **Player Heads:** Restores player head rendering in the multiplayer player list.
* **No Annoying Toasts:** Hides the "Unverified Server" warning shown when joining insecure or offline-mode servers.
* **Better Offline Skin Compatibility:** Bypasses strict texture signature validation for known malformed dummy signature formats used by some offline skin systems.
* **Premium-Friendly:** The offline-specific multiplayer preparation bypass is only enabled when the local account uses an offline-mode UUID, so the mod can remain installed while joining online-mode servers.

> [!NOTE]
> Some client-side tweaks, such as hiding chat tags, disabling text filtering, restoring tab-list heads, and suppressing the unverified-server warning, remain active regardless of account type. Installing the mod therefore does not preserve completely vanilla client behavior on online-mode servers.

## Installation

1. Install Fabric Loader for your Minecraft version.
2. Download the correct `.jar` for your Minecraft version from the official [Modrinth](https://modrinth.com/mod/offline-mode-fix) page.
3. Place the `.jar` in your `.minecraft/mods` folder.
4. Launch the game using your Fabric profile.

No server-side installation is required for the fixes themselves.

## Building from Source

This project uses [Stonecutter](https://stonecutter.kikugie.dev/) to support multiple Minecraft versions from a single codebase.

You will need:

* Java 21 or newer
* Git

### 1. Clone the repository

```bash
git clone https://github.com/louiszn/offline-mode-fix
cd offline-mode-fix
```

### 2. Build all supported versions

On Linux or macOS:

```bash
./gradlew buildAndCollect
```

On Windows:

```powershell
gradlew buildAndCollect
```

Individual version builds can also be run through their corresponding Gradle projects.

Compiled `.jar` files can be found under:

```text
versions/<minecraft-version>/build/libs/
```

## IDE Setup

### IntelliJ IDEA

Because Stonecutter uses preprocessing directives to maintain multiple Minecraft versions in one codebase, installing the **Stonecutter Dev** plugin by KikuGie is recommended for proper syntax highlighting and code analysis.

To switch the active development version:

1. Open the Gradle tool window.
2. Navigate to `Tasks -> stonecutter`.
3. Run the corresponding `Set active project to ...` task.

For example, selecting the `1.21.11` project makes that version active in the IDE.

## How It Works

Offline-mode servers can expose several client behaviors that normally assume players and servers are backed by Mojang account services.

Offline Mode Fix applies a small set of Mixins to change those behaviors.

### Player blocklist checks

Minecraft normally calls `isBlocked(UUID)` when processing players and chat messages.

For:

* `null` UUIDs,
* Version 3 UUIDs used by offline-mode players, and
* the NIL UUID used by system messages,

the mod immediately returns `false` instead of performing the normal blocklist check.

Premium UUIDs continue through the vanilla `isBlocked` implementation.

### Text filtering

The mod overrides `isTextFilteringEnabled()` and returns `false`.

This disables the client-side text filtering path and avoids account-service-dependent filtering checks.

This behavior is currently applied regardless of whether the local account is offline or authenticated.

### Multiplayer preparation

When joining multiplayer, Minecraft normally runs `prepareForMultiplayer()`, which initializes account-dependent multiplayer services.

The mod checks the local player's profile UUID before changing this behavior.

If the local profile uses a Version 3 offline-mode UUID, the preparation step is cancelled.

Authenticated accounts continue through the normal vanilla multiplayer preparation path.

### Chat message tags

The mod intercepts messages added to the chat GUI and removes their `GuiMessageTag`.

This removes the sidebar indicators used for unsigned or otherwise tagged messages, including the familiar red and gray "Not Secure" indicators.

Because the tag itself is removed, this currently affects all chat message tags rather than only insecure-message tags.

### Player heads in the tab list

The mod forces player head rendering in the multiplayer player list.

This restores heads that Minecraft may otherwise hide when the connection is considered insecure.

### Unverified server warning

The mod intercepts the client toast manager and prevents the insecure-server warning toast from being displayed.

Other toast notifications are left untouched.

### Texture signature validation

Modern Authlib versions strictly validate signed texture properties.

Some offline skin systems provide placeholder signatures that are not valid Base64 strings, which can cause validation errors and prevent textures from loading correctly.

The mod bypasses `YggdrasilServicesKeyInfo.validateProperty()` when the provided signature contains an underscore (`_`) or a space, allowing those known malformed placeholder formats through validation.

Other signatures continue through the normal Authlib validation path.

## Scope

Offline Mode Fix is intentionally focused on client-side compatibility issues related to offline-mode servers.

It does **not**:

* add authentication to offline-mode servers,
* protect offline-mode servers from username impersonation,
* replace authentication plugins,
* modify server-side account handling, or
* make offline-mode servers equivalent to online-mode servers.

Server owners should still use an appropriate authentication solution when running a publicly accessible offline-mode server.

## Contributing

Contributions are welcome.

If you find a bug or have an idea for an improvement, feel free to open an issue to discuss it. Pull requests are also greatly appreciated.

When contributing changes that affect Minecraft internals, please keep multi-version compatibility in mind and test the relevant Stonecutter versions where possible.

## License

Offline Mode Fix is licensed under the MIT License. See [`LICENSE`](LICENSE) for details.
