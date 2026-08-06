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
- Fire and projectile shields
- Reflective backlash for attackers

### Scrying
- Spy on other players from afar
- Learn their location, health, and held items
- Treasure dowsing and aura sight
- Blocked by protective wards

### Arcane Exhaustion
- Attackers who target protected players receive exhaustion
- Cannot cast magic during exhaustion period
- Persists across server restarts

---

## Ritual Guide

### How to Perform a Ritual

1. **Fill a cauldron** with water (use a water bucket on a cauldron)
2. **Add ingredients** by right-clicking the cauldron with each ingredient in hand
3. **Optionally add a target paper** (right-click with paper) for spells that target another player
4. **Wait** — the ritual will automatically start once all ingredients are added
5. **Stay near the cauldron** — move away and the ritual fails
6. The ritual charges over time with increasing particle effects, then completes

**Requirements:**
- Permission: `witchcraft.cast`
- Enough XP levels (shown per spell)
- Not under Arcane Exhaustion
- Some rituals require specific moon phase or weather

**Target Paper:** For curses and player-targeting spells, create a target paper with `/witchcraft targetpaper <player>` and add it to the cauldron after adding ingredients. The paper is consumed on use.

---

### Curse Rituals

#### Curse of the Deep Mine
| | |
|---|---|
| **Effect** | Afflicts target with Mining Fatigue |
| **Ingredients** | Nether Wart + Redstone + Coal |
| **Incantation** | `tenebris ferrum obstaculum` |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of Misfortune
| | |
|---|---|
| **Effect** | Brings bad luck to the target |
| **Ingredients** | Spider Eye + Fermented Spider Eye + Coal |
| **Incantation** | `fortuna inversa cadat` |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Withering
| | |
|---|---|
| **Effect** | Slows healing on the target |
| **Ingredients** | Bone Meal + Ghast Tear + Coal |
| **Incantation** | `vita sanatio lento` |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of Feebleness
| | |
|---|---|
| **Effect** | Weakens the target |
| **Ingredients** | Bone Meal + Sugar + Coal |
| **Incantation** | `vis deficiat invalidus` |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Night Watch
| | |
|---|---|
| **Effect** | Increases phantom activity around the target |
| **Ingredients** | Phantom Membrane + Echo Shard + Coal |
| **Incantation** | `umbra vigilare noctis` |
| **XP Cost** | 3 |
| **Requires Target** | Yes |
| **Special** | Requires Full Moon (moon phase 4) |

#### Curse of Barren Fields
| | |
|---|---|
| **Effect** | Causes crop failure around the target |
| **Ingredients** | Bone Meal + Sugar + Nether Wart |
| **Incantation** | `agricultura maledictio sterile` |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of Sterility
| | |
|---|---|
| **Effect** | Prevents animals from breeding near the target |
| **Ingredients** | Bone Meal + Spider Eye + Nether Wart |
| **Incantation** | `procreatio negare infructuosa` |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Empty Net
| | |
|---|---|
| **Effect** | Reduces fishing luck for the target |
| **Ingredients** | Nautilus Shell + Prismarine Shard + Coal |
| **Incantation** | `piscis fortuna careat` |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Muted Tongue
| | |
|---|---|
| **Effect** | Prevents a target from casting spells |
| **Ingredients** | Gunpowder + Fermented Spider Eye + Coal |
| **Incantation** | `mutus lingua sileat` |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Shrouded Eye
| | |
|---|---|
| **Effect** | Blinds the target |
| **Ingredients** | Fermented Spider Eye + Coal + Nether Quartz |
| **Incantation** | `oculus caligo tenebris` |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Iron Boots
| | |
|---|---|
| **Effect** | Slows the target |
| **Ingredients** | Iron Ingot + Sugar + Coal |
| **Incantation** | `gradus lentus ferrum` |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Ravenous Maw
| | |
|---|---|
| **Effect** | Makes the target endlessly hungry |
| **Ingredients** | Magma Cream + Sugar + Coal |
| **Incantation** | `esuries fames devorat` |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

---

### Fertility Rituals

#### Blessing of Abundance
| | |
|---|---|
| **Effect** | Blesses the land with fertility, accelerates crop growth |
| **Ingredients** | Bone Meal + Glowstone Dust + Sugar |
| **Incantation** | `terra foecunditas abundet` |
| **XP Cost** | 5 |
| **Special** | Requires Clear Weather |

