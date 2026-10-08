<p align="center">
  <img src="https://raw.githubusercontent.com/IntelligenceModding/CommonTrades/refs/heads/assets/Project%20Title.png" alt="Common Trades" width="900">
</p>

<h1 align="center">Config Reference</h1>

<p align="center">
  Common Trades has a server config for Wandering Trader gameplay behavior and a client config for local visual markers.
</p>

<p align="center">
  The server config is authoritative for trade generation. The client config only changes how Common Trades-generated offers are marked in the local Wandering Trader screen.
</p>

## Config Files

The server config is registered as a Forge server config. In a running world it is normally written as:

```text
<world>/serverconfig/commontrades-server.toml
```

The client config is normally written as:

```text
config/commontrades-client.toml
```

On a dedicated server, only the server's `commontrades-server.toml` controls generated trades.

In singleplayer, the loaded world's server config controls gameplay. Forge config tools can edit the loaded world's server config when available.

The Forge Mods screen config button opens Common Trades' client visual-marker settings. Server-authoritative gameplay settings remain in the world or dedicated-server `commontrades-server.toml` file.

## Server Config Sections

| Section | Keys | What it controls |
| --- | --- | --- |
| `general` | `enabled`, `extraTradesPerTrader` | Master server switch and target number of Common Trades offers per Wandering Trader. |
| `categories` | `enableSaplings`, `enableFlowers`, `enableSeeds`, `enableMushrooms`, `enableSmallPlants` | Which discovered item categories can generate offers. |
| `pricing` | `saplingEmeraldCost`, `flowerEmeraldCost`, `seedsEmeraldCost`, `mushroomsEmeraldCost`, `smallPlantsEmeraldCost` | Emerald prices used by generated offers in each category. |
| `blacklists` | `itemBlacklist`, `modBlacklist`, `tagBlacklist` | Server-side filters that remove items from all Common Trades discovery. |

## Server Defaults

| Key | Default | Valid range | Meaning |
| --- | --- | --- | --- |
| `general.enabled` | `true` | `true` or `false` | Enables Common Trades offer generation. |
| `general.extraTradesPerTrader` | `2` | `0` to `3` | Target number of Common Trades offers per Wandering Trader. |
| `categories.enableSaplings` | `true` | `true` or `false` | Allows sapling offers. |
| `categories.enableFlowers` | `true` | `true` or `false` | Allows flower offers. |
| `categories.enableSeeds` | `true` | `true` or `false` | Allows seed offers. |
| `categories.enableMushrooms` | `true` | `true` or `false` | Allows mushroom offers. |
| `categories.enableSmallPlants` | `true` | `true` or `false` | Allows small plant offers. |
| `pricing.saplingEmeraldCost` | `5` | `1` to `32` | Emerald cost for one sapling. |
| `pricing.flowerEmeraldCost` | `1` | `1` to `16` | Emerald cost for one flower. |
| `pricing.seedsEmeraldCost` | `1` | `1` to `16` | Emerald cost for one to three seeds. |
| `pricing.mushroomsEmeraldCost` | `1` | `1` to `16` | Emerald cost for one mushroom. |
| `pricing.smallPlantsEmeraldCost` | `1` | `1` to `16` | Emerald cost for one small plant. |
| `blacklists.itemBlacklist` | `[]` | item IDs | Item IDs that Common Trades must never offer. |
| `blacklists.modBlacklist` | `[]` | mod namespaces | Mod namespaces that Common Trades must never offer. |
| `blacklists.tagBlacklist` | `[]` | item tag IDs | Item tag IDs whose contents Common Trades must never offer. A leading `#` is optional. |

Vanilla Wandering Traders select five generic offers, so `extraTradesPerTrader` is intentionally capped at `3`.

## Blacklist Examples

```toml
[blacklists]
itemBlacklist = ["examplemod:rare_seed", "examplemod:decorative_sapling"]
modBlacklist = ["examplemod"]
tagBlacklist = ["examplemod:not_for_traders", "#c:hidden_from_recipe_viewers"]
```

Malformed blacklist entries are ignored and logged.

The built-in `commontrades:wandering_trader/blacklist` item tag is always respected.

## Server Authority

Gameplay settings are server-authoritative.

On a dedicated server, a remote player's local config cannot change prices, categories, blacklist behavior, or how many Common Trades offers a trader receives.

Client-side settings do not affect trade contents, prices, availability, or server-side gameplay.

## Client Config

Client settings only affect local Wandering Trader GUI rendering.

These settings can be edited from the Forge Mods screen config button or directly in `config/commontrades-client.toml`.

| Section | Key | Default | Valid range | Meaning |
| --- | --- | --- | --- | --- |
| `visualMarkers` | `visualIndicators` | `true` | `true` or `false` | Shows Common Trades trade-row outlines and the `Added by Common Trades` tooltip line. |
| `visualMarkers` | `outlineOpacity` | `100` | `0` to `100` | Opacity percentage for the trade-row outline. |
| `visualMarkers` | `outlineColor` | `"#00D26A"` | RGB hex color | Color for the trade-row outline. Accepted forms are `#00D26A`, `00D26A`, and `0x00D26A`. |

Client visual markers require the server to send Common Trades offer indexes.

No custom NBT or permanent item metadata is attached to trade result stacks for marker detection.

## Debug Command

Common Trades includes an operator-only debug command for inspecting Wandering Trader trade pools:

```text
/commontrades trades
/commontrades trades <page>
/commontrades trades commontrades
/commontrades trades vanilla
/commontrades trades <modid>
```

The command requires permission level `2`.

Use it while developing modpacks, datapacks, or compatibility changes to verify which offers are visible to Common Trades and how they are grouped.

Common Trades uses the registered Wandering Trader trade table to avoid adding tagged items that are already supplied by vanilla or another mod.

If a registered trade's result cannot be inspected safely because it is generated by custom dynamic code, the debug command may show it as `dynamic/unknown`, and Common Trades may only be able to catch duplicates after the current trader selects that external offer.
