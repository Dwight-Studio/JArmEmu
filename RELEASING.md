# Releasing JArmEmu

The version is defined **once**, in the root `pom.xml` (`<revision>`). Modules inherit it
(`${revision}`, resolved at build time by `flatten-maven-plugin`).

## Steps

1. Update `<revision>` in `pom.xml`.
2. Update `version` in `flake.nix`.
3. Add a `<release>` entry in `resources/metainfo/fr.dwightstudio.JArmEmu.metainfo.xml`.
4. Add a section to `CHANGELOG`.
5. Open a PR to `main`: CI refuses a version whose tag `v<version>` already exists, then builds and runs the tests.
6. After merging, publish a GitHub release tagged `v<version>`: `deploy.yml` builds the portable archive and
   updates the packages (AUR, Fedora/Debian repositories, Chocolatey...).

## Dependencies

Versions are centralised as properties in the root `pom.xml` (`javafx.version`, `junit.version`,
`commons-lang3.version`, `json.version`, `github-api.version`). Stay on Java 21 (JavaFX 21.x).
Check for known vulnerabilities in these dependencies before each release.
