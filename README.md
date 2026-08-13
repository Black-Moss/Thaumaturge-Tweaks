[中文指南](README_ZH.md)

# Thaumaturge Tweaks

A quality-of-life addon mod for **[Thaumaturge](https://github.com/Leclowndu93150/Thaumaturge)**, built for **NeoForge 26.1.2** (Minecraft 26.1.2).

It adds REI recipe/information support for Thaumaturge's crafting systems, a ThaumicInventoryScanning-style inventory scanning feature, and enhanced controls for the Thaumonomicon research screens.

## Features

### REI Integration (Requires REI)
Adds full support for Thaumaturge content in [Roughly Enough Items](https://www.curseforge.com/minecraft/mc-mods/roughly-enough-items):

- **Arcane Workbench** recipes
- **Crucible** recipes
- **Infusion** recipes, including Infusion Enchantment and Runic Augment variants
- **Dust Trigger** recipes (Salis Mundus triggers)
- **Multiblock** structures
- **Aspect Composition** — how each aspect is combined from its parents
- **Aspect From Stacks** — which items carry a given aspect
- A dedicated **Aspect** entry type, with an information page for every registered aspect

### Inventory Scanning
Inspired by and referencing [Thaumcraft Inventory Scanning](https://www.curseforge.com/minecraft/mc-mods/thaumcraft-inventory-scanning) by Adrimar:

- Hold a **Thaumometer** on your cursor (pick it up from your inventory) and hover over an item in any open container or over your own player model to scan it.
- A short scan animation plays while hovering; once complete, the item/entity is added to your Thaumaturge scan knowledge and its aspects are revealed.
- Already-scanned targets instantly show their aspect tags.
- Requires the mod to be installed on both the client and the server.

### Thaumonomicon Controls
Quality-of-life controls for the research/entry detail screens of the Thaumonomicon:

- **Mouse wheel** up/down — previous/next page
- **Left/Right arrow keys** — previous/next page
- **Backspace** — close the screen
- **Right mouse button** — close the screen (or return from a sub-view first)

## Requirements

| Dependency                                                               | Type     | Notes                                                       |
|--------------------------------------------------------------------------|----------|-------------------------------------------------------------|
| [NeoForge](https://neoforged.net/)                                       | Required | 26.1.2                                                      |
| [Thaumaturge](https://github.com/Leclowndu93150/Thaumaturge)             | Required | Loads before this mod                                       |
| [REI](https://www.curseforge.com/minecraft/mc-mods/roughly-enough-items) | Optional | Client-side only; enables the recipe/information categories |

## Usage

Install the mod into the `mods` folder together with the requirements above. No configuration is needed.

For inventory scanning: pick up a Thaumometer with your cursor, then hover over a container slot or your own player model. Hold still until the scan completes.

## Credits

- **[Thaumaturge](https://github.com/Leclowndu93150/Thaumaturge)** by Leclowndu93150 — the parent mod this addon extends.
- **[Thaumcraft Inventory Scanning](https://www.curseforge.com/minecraft/mc-mods/thaumcraft-inventory-scanning)** by BlayTheNinth — the inventory scanning feature is inspired by and references this mod.

## License

[LGPL-3.0](LICENSE.md)
