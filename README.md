[中文指南](README_ZH.md)

# Thaumaturge Tweaks

A quality-of-life addon mod for **[Thaumaturge](https://github.com/Leclowndu93150/Thaumaturge)**, built for **NeoForge 26.1.2** (Minecraft 26.1.2).

It adds REI recipe/information support for Thaumaturge's crafting systems, a ThaumicInventoryScanning-style inventory scanning feature, and enhanced controls for the Thaumonomicon, Research Table, and Thaumatorium screens.

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

### Research Table Controls
Quality-of-life enhancements for the Research Table:

- Scroll the **aspect palette** with the **mouse wheel** (hover over the palette) or the **PageUp / PageDown / arrow keys**.
- **Drag-and-drop combining** — drop one palette aspect onto another to combine them instantly; hold **Shift** to batch-combine up to 10 times (as many as your available materials allow).
- The **aspect combination helper panel** can be paged with the mouse wheel and closed with **Backspace** or the **right mouse button**.
- **Right-click** a placed aspect on the research hex grid to erase it (same as left-click erase).

### Thaumatorium Controls
Quality-of-life controls for the Thaumatorium (automatic alchemy) screen:

- **Mouse wheel** over the recipe grid — previous/next page (same as clicking the up/down arrows)
- **PageUp / PageDown** or **Up/Down arrow keys** — previous/next page

### Aspect Icons on Shift (Essentia Phial & Crystal Shards)
Hold **Shift** to see the actual **aspect icon** on certain aspect-carrying items (inspired by [Thaumcraft Aspect Annotations](https://github.com/Aedial/Thaumcraft-Aspect-Annotations); the render also borrows the material-preview style of Avaritia's singularity items):

- While **Shift** is held, **Essentia Phials** and **Essentia Crystal Shards** render as their contained **aspect icon** (including the aspect's color) instead of the phial/shard texture — in GUIs, in hand, as dropped items, and in REI entry panels.
- Releasing **Shift** restores the default item texture.
- Only these two items are affected; other items keep their normal rendering.

## Credits

- **[Thaumaturge](https://github.com/Leclowndu93150/Thaumaturge)** by Leclowndu93150 — the parent mod this addon extends.
- **[Thaumcraft Inventory Scanning](https://www.curseforge.com/minecraft/mc-mods/thaumcraft-inventory-scanning)** by BlayTheNinth — the inventory scanning feature is inspired by and references this mod.
- **[Thaumcraft Aspect Annotations](https://github.com/Aedial/Thaumcraft-Aspect-Annotations)** by Aedial — the aspect icon display feature is inspired by and references this mod.

## Authorization

This mod is open-sourced with the author's permission.

![authorization](authorization.png)

## License

[LGPL-3.0](LICENSE.md)
