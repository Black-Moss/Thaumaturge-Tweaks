# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and this project adheres
to [Semantic Versioning](https://semver.org/).

---

## v1.3.0

### Added

- Container Scanning: using the Thaumometer on a container block now scans every item inside it, in addition to the block itself. Unlike the base mod, this still works when the container block has already been scanned, so items placed inside later can be scanned too.

### Removed

- Inventory Scanning: removed our duplicate implementation (hover over items in open containers / your player model with the Thaumometer on the cursor). Thaumaturge 0.2.0 now ships this feature itself (`InventoryScanHandler`), including its own tooltip hint, so the addon no longer duplicates it. Container block scanning above is a separate feature and is unaffected.
