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
- 40+ unique ritual recipes
- Moon phase and weather requirements for powerful rituals
- Coven rituals require multiple members present

### Spoken Incantations
- Cast spells by typing magical phrases in chat
- 27+ learnable incantations
- Cooldowns and arcane exhaustion systems
- No ingredients required

### Curse System
- 15 curses including Plague, Withering, and Madness
- Mining fatigue, bad luck, crop failure, and more
- Curses can be removed through protective rituals

### Protection Wards
- Create protective wards against curses
- Anti-scrying, fire, and projectile shields
- Thorn and Absorption wards
- Reflective backlash for attackers

### Scrying & Divination
- Spy on other players from afar
- Treasure dowsing, aura sight, and player reveal
- Blocked by protective wards

### Covens & Rank System
- Form covens with up to 2 leaders (Priest/Priestess)
- Council members are high-ranking officials
- Initiates are regular members
- 12+ coven rituals requiring coordinated groups
- 11 coven spells with multi-player chanting

### Target Paper System
- Craft target papers at an anvil by renaming paper to a player's name (costs 30 XP levels)
- Used for player-targeting spells and rituals
- Consumed on use

### Spell Books
- 68 written books covering all spells, rituals, and coven magic
- Found as loot in dungeon chests (3% chance)
- Available from librarian villagers (15% chance)
- Admin command to give specific books

### In-Game Grimoire
- `/grimoire incantations` — browse all incantations with spoken text
- `/grimoire rituals` — browse ritual recipes with ingredients
- `/grimoire coven` — browse coven spells with chanting lines
- Paginated with 8 entries per page

## Commands

### Player Commands

| Command | Description |
|---------|-------------|
| `/witchcraft help` | Show help |
| `/witchcraft reload` | Reload configuration |
| `/coven create <name>` | Create a new coven |
| `/coven invite <player>` | Invite a player |
| `/coven accept` | Accept an invite |
| `/coven leave` | Leave your coven |
| `/coven info` | View coven info |
| `/coven setrank <player> <rank>` | Set a member's rank |
| `/coven promote <player>` | Promote to next rank |
| `/coven demote <player>` | Demote to next rank |
| `/coven transfer <player>` | Transfer leadership |
| `/grimoire incantations` | Browse incantations |
| `/grimoire rituals` | Browse ritual recipes |
| `/grimoire coven` | Browse coven spells |

### Admin Commands (`/witchcraft admin`)

| Command | Description |
|---------|-------------|
| `givebook [player]` | Give guide book |
| `learn <player> <id>` | Teach incantation |
| `unlearn <player> <id>` | Remove incantation |
| `purge <player>` | Clear all magic effects |
| `exhaust <player>` | Apply arcane exhaustion |
| `targetpaper <player>` | Create target paper |
| `givegrimoire <player> [id]` | Give spell book |
| `debug` | Show debug info |

## Coven Ranks

| Rank | Authority | Abilities |
|------|-----------|-----------|
| **Priest** | Leader | Full control: invite, kick, set ranks, claim chunks, disband |
| **Priestess** | Leader | Full control: invite, kick, set ranks, claim chunks, disband |
| **Council** | High | Notable member, can be promoted to leader |
| **Initiate** | Basic | Standard member, learns and casts magic |

## Requirements

- Java 21+
- Paper API 1.21.4+
- Optional: PlaceholderAPI

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `witchcraft.admin` | op | Admin commands |
| `witchcraft.cast` | true | Cast spells and rituals |
| `witchcraft.grimoire` | true | Use the grimoire |
| `witchcraft.reload` | op | Reload config |

## Configuration

- `config.yml` — General settings, spell cooldowns, success rates
- `messages.yml` — All user-facing messages (customizable)
- `covens.yml` — Coven data persistence

## PlaceholderAPI Support

| Placeholder | Description |
|-------------|-------------|
| `%witchcraft_exhausted%` | Whether player is exhausted |
| `%witchcraft_coven_name%` | Player's coven name |
| `%witchcraft_coven_size%` | Coven member count |
| `%witchcraft_coven_role%` | Player's rank (Priest, Priestess, Council, Initiate) |
| `%witchcraft_coven_leader%` | Coven leader name |
| `%witchcraft_knows_<spell>%` | Whether player knows a spell |
| `%witchcraft_cooldown_<spell>%` | Cooldown remaining |

## Links

- [GitHub](https://github.com/THTStreamer/witchcraft)
- [Report Issues](https://github.com/THTStreamer/witchcraft/issues)
