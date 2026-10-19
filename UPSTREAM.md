# Upstream source and synchronization

Brix Launcher is a customized Android Minecraft: Java Edition launcher developed from [MojoLauncher](https://github.com/MojoLauncher/MojoLauncher), which in turn builds on [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher).

## Important distinction

This repository was initialized by uploading a customized source snapshot. **It is not presently a GitHub-native fork** of MojoLauncher, so GitHub may not display the "forked from" badge or offer an automatic upstream comparison. That badge cannot be added by editing a README.

## Connect the actual upstream Git remote

In a local clone, from PowerShell:

```powershell
git remote -v
git remote add upstream https://github.com/MojoLauncher/MojoLauncher.git
git fetch upstream
git branch -r
```

If `upstream` already exists, replace the `git remote add` command with `git remote set-url upstream https://github.com/MojoLauncher/MojoLauncher.git`.

**Do not blindly merge or reset** `upstream` into `main`: the customized source snapshot may have unrelated Git history and extensive local modifications. Review diffs, licenses, dependencies, and build/test changes before applying upstream commits.

## Legal attribution

Keep the upstream project's applicable LGPL-3.0 license, copyright notices, and third-party licenses. The Brix brand does not imply affiliation with Mojang, Microsoft, or the upstream maintainers.
