package com.hqx.luckier_clover;

import com.hqx.luckier_clover.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 创造模式标签页注册器
 * 专门处理所有创造模式标签页的注册
 */
public class CreativeModeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LuckierClover.MOD_ID);

    public static final RegistryObject<CreativeModeTab> LUCKY_CLOVER_TAB = CREATIVE_MODE_TABS.register("luckierclover_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.luckierclover"))
            .icon(() -> ModItems.LUCKY_CLOVER.get().getDefaultInstance())
            .build());
}