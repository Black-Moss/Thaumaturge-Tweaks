# ThaumaturgeJS — KubeJS 集成独立模组技术方案

> 本文档是未来独立模组 **ThaumaturgeJS** 的技术方案与实现笔记。
> 目标：让 Thaumaturge 的配方、要素、研究、扫描等内容可通过 KubeJS 脚本自定义。
> 背景：本 addon（ThaumaturgeTweaks）明确**不做** KubeJS 硬依赖；KubeJS 支持以独立模组形式交付，与 Thaumaturge、KubeJS 均为可选/外部依赖关系。

---

## 0. 版本坐标（重要）

| 组件              | 值                                                                                |
|-------------------|-----------------------------------------------------------------------------------|
| Minecraft         | 1.21.1                                                                            |
| NeoForge          | 26.1.2.x                                                                          |
| Thaumaturge       | 26.1.2-NeoForge-BETA-0.1.4（本地源码 `E:\MossProject\MinecraftJava\Thaumaturge`） |
| KubeJS (NeoForge) | **`26.1.2-8.0.4`**（对应 NeoForge 26.1.2，Maven 元数据确认的最新稳定版）          |
| Rhino             | KubeJS 传递依赖（`dev.latvian.apps`），无需显式声明                               |
| Architectury      | KubeJS 传递依赖                                                                   |

KubeJS Maven 仓库：
```groovy
maven {
    url "https://maven.latvian.dev/releases"
    content {
        includeGroup "dev.latvian.mods"
        includeGroup "dev.latvian.apps"
    }
}
// 可选：rhino 需要的 jitpack
maven {
    url 'https://jitpack.io'
    content {
        includeGroup "com.github.rtyley"
    }
}
```

---

## 1. 架构与依赖决策

- **独立模组**（非 addon）：mod_id 建议 `thaumaturgejs`，包 `com.blackmoss.thaumaturgejs`。
- **依赖**：
  - `compileOnly` Thaumaturge（`thaumaturge-26.1.2-NeoForge-BETA-0.1.4`，flatDir）
  - `compileOnly` KubeJS（`dev.latvian.mods:kubejs-neoforge:26.1.2-8.0.4`），运行时**不**随模组分发（KubeJS 由用户自行安装）
  - `interfaceInjectionData("dev.latvian.mods:kubejs-neoforge:26.1.2-8.0.4")`（可选，提供接口注入）
- `neoforge.mods.toml`：声明 `kubejs` 与 `thaumaturge` 为**可选依赖**（`type="optional"`），保证没有 KubeJS 时模组本身不加载插件逻辑也不崩溃。
- 插件加载由 KubeJS 驱动（见 §2），无 KubeJS 时整个插件类不会被加载。

---

## 2. KubeJS 插件注册机制（核心）

KubeJS 插件 = 一个实现 KubeJS 插件接口/继承基类的 Java 类，通过资源文件注册：

### `src/main/resources/kubejs.plugins.txt`
```
com.blackmoss.thaumaturgejs.ThaumaturgeKubePlugin thaumaturgejs
```
- 每行 `<插件类全名> [modid]`；`modid` 后缀用于把插件与 mod 关联（推荐写，便于定位与条件加载）。

### 插件类需实现的钩子（KubeJSPlugin 基类方法，逐个 override）

| 方法                                                | 用途                         | 对应阶段 |
|-----------------------------------------------------|------------------------------|----------|
| `init()` / `afterInit()`                            | 插件初始化 / KubeJS 初始化后 | 阶段 0   |
| `registerRecipeTypes(registry)`                     | 注册自定义配方处理器类型     | 配方     |
| `registerBindings(BindingsEvent event)`             | 暴露 Java 静态门面给脚本     | 绑定     |
| `registerEvents(EventGroup group)`                  | 注册自定义事件组             | 事件     |
| `registerClasses(ClassFilter filter)`               | 声明脚本可访问的类/包        | 通用     |
| `registerWrappers(WrapperEvent event)`              | 类型包装器                   | 通用     |
| `attachPlayerData/attachWorldData/attachServerData` | 附加脚本侧数据               | 可选     |

