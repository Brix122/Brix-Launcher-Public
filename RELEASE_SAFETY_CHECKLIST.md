# Brix Launcher — before publishing an APK

**Release status:** NOT CLEARED by this checklist. A successful GitHub Actions job or local APK install does not establish legal compliance, security or device compatibility.

- [ ] Review the actual built APK (not just repository files), full dependency tree and all embedded native libraries.
- [ ] Confirm all MojoLauncher/PojavLauncher license and copyright notices are retained and give users access to required corresponding source.
- [ ] Inventory third-party licenses, including GPL/LGPL/AGPL conditions when applicable.
- [ ] Verify original or appropriately licensed rights to the BL logo, background banner, icons, screenshots, fonts and any other bundled art.
- [ ] Replace the official-style Minecraft version icon unless its use is specifically permitted by the current Minecraft guidelines.
- [ ] Show clearly in README, website, launcher About/Credits, video description and release description: **NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**
- [ ] Do not ship game JARs, paid Minecraft assets, copied commercial mods, keystores, tokens, credentials or private user data.
- [ ] Verify official/account-authorized download paths, security, privacy and telemetry disclosure.
- [ ] Build a signed release from a tagged, reproducible source commit; confirm its actual Android application ID, signing fingerprint, version and device tests.
- [ ] Test install/update without wiping existing worlds; test Minecraft launch, skin preview, mod loaders, crash recovery and downloads.
- [ ] Attach checksum (SHA-256), release notes, known issues, and verified source link to the release.

**Do not switch the repository to public or publish an APK before these checks are addressed.**
