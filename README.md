<img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="140"/>

# JArmEmu

### Simple ARMv7 simulator written in Java, intended for educational purpose.

- [Features](#features)
- [Installation](#install)
  - [From Git](#from-git)
  - [Portable archive](#portable)
  - [Windows](#windows)
  - [FlatPaks (FlatHub)](#flatpak)
  - [Fedora/Nobara](#fedora)
  - [Arch Linux/Manjaro](#arch-linux)
  - [Debian/Ubuntu/Pop! OS/Linux Mint/Kali Linux](#debian)
  - [Nix](#nix)
- [Licence](#licence)

# Features

![JArmEmu Editor](https://static.dwightstudio.fr/v1/jarmemu/showcase/cupertino_light.png)

<img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu is a simple, user-friendly simulator that provides basic control and information about a simulated ARMv7
architecture.

<img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu is powered by an ARMv7 interpreter built *Ex Nihilo* for this project, which provides real-time syntax
highlighting, intelligent auto-completion, memory, stack and register monitoring...

You can write your program using ARMv7 instructions and commonly used GNU directives, and watch the simulator execute
it with details on any step.

> [!NOTE]
> The implemented instruction set is available directly in the simulator (Help > See instructions...).

# Install

## From Git

You can compile and run <img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu directly from source by cloning this repository. The project requires the `JDK 21` and
is built using `Maven 3`.
```bash
# Clone the repository
git clone https://github.com/Dwight-Studio/JArmEmu
cd JArmEmu
```

To launch <img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu, you juste need to `compile` and run the maven goal `exec:java` :
```bash
mvn compile exec:java
```
## Portable

<img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu is distributed in a portable archive available from
the [release page](https://github.com/Dwight-Studio/JArmEmu/releases/latest). It requires Java 21 (or newer), which is **not included in the archive**.

## Windows

[![Windows](https://img.shields.io/badge/Windows-0079D6?style=for-the-badge&logo=windows&logoColor=white)](#Windows)
[![Chocolatey](https://img.shields.io/badge/Chocolatey-000000?style=for-the-badge&logo=chocolatey&logoColor=white)](#Windows)

[![Chocolatey downloads](https://img.shields.io/chocolatey/dt/fr.dwightstudio.JArmEmu)](https://community.chocolatey.org/packages/fr.dwightstudio.JArmEmu/)

You can download an installer for <img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu (or the portable version) from
the [release page](https://github.com/Dwight-Studio/JArmEmu/releases/latest).

> [!IMPORTANT]
> The executables aren't signed, and can trigger a warning screen from Windows UAC (which you can simply ignore).

<img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu is also available on Chocolatey:

```bash
choco install jarmemu
```

## FlatPak

[![FlatPak](https://img.shields.io/badge/FlatPak-4A90D9?style=for-the-badge&logo=flatpak&logoColor=white)](#flatpak)
[![FlatHub](https://img.shields.io/badge/FlatHub-000000?style=for-the-badge&logo=flathub&logoColor=white)](#flatpak)

[![FlatHub downloads](https://img.shields.io/flathub/downloads/fr.dwightstudio.JArmEmu)](https://flathub.org/fr/apps/fr.dwightstudio.JArmEmu)

<img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu is available on [FlatHub](https://flathub.org/fr/apps/fr.dwightstudio.JArmEmu).

> [!TIP]
> You can install it using your Software Manager (Gnome Software, KDE Discover...).

You can install it using FlatPak CLI:

```bash
flatpak install flathub fr.dwightstudio.JArmEmu
```

## Fedora

[![Fedora](https://img.shields.io/badge/Fedora-294172?style=for-the-badge&logo=fedora&logoColor=white)](#fedora)
[![Nobara](https://img.shields.io/badge/Nobara-black?style=for-the-badge)](#fedora)

[![Copr build status](https://copr.fedorainfracloud.org/coprs/dwight-studio/JArmEmu/package/jarmemu/status_image/last_build.png)](https://copr.fedorainfracloud.org/coprs/dwight-studio/JArmEmu/package/jarmemu/)

<img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu is available on [Fedora Copr](https://copr.fedorainfracloud.org/coprs/dwight-studio/JArmEmu/package/jarmemu/):

```bash
sudo dnf copr enable dwight-studio/JArmEmu
sudo dnf install jarmemu
```

## Arch Linux

[![Arch Linux](https://img.shields.io/badge/Arch_Linux-1793D1?style=for-the-badge&logo=arch-linux&logoColor=white)](#ArchLinux)
[![Manjaro](https://img.shields.io/badge/manjaro-35BF5C?style=for-the-badge&logo=manjaro&logoColor=white)](#ArchLinux)

[![AUR](https://img.shields.io/aur/votes/jarmemu.svg)](https://aur.archlinux.org/packages/jarmemu)

<img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu is available on [AUR](https://aur.archlinux.org/packages/jarmemu). Use your favorite AUR Helper (`yay` for
instance):

```bash
yay -S jarmemu
```

## Debian

[![Debian](https://img.shields.io/badge/Debian-A81D33?style=for-the-badge&logo=debian&logoColor=white)](#debian)
[![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white)](#debian)
[![Pop! OS](https://img.shields.io/badge/Pop!_OS-48B9C7?style=for-the-badge&logo=Pop!_OS&logoColor=white)](#debian)
[![Linux Mint](https://img.shields.io/badge/Linux_Mint-87CF3E?style=for-the-badge&logo=linux-mint&logoColor=white)](#debian)
[![Kali Linux](https://img.shields.io/badge/Kali_Linux-557C94?style=for-the-badge&logo=kali-linux&logoColor=white)](#debian)

<img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu is available on the Dwight Studio repository.

> [!NOTE]
> The following command will install the Dwight Studio's repository which hosts Debian binaries for all available
> projects of the collective.


You can install it by running:

```bash
sudo sh -c "curl -sS https://deb.dwightstudio.fr/repo/gpgkeys/deb.dwightstudio.fr.pub | gpg --dearmor > /etc/apt/trusted.gpg.d/deb.dwightstudio.fr.gpg"
sudo sh -c "echo 'deb https://deb.dwightstudio.fr/repo/deb/dwightstudio-stable/any/main/prod any main' > /etc/apt/sources.list.d/repomanager-dwightstudio-stable-any-main.list"
sudo apt update && sudo apt install jarmemu
```

## Nix

[![NixOS](https://img.shields.io/badge/Nix-5277C3?style=for-the-badge&logo=nixos&logoColor=white)](#nix)
[![NixOS](https://img.shields.io/badge/NixOS-FFFFF3?style=for-the-badge&logo=nixos&logoColor=5277C3)](#nix)

<img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu is available with the [Nix](https://nixos.org/) package manager through this repository flake.
You can build/run it imperatively by running (with flake enabled):

```bash
nix build github:Dwight-Studio/jArmEmu
nix run github:Dwight-Studio/jArmEmu
```

Otherwise, use your preferred way to declaratively install the package `jarmemu` from the `github:Dwight-Studio/jArmEmu` flake as input.

## Licence

This project is managed by the <img src="https://static.dwightstudio.fr/v1/dwightstudio/base/svg/logo_red.svg" alt="drawing" width="15"/> Dwight Studio collective, which is not affiliated not endorsed by Arm Limited in any sort.

This project was created by Kévin "FoxYinx" TOLLEMER and Alexandre "Deleranax" LECONTE, students at INSA Rennes
(not affiliated). <img src="https://static.dwightstudio.fr/v1/jarmemu/base/svg/logo.svg" alt="drawing" width="15"/> JArmEmu is distributed in open source under GPL3 (refer to the LICENCE file).