参考实现：KubeJS 仓库内 `KubeJS-Mods/KubeJS` 的官方插件（如 `MekanismKubeJSPlugin`、`KubeJSCreate`），以及 `AntimatterAPI`（简单 binding 示例）。

### 类过滤器（可选，二选一）
- 插件内 `registerClasses`；或
- 资源文件 `src/main/resources/kubejs.classfilter.txt`：
  ```
  +com.leclowndu93150.thaumaturge.api   // 允许 api 包
  -com.leclowndu93150.thaumaturge.content  // 拒绝 content 内部
  ```

---

## 3. 阶段一：自定义配方（价值最高）

### 3.1 RecipeSchema 系统

KubeJS 自 v6 起用 **RecipeSchema** 声明配方 JSON 结构，两种方式：

**方式 A：JSON Schema（首选）**
- 注册中心：`kubejs/recipe_schema/<modid>/<type>.json`（`ResourceLocation`）
- 或 DataGen 生成：继承 `dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaProvider`，在 `GatherDataEvent` 中 `add(ResourceLocation("mod","recipe"), builder -> {...})`，`builder.mappings("modRecipe", "hungry")` 等
- **DataGen 需在 run config 加 `--existing-mod kubejs`**

**方式 B：Java `RecipeSchema` 直接构造**（`registerRecipeHandlers` 事件）
- `RecipeSchema.of(RecipeComponent..., RecipeBuilderFactory)`，每个 `RecipeComponent` 对应 JSON 一个字段
- `RecipeComponent`：`RecipeComponent.of(Class<T>, String key, ...)`，可组合成 `ItemStackComponent`、`IngredientComponent`、`TagComponent` 等
- 要素这类"非物品"输入需自定义 `RecipeComponent`（见 §3.3）

### 3.2 Thaumaturge 5 类配方目标

| 配方       | RecipeType (TCRecipeTypes)       | 关键字段                                                                                                             | 备注                                                           |
|------------|----------------------------------|----------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------|
| 奥术工作台 | `ARCANE`                         | 3x3 格（有序/无序）、`ItemStackTemplate` 输出、`AspectList` 水晶消耗、`int` baseVis、可选 `ResearchGate`             | `ArcaneShapedCraftingRecipe` / `ArcaneShapelessCraftingRecipe` |
| 熔锅       | `CRUCIBLE`                       | `Ingredient` 催化剂、`AspectList` 要素、`ItemStackTemplate` 输出、`ResearchGate`                                     | `CrucibleRecipe`                                               |
| 注魔       | `INFUSION`                       | `Ingredient` 催化剂、`List<Ingredient>` 组件、`AspectList` 要素、`int` instability、`ItemStack` 输出、`ResearchGate` | `InfusionRecipe`                                               |
| 注魔附魔   | `INFUSION_ENCHANTMENT`           | 同注魔                                                                                                               | `InfusionEnchantmentRecipe`                                    |
| 符文强化   | `RUNIC_AUGMENT`                  | 同注魔                                                                                                               | `InfusionRunicAugmentRecipe`                                   |
| 尘触发     | `DUST_TRIGGER`                   | `Block`/`TagKey<Block>` 目标、`ItemStackTemplate` 输出、`ResearchGate`                                               | `DustTriggerSimpleRecipe` / `DustTriggerTagRecipe`             |
| 多方块触发 | `DUST_TRIGGER`（`isMultiblock`） | `Identifier` blueprintId、输出、`ResearchGate`                                                                       | `DustTriggerMultiblockRecipe`，配方本体引用 `Blueprint` 注册表 |

