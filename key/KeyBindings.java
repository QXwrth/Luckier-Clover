package com.hqx.luckier_clover.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

/**
 * 按键绑定类
 */
public class KeyBindings {
    // 配置GUI按键绑定
    public static final KeyMapping OPEN_CONFIG_GUI = new KeyMapping(
            "key.luckier_clover.open_config_gui", // 键名 - 与语言文件匹配
            KeyConflictContext.IN_GAME, // 按键冲突上下文
            KeyModifier.NONE, // 修饰键
            InputConstants.Type.KEYSYM, // 按键类型
            InputConstants.KEY_L, // 默认按键
            "key.category.luckier_clover.config" // 按键分类 - 使用自定义分类
    );
    
    /**
     * 注册按键绑定
     */
    public static void register() {
        // 在Forge 1.20.x中，按键绑定通过事件注册
        // 这个方法保留用于兼容性
    }
    
    // 按键绑定注册事件
    @Mod.EventBusSubscriber(modid = "luckier_clover", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class KeyRegisterHandler {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            // 注册按键绑定
            event.register(OPEN_CONFIG_GUI);
        }
    }
}