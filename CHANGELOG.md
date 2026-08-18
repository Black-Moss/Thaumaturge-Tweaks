# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and this project adheres
to [Semantic Versioning](https://semver.org/).

---

## v1.2.0

### Added

- Research Table: scroll the aspect palette with the mouse wheel (hover over the palette) or the PageUp / PageDown / arrow keys.
- Research Table: drop one palette aspect onto another to combine them directly; hold Shift to batch-combine up to 10 times (as many as the available materials allow).
- Research Table: the aspect combination helper panel can now be paged with the mouse wheel, and closed with Backspace or the right mouse button.
- Research Table: right-click a placed aspect on the research hex grid to erase it (same behavior as left-click erase).
- REI: the Arcane Workbench category now shows the missing research on the research gate barrier tooltip (previously the barrier was shown without any explanation).
- Curios: right-clicking the Revealing Goggles now equips them into the Curios head slot first (falling back to the helmet slot when the head slot is full).
- Aspect Icons: while holding Shift, Essentia Phials and Essentia Crystal Shards render as their contained aspect icon (including the aspect's color) instead of the phial/shard texture — in GUIs, in hand, as dropped items, and in REI entry panels (inspired by and referencing [Thaumcraft Aspect Annotations](https://github.com/Aedial/Thaumcraft-Aspect-Annotations)).
- Thaumatorium: scroll the recipe grid with the mouse wheel (hover over the grid) or the PageUp / PageDown / arrow keys (same as clicking the up/down arrows).

### Fixed

- REI: the Arcane Workbench category no longer shows a fake barrier item in the essence crystal column when a recipe does not use crystals from the start of the primal order — the placeholder is now an empty slot, matching the base mod.