#### Bloom of the Green Hand
| | |
|---|---|
| **Effect** | Accelerates crop growth in the area |
| **Ingredients** | Bone Meal + Glowstone Dust + Nether Quartz |
| **Incantation** | `germen floreant viridis` |
| **XP Cost** | 5 |

---

### Warding Rituals

#### Guardian's Ward
| | |
|---|---|
| **Effect** | Creates a ward against hostile mobs |
| **Ingredients** | Obsidian + Echo Shard + Bone Meal |
| **Incantation** | `custos praesidio locus` |
| **XP Cost** | 5 |

---

### Cleansing Rituals

#### Rite of Purification
| | |
|---|---|
| **Effect** | Removes curses from a player |
| **Ingredients** | Ghast Tear + Sugar + Bone Meal |
| **Incantation** | `purgatio maledictio liberare` |
| **XP Cost** | 4 |

#### Purification of the Clear Spring
| | |
|---|---|
| **Effect** | Cleanses nearby water sources |
| **Ingredients** | Ghast Tear + Nether Quartz + Bone Meal |
| **Incantation** | `aqua purificatio fons` |
| **XP Cost** | 4 |

---

### Protection Rituals

#### Ward of Protection
| | |
|---|---|
| **Effect** | Creates a protective ward against curses |
| **Ingredients** | Obsidian + Amethyst Shard + Glowstone Dust |
| **Incantation** | `custodio sanctum aegis` |
| **XP Cost** | 5 |

#### Veil of Obscurity
| | |
|---|---|
| **Effect** | Blocks scrying attempts on you |
| **Ingredients** | Echo Shard + Amethyst Shard + Crying Obsidian |
| **Incantation** | `obscurus ne videar` |
| **XP Cost** | 5 |

#### Ward of the Immolator
| | |
|---|---|
| **Effect** | Grants fire resistance through ritual |
| **Ingredients** | Blaze Powder + Obsidian + Glowstone Dust |
| **Incantation** | `ignis scutum immolare` |
| **XP Cost** | 5 |

#### Aegis of Deflection
| | |
|---|---|
| **Effect** | Protects against projectile attacks |
| **Ingredients** | Iron Ingot + Obsidian + Flint |
| **Incantation** | `aegis deflexio projicere` |
| **XP Cost** | 5 |

---

### Divination Rituals

#### Mirror of the Soul
| | |
|---|---|
| **Effect** | Allows scrying on another player (location, health, items) |
| **Ingredients** | Echo Shard + Amethyst Shard + Ender Pearl |
| **Incantation** | `speculum anima videre` |
| **XP Cost** | 7 |
| **Requires Target** | Yes |

#### Dowsing of the Hidden Way
| | |
|---|---|
| **Effect** | Reveals nearby hidden structures |
| **Ingredients** | Echo Shard + Gold Ingot + Amethyst Shard |
| **Incantation** | `thesaurus videre via abscondita` |
| **XP Cost** | 7 |

#### The Witch's Third Eye
| | |
|---|---|
| **Effect** | Reveals a target's inner aura and status |
| **Ingredients** | Amethyst Shard + Echo Shard + Glowstone Dust |
| **Incantation** | `oculus tertius aura videre` |
| **XP Cost** | 7 |
| **Requires Target** | Yes |

---

### Ritual-Based Spells

#### Gathering of Lost Souls
| | |
|---|---|
| **Effect** | Collects ambient soul energy |
| **Ingredients** | Echo Shard + Nether Quartz + Coal |
| **Incantation** | `anima colligere umbrarum` |
| **XP Cost** | 5 |

#### Chains of the Bound Soul
| | |
|---|---|
| **Effect** | Binds a target's spirit to a location |
| **Ingredients** | Iron Ingot + Echo Shard + Coal |
| **Incantation** | `catena anima vincire` |
| **XP Cost** | 4 |
| **Requires Target** | Yes |

#### Rite of Banishment
| | |
|---|---|
| **Effect** | Banishes a target to the void |
| **Ingredients** | Ender Pearl + Echo Shard + Nether Quartz |
| **Incantation** | `exilium repellere portam` |
| **XP Cost** | 5 |
| **Requires Target** | Yes |

---

## Incantation Casting

In addition to cauldron rituals, you can cast spells by typing the incantation phrase in chat. You must first learn the incantation (via guide book or admin command).

**Example:** Type `tenebris ferrum obstaculum` in chat to cast Curse of the Deep Mine on yourself or the nearest valid target.

Aliases are available for each incantation (e.g., `deep mine curse` for the mining fatigue curse).

---

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
| `/witchcraft targetpaper <player>` | Create target paper | `witchcraft.admin` |
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
