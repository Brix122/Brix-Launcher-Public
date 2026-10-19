# Security policy

Brix Launcher is a community-customised Android launcher under development. No supported security-release cadence is established yet.

## Reporting a vulnerability
Do not publish passwords, session tokens, private keys, user data, or exploits in a public issue. Contact the maintainer privately through the repository owner's GitHub profile. Once a verified private security-reporting method is configured, it will be added here.

For reports, provide the affected revision, Android version, steps to reproduce and an impact summary. Remove personal information and secrets from logs.

## Signing and release
Never commit a JKS/keystore or its credentials. Store signing material off-repository, keep secure backups, rotate compromised credentials, verify released bundle certificate fingerprints, and test release artifacts on actual devices.
