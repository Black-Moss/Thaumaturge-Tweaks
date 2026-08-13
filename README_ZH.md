[English Guide](README.md)

# Thaumaturge Tweaks

面向 **[Thaumaturge](https://github.com/Leclowndu93150/Thaumaturge)** 的便利性扩展模组，基于 **NeoForge 26.1.2**（Minecraft 26.1.2）构建。

为本体模组补充了 REI 配方/信息支持、参考 ThaumicInventoryScanning 的物品栏扫描功能，以及魔导手册研究界面的操作增强。

## 功能特性

### REI 集成（需要 REI）
为 [Roughly Enough Items](https://www.curseforge.com/minecraft/mc-mods/roughly-enough-items) 添加了Thaumaturge全部内容支持：

- **奥术工作台**配方
- **熔锅**配方
- **注魔**配方，含注魔附魔与符文强化（Runic Augment）变体
- **尘触发**配方（盐晶触发）
- **多方块**结构
- **要素合成**——每个要素由哪些子要素组合而来
- **要素来源物**——某要素出现在哪些物品上
- 专用的**要素（Aspect）条目类型**，并为每个已注册要素提供信息页

### 物品栏扫描
灵感来源并参考了 [神秘时代物品栏扫描 Thaumcraft Inventory Scanning](https://www.curseforge.com/minecraft/mc-mods/thaumcraft-inventory-scanning)（作者 BlayTheNinth）：

- 将**魔导透镜**拿在鼠标指针上（从物品栏中拿起），悬停在任意打开容器内的物品或自己的玩家模型上即可扫描。
- 悬停时播放短暂的扫描动画，完成后该物品/实体被记入你的扫描知识，并揭示其要素构成。
- 已扫描过的目标会直接显示要素标签。
- 需要客户端与服务端同时安装本模组。

### 魔导手册操作增强
为魔导手册的研究/条目详情界面提供便利操作：

- **鼠标滚轮**上下——上一页/下一页
- **左右方向键**——上一页/下一页
- **退格键**——关闭界面
- **鼠标右键**——关闭界面（处于子视图时先返回主视图）

## 使用方法

将模组与上述依赖一并放入 `mods` 文件夹即可，无需额外配置。

物品栏扫描：用鼠标指针拿起魔导透镜，悬停在容器槽位或自己的玩家模型上，保持不动直到扫描完成。

## 致谢

- **[Thaumaturge](https://github.com/Leclowndu93150/Thaumaturge)**，作者 Leclowndu93150——本模组扩展的父模组。
- **[神秘时代物品栏扫描 Thaumcraft Inventory Scanning](https://www.curseforge.com/minecraft/mc-mods/thaumcraft-inventory-scanning)**，作者 BlayTheNinth——物品栏扫描功能的灵感来源与参考对象。

## 许可证

[LGPL-3.0](LICENSE.md)
