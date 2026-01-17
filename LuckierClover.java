package com.hqx.luckier_clover;

import com.hqx.luckier_clover.item.ModItems;
import com.hqx.luckier_clover.key.KeyBindings;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * 幸运四叶草Mod主类
 * 为游戏添加幸运相关的功能和物品
 */
@Mod(LuckierClover.MOD_ID)
public class LuckierClover
{
    public static final String MOD_ID = "luckier_clover";
    private static final Logger LOGGER = LogUtils.getLogger();

    public LuckierClover(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // 注册设置事件
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        // 注册所有内容
        ModItems.ITEMS.register(modEventBus);
        CreativeModeTabRegistry.CREATIVE_MODE_TABS.register(modEventBus);

        // 注册事件处理器
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("幸运四叶草Mod初始化完成");
    }

    // 添加物品到创造模式标签页
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTabKey() == CreativeModeTabRegistry.LUCKY_CLOVER_TAB.getKey())
            event.accept(ModItems.LUCKY_CLOVER);
    }

    // 客户端设置事件
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // 注册按键绑定
            KeyBindings.register();
            LOGGER.info("幸运四叶草Mod客户端已设置");
        }
    }
}
