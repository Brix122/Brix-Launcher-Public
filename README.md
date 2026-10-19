# Brix Launcher

**Independent, unofficial Android launcher for Minecraft: Java Edition.**

> **NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**

Brix Launcher is a customised launcher based on [MojoLauncher](https://github.com/MojoLauncher/MojoLauncher), itself based on [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher). The underlying launcher engine and upstream software were **not** created from scratch by BrixTools.

## Overview
- Minecraft Java Edition version management and launching on supported Android devices
- Touchscreen controls and control-layout customisation
- Available runtime, rendering and memory settings
- Independent Brix Launcher red/white user interface

Features and compatibility depend on the device, installed runtime, game version and configuration. Minecraft game ownership or a valid account may be required where applicable. Brix Launcher does not include a Minecraft licence.

## Build from source
The Android project includes Gradle build variants. Example development build:

```powershell
.\gradlew.bat :app_mojo:assembleFullDebug
```

Release builds require a separate private signing key; **never upload keystores, passwords or access tokens**. The actual Play Store application ID may differ from the version checked into this branch until the developer's local changes have been committed. Always inspect the built AAB and merged manifest before publishing.

## Downloads and release status
Public release downloads: https://github.com/Brix122/Brix-Launcher-Public/releases

See [release checklist](RELEASE_SAFETY_CHECKLIST.md) before publishing and [security guidance](SECURITY.md) for reporting issues.

## Credits and licence
- [MojoLauncher](https://github.com/MojoLauncher/MojoLauncher) — upstream project, LGPL-3.0.
- [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher) — original launcher foundation and contributors.
- Additional libraries and dependencies have their own licences. See the preserved [upstream README](README_UPSTREAM.md), [legal notice](BRIX_LEGAL_NOTICE.md), [upstream notes](UPSTREAM.md) and [LICENSE](LICENSE).
- Brix branding and UI customisations do not override upstream copyright or third-party licence obligations.

Minecraft and related trademarks belong to their respective owners. This project is not affiliated with, sponsored by or endorsed by Mojang Studios or Microsoft.

## Privacy
See [Privacy and data-handling guidance](PRIVACY.md). A publicly hosted, verified privacy-policy URL must be provided in the app and Play Console as applicable. Do not treat a generic template as a substitute for auditing the actual release bundle.

## Contributing
Issues and proposed improvements are welcome once repository access is available. Avoid submitting proprietary game files, credentials or third-party assets without permission.
