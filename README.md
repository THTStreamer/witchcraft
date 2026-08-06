# Witchcraft

An immersive magical progression system for Minecraft Paper servers.

## Overview

Witchcraft adds a deep, ritualistic magic system to Minecraft. Unlike flashy RPG spell plugins, Witchcraft emphasizes preparation, knowledge, and the slow unraveling of ancient mysteries. Every ritual carries weight, every incantation feels valuable.

## Features

### Two Ways to Cast Magic

Witchcraft provides **two separate systems** for casting spells:

1. **Rituals** — Use a cauldron + ingredients (no incantation needed)
2. **Spells** — Type incantations in chat (no ingredients needed)

Both methods trigger the same spell effects. Choose the approach that fits your playstyle.

### Ritual Cauldron Magic
- Perform rituals using cauldrons and ingredients
- Ingredients are consumed when the ritual starts
- Moon phase and weather can affect rituals
- Experience cost for powerful magic

### Spoken Incantations
- Cast spells by typing magical phrases in chat
- Learn incantations from magical books
- Cooldowns and exhaustion systems
- No ingredients required

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

### Covens
- Form groups of witches to unlock more powerful magic
- Coven rituals require multiple members near the cauldron
- Coven spells require members to chant incantations in sequence
- The more members, the stronger the spells
- Failure to meet coven requirements results in losing magic for 3 days

---

## Coven Guide

### Forming a Coven

| Command | Description |
|---|---|
| `/coven create <name>` | Create a new coven (you become the leader) |
| `/coven invite <player>` | Invite a player to your coven |
| `/coven accept` | Accept a pending coven invite |
| `/coven leave` | Leave your current coven |
| `/coven kick <player>` | Kick a member (leader only) |
| `/coven disband` | Disband the coven entirely (leader only) |
| `/coven info` | View your coven's info |
| `/coven list` | List all covens on the server |

### Coven Rituals

Some rituals require a minimum number of coven members to be present near the cauldron. If not enough members are present when the ritual starts, the caster **loses their magic for 3 Minecraft days**.

Coven members must be within a certain radius of the cauldron (varies per ritual).

### Coven Spells

Coven spells are powerful incantations that require multiple members to chant in sequence. Each member says one line of the incantation, in order, within a time limit.

**Example:** A 3-member coven spell requires:
1. Member A says: `ira congregatio spiritus`
2. Member B says: `furor antiquus invocare`
3. Member C says: `potentia devastare hostes`

All members must be within range of each other. If the incantation isn't completed in time, it fails.

### Built-in Coven Spells

| Spell | Members Required | Incantation Lines |
|---|---|---|
| **Wrath of the Coven** | 3 | `ira congregatio spiritus` → `furor antiquus invocare` → `potentia devastare hostes` |
| **Unity Shield** | 2 | `unitas scutum protectionis` → `congregatio defensare animas` |
| **Shared Sight** | 2 | `oculus communis videre` → `animae nexus revelare` |

---

## Ritual Guide (Cauldron + Ingredients)

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

**Target Paper:** For player-targeting rituals, create a target paper with `/witchcraft targetpaper <player>` and add it to the cauldron after adding ingredients. The paper is consumed on use.

---

### Curse Rituals

#### Curse of the Deep Mine
| | |
|---|---|
| **Effect** | Afflicts target with Mining Fatigue |
| **Ingredients** | Nether Wart + Redstone + Coal |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of Misfortune
| | |
|---|---|
| **Effect** | Brings bad luck to the target |
| **Ingredients** | Spider Eye + Fermented Spider Eye + Coal |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Withering
| | |
|---|---|
| **Effect** | Slows healing on the target |
| **Ingredients** | Bone Meal + Ghast Tear + Coal |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of Feebleness
| | |
|---|---|
| **Effect** | Weakens the target |
| **Ingredients** | Bone Meal + Sugar + Coal |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Night Watch
| | |
|---|---|
| **Effect** | Increases phantom activity around the target |
| **Ingredients** | Phantom Membrane + Echo Shard + Coal |
| **XP Cost** | 3 |
| **Requires Target** | Yes |
| **Special** | Requires Full Moon (moon phase 4) |

