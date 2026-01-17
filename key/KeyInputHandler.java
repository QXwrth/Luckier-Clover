package com.hqx.luckier_clover.key;

import com.hqx.luckier_clover.screen.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 按键输入处理器
 */
@Mod.EventBusSubscriber(modid = "luckier_clover", value = Dist.CLIENT)
public class KeyInputHandler {
    
    /**
     * 按键按下事件处理
     */
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        // 检查是否按下了配置GUI按键
        if (KeyBindings.OPEN_CONFIG_GUI.isDown()) {
            // 打开配置GUI
            Minecraft minecraft = Minecraft.getInstance();
            Screen currentScreen = minecraft.screen;
            minecraft.setScreen(new ConfigScreen(currentScreen));
        }
    }
}