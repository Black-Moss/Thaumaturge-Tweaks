// 物品模型 DataGen：为无尽贪婪联动物品生成扁平物品模型。
package com.blackmoss.thaumaturgetweaks.data.model;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.blackmoss.thaumaturgetweaks.registry.TweaksItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import org.jspecify.annotations.NonNull;

public final class TweaksModelProvider extends ModelProvider {

    public TweaksModelProvider(PackOutput output) {
        super(output, ThaumaturgeTweaks.MODID);
    }

    @Override
    protected void registerModels(@NonNull BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // 扁平物品模型，自动引用 item/<注册名>.png 纹理。
        itemModels.generateFlatItem(TweaksItems.EXTREMELY_PRIMORDIAL_PEARL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(TweaksItems.AKASHIC_RECORDS.get(), ModelTemplates.FLAT_ITEM);
    }
}