> 全部配方类实现 `ResearchGated`（`researchGate(): Optional<ResearchGate>` / `doesPassGate(player)`）。
> 配方对象经 `RecipeManager` 落库后，本体的 REI/JEI 类别、工作台配方书、研究门控自动生效。

### 3.3 要素输入（关键难点）

Thaumaturge 要素（`AspectList`）不是 Minecraft 原生物品/标签，KubeJS 默认 RecipeComponent 不识别。方案：

- **自定义 `RecipeComponent`**：`AspectListComponent`，映射 JSON 字段如 `"aspects": {"aer": 4, "ignis": 2}`，内部把字符串 key 解析为 `Holder<IAspect>`（用 `TCAspects` 常量 / 注册表查）。
- 最终 schema 的 value 类型是 `AspectList`（或 `List<AspectInstance>`），由自定义 `RecipeBuilder` 转成 `AspectList`。
- 可选的脚本侧友好 API：提供绑定方法 `Aspects.of("aer", 4)` 等。

### 3.4 落库路径

- KubeJS 的 `RecipeSchema` 处理器最终产出 **JSON**，经 `RecipeManager` 标准流程加载，注册进 Thaumaturge 的 `RecipeType`。
- 不需要改 Thaumaturge 注册代码：只要 schema 产出的 JSON 与 Thaumaturge 配方 `MapCodec` 字段一致即可（`catalyst`/`aspects`/`result`/`research`/`components`/`instability`/`blueprint` 等，参考各配方类 `MAP_CODEC`）。

---

## 4. 阶段二：自定义绑定

`registerBindings(BindingsEvent event)` 暴露 Thaumaturge facade 给脚本（`event.add(String name, Object value)`）：

| 绑定名        | 目标                               | 脚本用法示例                                                                                           |
|---------------|------------------------------------|--------------------------------------------------------------------------------------------------------|
| `Aspects`     | `TCAspects` 常量 + 注册表辅助      | `Aspects.aer`、`Aspects.of("praecantatio")`                                                            |
| `AspectIndex` | `AspectIndexAccess`                | `AspectIndex.of(item)` 查物品要素                                                                      |
| `Scanning`    | `ScanningManager`                  | `Scanning.isScannable(player, target)`、`Scanning.scan(player, target)`、`Scanning.itemAspects(stack)` |
| `Research`    | `KnowledgeAccess` + `ResearchGate` | `Research.isKnown(player, key)`、`Research.complete(player, key)`                                      |
| `AspectPool`  | `AspectPoolAccess`                 | 读写玩家要素池                                                                                         |

> 注意：`ScanningManager.scanTheThing` 仅服务器有效；`KnowledgeAccess.of(player)` 读同步副本可客户端安全调用。绑定只暴露**读/安全** API 给脚本，禁止暴露 `.bind(...)` 相关内部。

---

## 5. 阶段三：自定义事件

`registerEvents(EventGroup group)` 注册事件组（脚本侧 `ThaumaturgeEvents.*`）。把 Thaumaturge 的 NeoForge 事件转发为 KubeJS `SimpleEvent`/`CancellableEvent`：

Thaumaturge 领域事件（`api.research` 下，均可在 NeoForge.EVENT_BUS 监听并可取消）：
- `ResearchEvent.Unlocked`
- `ResearchEvent.StageAdvanced`
- `ResearchEvent.Completed`
- `ResearchEvent.KnowledgeGained`

KubeJS 侧：
- `group.server` / `group.player` 等作用域下注册 `SimpleEvent`，在转发监听器里 `post()`。
- 事件数据（玩家、研究 key、知识量）通过 `ScriptType` 上下文传入脚本。
- 示例脚本：`ThaumaturgeEvents.researchCompleted(event => { ... })`

---

## 6. 阶段四：自定义内容

通过绑定/事件暴露 Thaumaturge 的注册扩展点：

