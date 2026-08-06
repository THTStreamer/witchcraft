# Witchcraft

An immersive magical progression system for Minecraft Paper servers.

## Overview

Witchcraft adds a deep, ritualistic magic system to Minecraft. Unlike flashy RPG spell plugins, Witchcraft emphasizes preparation, knowledge, and the slow unraveling of ancient mysteries. Every ritual carries weight, every incantation feels valuable.

## Features

### Ritual Cauldron Magic
- Perform rituals using cauldrons and ingredients
- Different spells require different ingredient combinations
- Moon phase and weather can affect rituals
- Experience cost for powerful magic

### Spoken Incantations
- Cast spells by typing magical phrases in chat
- Learn incantations from magical books
- Cooldowns and exhaustion systems
- Configurable aliases and matching

### Curse System
- Curse other players with various afflictions
- Mining fatigue, bad luck, crop failure, and more
- Curses can be removed through protective rituals

### Protection Wards
- Create protective wards against curses
- Anti-scrying protection
- Reflective backlash for attackers

### Scrying
- Spy on other players from afar
- Learn their location, health, and held items
- Blocked by protective wards

### Arcane Exhaustion
- Attackers who target protected players receive exhaustion
- Cannot cast magic during exhaustion period
- Persists across server restarts

## Installation

1. Download the latest Witchcraft JAR
2. Place it in your server's `plugins/` folder
3. Restart the server
4. Configure `plugins/Witchcraft/config.yml`

## Configuration

### config.yml
- General settings (ritual duration, moon phase requirements)
- Spell settings (cooldowns, success rates)
- Curse settings (duration, removal methods)
- Protection settings (duration, reflection)
- Visual effects (particles, sounds)

### messages.yml
- All user-facing messages
- Customizable prefixes and colors
- Placeholder support

### spells/
- Individual spell configuration files
- Create custom spells without recompiling

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/witchcraft help` | Show help | None |
| `/witchcraft reload` | Reload configuration | `witchcraft.reload` |
| `/witchcraft givebook [player]` | Give guide book | `witchcraft.admin` |
| `/witchcraft learn <player> <id>` | Teach incantation | `witchcraft.admin` |
| `/witchcraft unlearn <player> <id>` | Remove incantation | `witchcraft.admin` |
| `/witchcraft purge <player>` | Clear all magic effects | `witchcraft.admin` |
| `/witchcraft list` | List all spells | `witchcraft.admin` |
| `/witchcraft exhaust <player>` | Apply exhaustion | `witchcraft.admin` |
| `/witchcraft debug` | Show debug info | `witchcraft.debug` |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `witchcraft.admin` | Access to all admin commands | op |
| `witchcraft.reload` | Reload configuration | op |
| `witchcraft.cast` | Cast rituals and incantations | true |
| `witchcraft.learn` | Learn new incantations | true |
| `witchcraft.curse` | Cast curse spells | true |
| `witchcraft.protection` | Cast protection spells | true |
| `witchcraft.scrying` | Cast scrying spells | true |
| `witchcraft.debug` | Debug commands | op |

## API

Witchcraft exposes an API for other plugins:

```java
// Get the spell registry
SpellRegistry registry = WitchcraftAPI.getSpellRegistry();

// Check if a player knows an incantation
boolean knows = WitchcraftAPI.hasLearnedIncantation(playerId, "incantation_id");

// Check if a player is exhausted
boolean exhausted = WitchcraftAPI.isExhausted(playerId);
```

### Events

- `WitchSpellCastEvent` - Fired when a spell is cast
- `WitchCurseApplyEvent` - Fired when a curse is applied
- `WitchCurseRemoveEvent` - Fired when a curse is removed
- `WitchProtectionApplyEvent` - Fired when protection is applied
- `WitchScryEvent` - Fired when scrying is attempted
- `WitchBackfireEvent` - Fired when a spell backfires

## Building

```bash
./gradlew build
```

The JAR will be in `build/libs/`.

## Development

### Requirements
- Java 21+
- Gradle 8+
- Paper API 1.21.4+

### Project Structure
```
src/main/java/com/witchcraft/
├── Witchcraft.java          # Main plugin class
├── api/                     # Public API and events
├── core/                    # Core systems (Spell, Registry, etc.)
├── ritual/                  # Ritual cauldron system
├── incantation/             # Spoken incantation system
├── spells/                  # Spell implementations
│   ├── curse/               # Curse spells
│   ├── ritual/              # Ritual spells
│   ├── protection/          # Protection spells
│   └── divination/          # Scrying spells
├── data/                    # Data persistence
├── commands/                # Command handlers
├── effects/                 # Visual effects
├── book/                    # Guide book system
└── util/                    # Utility classes
```

## License

MIT License

## Credits

Created by the Witchcraft Team.
