<p align="center">
  <img src="https://raw.githubusercontent.com/IntelligenceModding/CommonTrades/refs/heads/assets/Project%20Title.png" alt="Common Trades" width="900">
</p>

<h1 align="center">Datapack Reference</h1>

<p align="center">
  Common Trades uses item tags to discover eligible Wandering Trader offers and to let pack developers include or exclude specific modded items that are not already covered by registered trader pools.
</p>

<p align="center">
  Tags provide candidates. Server config decides whether those candidates are enabled, priced, filtered, and added to traders.
</p>

## Where Files Go

Common Trades uses the mod id `commontrades`.

In a datapack, item tag files go under:

```text
data/commontrades/tags/item/wandering_trader/
```

Supported tag files:

```text
data/commontrades/tags/item/wandering_trader/saplings.json
data/commontrades/tags/item/wandering_trader/flowers.json
data/commontrades/tags/item/wandering_trader/seeds.json
data/commontrades/tags/item/wandering_trader/mushrooms.json
data/commontrades/tags/item/wandering_trader/small_plants.json
data/commontrades/tags/item/wandering_trader/blacklist.json
```

Use `/reload` after changing datapack tags.

## Discovery Tags

Common Trades discovers eligible items from normal Minecraft tags, common `c` tags, and Common Trades tags.

| Category | Source tags | Selection weight | Default output | Default max uses |
| --- | --- | --- | --- | --- |
| Saplings | `minecraft:saplings`, `c:saplings`, `commontrades:wandering_trader/saplings` | `1` | `1` | `8` |
| Flowers | `minecraft:small_flowers`, `c:flowers`, `commontrades:wandering_trader/flowers` | `3` | `1` | `12` |
| Mushrooms | `c:mushrooms`, `commontrades:wandering_trader/mushrooms` | `2` | `1` | `4` |
| Seeds | `minecraft:villager_plantable_seeds`, `c:seeds`, `commontrades:wandering_trader/seeds` | `3` | `1` to `3` | `12` |
| Small plants | `c:small_plants`, `c:vines`, `c:mosses`, `commontrades:wandering_trader/small_plants` | `2` | `1` | `8` |

Only modded items are eligible. Items in the `minecraft` namespace are ignored even if they appear in a Common Trades tag.

## Category Priority

If an item appears in multiple supported tags, Common Trades creates only one candidate. Category priority is deterministic:

```text
saplings, flowers, mushrooms, seeds, small plants
```

For example, an item in both `commontrades:wandering_trader/flowers` and `commontrades:wandering_trader/seeds` is treated as a flower.

## Tag Examples

Add modded saplings:

```json
{
  "replace": false,
  "values": [
    "examplemod:blue_sapling",
    "examplemod:red_sapling"
  ]
}
```

Add a modded seed item:

```json
{
  "replace": false,
  "values": [
    "examplemod:ancient_seeds"
  ]
}
```

Blacklist specific modded items from all Common Trades categories:

```json
{
  "replace": false,
  "values": [
    "examplemod:rare_seed",
    "examplemod:decorative_sapling"
  ]
}
```

## Eligibility Rules

An item must pass all eligibility checks before it can become a Wandering Trader offer.

Common Trades excludes:

- `minecraft:*` items;
- items already identifiable in registered Wandering Trader trades;
- empty or air items;
- items with max stack size `1`;
- damageable items;
- items with creative slot lock data;
- items with hidden tooltip data;
- items in `commontrades:wandering_trader/blacklist`;
- items in the common `c:hidden_from_recipe_viewers` tag;
- items blocked by the server config item, mod, or tag blacklists.

Registered trade detection covers vanilla and modded trader listings whose result items can be inspected from the registered trade table.

If another mod uses a custom dynamic trade factory whose result item cannot be inspected safely, Common Trades may not be able to exclude that item during discovery. The same-trader duplicate cleanup still removes Common Trades offers when the current trader already selected an external offer with the same result item.

## Server Config Interaction

Datapack tags provide candidates. The server config decides whether those candidates can be used.

| Server config | Effect on datapack items |
| --- | --- |
| `general.enabled = false` | No Common Trades offers are added. |
| `general.extraTradesPerTrader = 0` | Discovery still works, but no offers are added to traders. |
| `categories.enableSaplings = false` | Sapling candidates are ignored. |
| `categories.enableFlowers = false` | Flower candidates are ignored. |
| `categories.enableSeeds = false` | Seed candidates are ignored. |
| `categories.enableMushrooms = false` | Mushroom candidates are ignored. |
| `categories.enableSmallPlants = false` | Small plant candidates are ignored. |
| `blacklists.itemBlacklist` | Matching item IDs are ignored. |
| `blacklists.modBlacklist` | Matching mod namespaces are ignored. |
| `blacklists.tagBlacklist` | Items in matching tags are ignored. |

Generated offer prices come from the server config `pricing` section, not from datapack tags.

## Generated Offers

Common Trades appends generated offers after the Wandering Trader's normal offer selection.

Generated offers use:

- one emerald as the input item;
- category-specific emerald prices from server config;
- category-specific output counts and max uses;
- one villager XP;
- a `0.05` price multiplier.

The mod avoids adding result items that are already present in registered Wandering Trader trades where the result can be identified.

It also avoids adding duplicate result items that are already present in the trader's selected offers.

## Visual Markers

Common Trades synchronizes the indexes of generated offers to clients that also have the mod installed.

The client uses those indexes to render the optional Wandering Trader GUI marker and tooltip line.

No custom NBT or permanent item metadata is attached to trade result stacks for marker detection.
