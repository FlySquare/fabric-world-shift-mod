# World Shift

**Languages:** [English](README.md) · [Türkçe](README_TR.md)

A Fabric mod for Minecraft that periodically shifts every player on the server between different worlds on a shared countdown.

---

## Download

| | Link |
| --- | --- |
| **Latest release** | [GitHub Releases](https://github.com/FlySquare/fabric-world-shift-mod/releases/latest) |
| **v1.0.0 jar** | [change-world-every-second-1.0.0.jar](https://github.com/FlySquare/fabric-world-shift-mod/releases/download/v1.0.0/change-world-every-second-1.0.0.jar) |
| **Source code** | [FlySquare/fabric-world-shift-mod](https://github.com/FlySquare/fabric-world-shift-mod) |

1. Download the `.jar` from the latest release
2. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft **26.2**
3. Install [Fabric API](https://modrinth.com/mod/fabric-api)
4. Put the World Shift jar into your `mods` folder
5. Launch the game

For multiplayer, install the mod on the **server** (and preferably on clients too for the title-screen settings / HUD offset).

---

## Overview

World Shift turns a Minecraft session into a rotating multi-world challenge:

- A global timer counts down for all players
- When it hits zero, everyone is teleported to the next world together
- Inventories stay intact
- Per-player positions are remembered per world, so return trips restore where you left off

It works in singleplayer and multiplayer (LAN / dedicated), as long as the mod is installed on the server side.

---

## Features

- Shared countdown HUD above the hotbar
- Instant world transitions (no title card delay)
- Custom dimensions:
  - **Skyblock**
  - **Deep Dark** (auto-aggressive Wardens)
  - **Mushroom Island**
- Vanilla dimensions too: Overworld, Nether, End
- English / Turkish UI language (default: English)
- Title-screen settings button next to Singleplayer
- Configurable seconds between world shifts
- Commands for start / stop / force shift / timer / debug

---

## Requirements

| Dependency | Version |
| --- | --- |
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3+ |
| Fabric API | matching 26.2 |
| Java | 25+ |

---

## Quick start

1. Open Minecraft and go to the main menu
2. Click the square **WS** button next to **Singleplayer**
3. Choose language (**English** / **Turkish**)
4. Set **Seconds between worlds** (default: `60`)
5. Create or open a world
6. Run:

```text
/worldshift start
```

The countdown begins. When it reaches zero, everyone shifts to the next world.

---

## Commands

| Command | Description |
| --- | --- |
| `/worldshift start` | Start the World Shift session |
| `/worldshift stop` | Stop the session |
| `/worldshift next` | Force an immediate shift |
| `/worldshift timer <seconds>` | Set countdown length (1–3600) |
| `/worldshift world <name>` | Teleport everyone to a specific world |
| `/worldshift debug` | Print session debug info |

World name examples: `overworld`, `nether`, `end`, `skyblock`, `deep_dark`, `mushroom_island`

---

## Settings

### Title screen (WS button)

Before opening a world you can configure:

- **Language** — `English` (default) or `Turkish`
- **Seconds between worlds** — how long each world lasts

Settings are saved to:

```text
config/change-world-every-second.json
```

### Config fields

| Field | Default | Meaning |
| --- | --- | --- |
| `language` | `en` | UI / command language (`en` or `tr`) |
| `countdownSeconds` | `60` | Seconds between world shifts |
| `transitionDelaySeconds` | `0` | Kept for compatibility (transitions are instant) |
| `startingWorld` | `OVERWORLD` | First world when a session starts |
| `enabledWorlds` | all | Worlds included in the rotation |
| `debug` | `false` | Extra debug logging flag |

---

## How shifting works

1. Session starts on the configured starting world
2. Countdown ticks for every online player
3. At zero, the mod picks the next enabled world
4. Player locations in the current world are saved (if safe)
5. Everyone is teleported to the next world
6. If a stored location exists and is safe, it is restored; otherwise a spawn point is used
7. Countdown restarts

---

## Building from source

```bash
./gradlew build
```

The output jar is written to `build/libs/`.

Published builds are attached to [GitHub Releases](https://github.com/FlySquare/fabric-world-shift-mod/releases).

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Credits

- Author: **Flyzen / FlySquare**
- Repository: [github.com/FlySquare/fabric-world-shift-mod](https://github.com/FlySquare/fabric-world-shift-mod)
- Built with Fabric for Minecraft 26.2