#### Curse of Barren Fields
| | |
|---|---|
| **Effect** | Causes crop failure around the target |
| **Ingredients** | Bone Meal + Sugar + Nether Wart |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of Sterility
| | |
|---|---|
| **Effect** | Prevents animals from breeding near the target |
| **Ingredients** | Bone Meal + Spider Eye + Nether Wart |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Empty Net
| | |
|---|---|
| **Effect** | Reduces fishing luck for the target |
| **Ingredients** | Nautilus Shell + Prismarine Shard + Coal |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Muted Tongue
| | |
|---|---|
| **Effect** | Prevents a target from casting spells |
| **Ingredients** | Gunpowder + Fermented Spider Eye + Coal |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Shrouded Eye
| | |
|---|---|
| **Effect** | Blinds the target |
| **Ingredients** | Fermented Spider Eye + Coal + Nether Quartz |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Iron Boots
| | |
|---|---|
| **Effect** | Slows the target |
| **Ingredients** | Iron Ingot + Sugar + Coal |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

#### Curse of the Ravenous Maw
| | |
|---|---|
| **Effect** | Makes the target endlessly hungry |
| **Ingredients** | Magma Cream + Sugar + Coal |
| **XP Cost** | 3 |
| **Requires Target** | Yes |

---

### Fertility Rituals

#### Blessing of Abundance
| | |
|---|---|
| **Effect** | Blesses the land with fertility, accelerates crop growth |
| **Ingredients** | Bone Meal + Glowstone Dust + Sugar |
| **XP Cost** | 5 |
| **Special** | Requires Clear Weather |

#### Bloom of the Green Hand
| | |
|---|---|
| **Effect** | Accelerates crop growth in the area |
| **Ingredients** | Bone Meal + Glowstone Dust + Nether Quartz |
| **XP Cost** | 5 |

---

### Warding Rituals

#### Guardian's Ward
| | |
|---|---|
| **Effect** | Creates a ward against hostile mobs |
| **Ingredients** | Obsidian + Echo Shard + Bone Meal |
| **XP Cost** | 5 |

---

### Cleansing Rituals

#### Rite of Purification
| | |
|---|---|
| **Effect** | Removes curses from a player |
| **Ingredients** | Ghast Tear + Sugar + Bone Meal |
| **XP Cost** | 4 |

#### Purification of the Clear Spring
| | |
|---|---|
| **Effect** | Cleanses nearby water sources |
| **Ingredients** | Ghast Tear + Nether Quartz + Bone Meal |
| **XP Cost** | 4 |

---

### Protection Rituals

#### Ward of Protection
| | |
|---|---|
| **Effect** | Creates a protective ward against curses |
| **Ingredients** | Obsidian + Amethyst Shard + Glowstone Dust |
| **XP Cost** | 5 |

#### Veil of Obscurity
| | |
|---|---|
| **Effect** | Blocks scrying attempts on you |
| **Ingredients** | Echo Shard + Amethyst Shard + Crying Obsidian |
| **XP Cost** | 5 |

#### Ward of the Immolator
| | |
|---|---|
| **Effect** | Grants fire resistance through ritual |
| **Ingredients** | Blaze Powder + Obsidian + Glowstone Dust |
| **XP Cost** | 5 |

#### Aegis of Deflection
| | |
|---|---|
| **Effect** | Protects against projectile attacks |
| **Ingredients** | Iron Ingot + Obsidian + Flint |
| **XP Cost** | 5 |

---

### Divination Rituals

#### Mirror of the Soul
| | |
|---|---|
| **Effect** | Allows scrying on another player (location, health, items) |
| **Ingredients** | Echo Shard + Amethyst Shard + Ender Pearl |
| **XP Cost** | 7 |
| **Requires Target** | Yes |

#### Dowsing of the Hidden Way
| | |
|---|---|
| **Effect** | Reveals nearby hidden structures |
| **Ingredients** | Echo Shard + Gold Ingot + Amethyst Shard |
| **XP Cost** | 7 |

#### The Witch's Third Eye
| | |
|---|---|
| **Effect** | Reveals a target's inner aura and status |
| **Ingredients** | Amethyst Shard + Echo Shard + Glowstone Dust |
| **XP Cost** | 7 |
| **Requires Target** | Yes |

---

### Ritual-Based Spells

#### Gathering of Lost Souls
| | |
|---|---|
| **Effect** | Collects ambient soul energy |
| **Ingredients** | Echo Shard + Nether Quartz + Coal |
| **XP Cost** | 5 |

