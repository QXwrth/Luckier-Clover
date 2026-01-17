package com.hqx.luckier_clover.item;

import com.hqx.luckier_clover.LuckierClover;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 物品注册器
 * 专门处理所有物品的注册
 */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LuckierClover.MOD_ID);

    public static final RegistryObject<Item> LUCKY_CLOVER = ITEMS.register("lucky_clover",
        () -> new LuckyCloverItem(new Item.Properties().stacksTo(64)));
}