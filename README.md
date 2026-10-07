# Common Trades

Automatically adds suitable modded items to Wandering Trader trades for seamless modpack integration.

Common Trades is a lightweight NeoForge modpack utility for Minecraft 1.21.1. It discovers eligible modded vegetation and crop-starter items through item tags, then makes a limited number of those items possible Wandering Trader sell offers.

The mod:

- Automatically discovers supported modded items from installed mods
- Uses tags rather than hardcoded compatibility patches
- Preserves vanilla Wandering Trader trades
- Appends to the existing trade pool for better modpack compatibility
- Supports server configuration and item, mod, and tag blacklists
- Adds no blocks, items, mobs, textures, models, custom GUIs, or progression systems

## Examples

Install a biome mod that adds 20 new tagged saplings. Common Trades automatically makes those saplings eligible to appear in Wandering Trader offers.

Install several biome or farming mods. Their tagged flowers, mushrooms, seeds, saplings, and supported small plants can enter Common Trades' cached trade pools, while each trader still receives only a small configured number of Common Trades offers.

## Datapack Tags

Pack developers can supplement automatic discovery with item tags:

- `commontrades:wandering_trader/saplings`
- `commontrades:wandering_trader/flowers`
- `commontrades:wandering_trader/seeds`
- `commontrades:wandering_trader/mushrooms`
- `commontrades:wandering_trader/small_plants`
- `commontrades:wandering_trader/blacklist`

The blacklist tag removes items from all Common Trades categories.

If an item appears in multiple supported tags, Common Trades creates only one candidate. Category priority is deterministic: saplings, flowers, mushrooms, seeds, then small plants.

## Configuration

Common Trades uses a NeoForge server config. It supports:

- Enabling or disabling the mod
- Configuring the target number of Common Trades offers per Wandering Trader
- Enabling or disabling each category
- Configuring the emerald price for each category
- Blacklisting specific items, mod namespaces, or item tags

The config is server-authoritative. On a dedicated server, only the server's config controls generated trades. A remote player cannot change server prices from their local Mods menu. In single-player, the Mods menu config button uses NeoForge's built-in config screen for the loaded world's server config.

## Development

Build the mod:

```bash
./gradlew build
```

Run the development client:

```bash
./gradlew runClient
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

Common Trades is developed as part of the Intelligence Modding Team and released under the MIT License.