#### Chains of the Bound Soul
| | |
|---|---|
| **Effect** | Binds a target's spirit to a location |
| **Ingredients** | Iron Ingot + Echo Shard + Coal |
| **XP Cost** | 4 |
| **Requires Target** | Yes |

#### Rite of Banishment
| | |
|---|---|
| **Effect** | Banishes a target to the void |
| **Ingredients** | Ender Pearl + Echo Shard + Nether Quartz |
| **XP Cost** | 5 |
| **Requires Target** | Yes |

#### Calling of the Bound
| | |
|---|---|
| **Effect** | Summons a target player to your location |
| **Ingredients** | Ender Pearl + Echo Shard + Amethyst Shard |
| **XP Cost** | 6 |
| **Requires Target** | Yes |

---

## Spell Guide (Spoken Incantations)

### How to Cast a Spell via Incantation

1. **Learn the incantation** from a spell book (right-click to learn)
2. **Type the incantation phrase** in chat
3. The spell is cast on yourself (or the nearest valid target)
4. For player-targeting spells, hold a **target paper** in your main or off hand

**Requirements:**
- Permission: `witchcraft.cast`
- Must have learned the incantation
- Enough XP levels
- Not under Arcane Exhaustion
- Cooldown must have expired

---

### Curse Incantations

| Spell | Incantation | Alias |
|-------|-------------|-------|
| Curse of the Deep Mine | `tenebris ferrum obstaculum` | `deep mine curse` |
| Curse of Misfortune | `fortuna inversa cadat` | `misfortune curse` |
| Curse of the Withering | `vita sanatio lento` | `withering curse` |
| Curse of Feebleness | `vis deficiat invalidus` | `feebleness curse` |
| Curse of the Night Watch | `umbra vigilare noctis` | `night watch curse` |
| Curse of Barren Fields | `agricultura maledictio sterile` | `barren fields curse` |
| Curse of Sterility | `procreatio negare infructuosa` | `sterility curse` |
| Curse of the Empty Net | `piscis fortuna careat` | `empty net curse` |
| Curse of the Muted Tongue | `mutus lingua sileat` | `muted tongue curse` |
| Curse of the Shrouded Eye | `oculus caligo tenebris` | `shrouded eye curse` |
| Curse of the Iron Boots | `gradus lentus ferrum` | `iron boots curse` |
| Curse of the Ravenous Maw | `esuries fames devorat` | `ravenous maw curse` |

### Protection Incantations

| Spell | Incantation | Alias |
|-------|-------------|-------|
| Ward of Protection | `custodio sanctum aegis` | `ward protection` |
| Veil of Obscurity | `obscurus ne videar` | `veil obscurity` |
| Ward of the Immolator | `ignis scutum immolare` | `immolator ward` |
| Aegis of Deflection | `aegis deflexio projicere` | `deflection aegis` |

### Divination Incantations

| Spell | Incantation | Alias |
|-------|-------------|-------|
| Mirror of the Soul | `speculum anima videre` | `mirror soul` |
| Dowsing of the Hidden Way | `thesaurus videre via abscondita` | `hidden way dowsing` |
| The Witch's Third Eye | `oculus tertius aura videre` | `third eye sight` |

### Fertility Incantations

| Spell | Incantation | Alias |
|-------|-------------|-------|
| Blessing of Abundance | `terra foecunditas abundet` | `abundance blessing` |
| Bloom of the Green Hand | `germen floreant viridis` | `green hand bloom` |

### Warding Incantations

| Spell | Incantation | Alias |
|-------|-------------|-------|
| Guardian's Ward | `custos praesidio locus` | `guardian ward` |

### Cleansing Incantations

| Spell | Incantation | Alias |
|-------|-------------|-------|
| Rite of Purification | `purgatio maledictio liberare` | `purification rite` |
| Purification of the Clear Spring | `aqua purificatio fons` | `clear spring purification` |

### Ritual-Based Incantations

| Spell | Incantation | Alias |
|-------|-------------|-------|
| Gathering of Lost Souls | `anima colligere umbrarum` | `lost souls gathering` |
| Chains of the Bound Soul | `catena anima vincire` | `bound soul chains` |
| Rite of Banishment | `exilium repellere portam` | `banishment rite` |

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