| 扩展点         | Thaumaturge API                                                                   | KubeJS 暴露方式          |
|----------------|-----------------------------------------------------------------------------------|--------------------------|
| 自定义要素注册 | `IAspect.REGISTRY_KEY` 注册表 + `AspectIndexBuilder.fireContributorEvent(modBus)` | 启动脚本绑定，写入注册表 |
| 扫描目标       | `ScanningManager.addScannableThing(IScanThing)`                                   | 事件/绑定暴露            |
| 研究条目       | `TCResearchEntries` / `TCResearchCategories` 注册表                               | 绑定                     |
| 多方块蓝图     | `Blueprint.REGISTRY_KEY`（datapack 注册表，datagen 生成）                         | 绑定生成 JSON            |

> 蓝图本质是注册表数据，KubeJS 可直接通过 `ServerEvents.registry` 或数据包 JSON 提供，优先级低。

---

## 7. 参考源码与链接

- Thaumaturge 本体源码（权威 API 参考）：`E:\MossProject\MinecraftJava\Thaumaturge`
  - 配方类：`content/recipe/**`、`content/infusion/**`
  - 注册表：`registry/TCRecipeTypes.java`、`registry/TCItems.java`、`registry/TCSounds.java`
  - facade：`api/**`（`aspect`、`research`、`recipe`、`aura`、`warp` 等）
  - 研究事件：`api/research/ResearchEvent.java`
- KubeJS：
  - 官方 Wiki：https://kubejs.com/wiki/（建设中）
  - GitHub：https://github.com/KubeJS-Mods/KubeJS （README 含插件开发要点）
  - 版本确认：`https://maven.latvian.dev/releases/dev/latvian/mods/kubejs-neoforge/maven-metadata.xml`
- 参考插件实现：`KubeJS-Mods/KubeJS-Create`、`AntimatterAPI`（binding 示例）、MC百科 KubeJS 1.21.1 中文资料

---

## 8. 已知注意事项

1. **`@EventBusSubscriber` 无 `bus` 属性**（NeoForge 26）：事件按类型自动分发给 mod bus / NeoForge bus，勿再写 `bus = EventBusSubscriber.Bus.MOD`。
2. **KubeJS 插件是运行时加载**：`kubejs.plugins.txt` 的 modid 后缀保证无 KubeJS 时不注册；编译期仅 `compileOnly`，运行时若缺 KubeJS 需确保插件类不被类加载器触碰（KubeJS 负责）。
3. **RecipeSchema 的 DataGen 需要 `--existing-mod kubejs`**，否则 schema 提供者找不到 KubeJS 数据。
4. **要素 Component 必须自定义**，这是配方支持里最容易踩坑的点；解析用 `TCAspects` 常量或 `IAspect.REGISTRY_KEY` 注册表按 id 反查。
5. **配方 JSON 字段严格对齐 Thaumaturge `MAP_CODEC`**：`ArcaneCraftingRecipe` 的 result 是 `ItemStackTemplate`（record，非 `ItemStack`），schema 输出时注意。
6. **`ScanningManager.scanTheThing` 服务器专用**；客户端只能 `isThingStillScannable`（读同步副本）。
7. 绑定只暴露 facade 读 API，绝不暴露 `XxxAccess.bind(...)`。

---

## 9. 交付顺序建议（独立模组）

1. 搭工程：build.gradle（kubejs + thaumaturge 依赖）、kubejs.plugins.txt、mods.toml 可选依赖
2. 配方：先做**熔锅**（字段最少、最容易验证）→ 奥术工作台 → 注魔 → 尘触发 → 多方块
3. 绑定：Aspects / AspectIndex / Scanning / Research
4. 事件：研究事件转发
5. 内容：要素注册 / 扫描目标 / 研究 / 蓝图
6. 每个阶段跑 `runClient` + `runServer` 验证脚本生效

---

*文档由 ThaumaturgeTweaks 项目调研沉淀，供 ThaumaturgeJS 独立模组启动时使用。*
