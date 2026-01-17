package com.hqx.luckier_clover.screen;

import com.hqx.luckier_clover.Config;
import com.hqx.luckier_clover.key.KeyBindings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * 配置GUI界面
 */
public class ConfigScreen extends Screen {
    private final Screen parent;
    private double currentDropChance;
    private double currentCreeperChance;
    private EditBox dropChanceInput;
    private EditBox creeperChanceInput;
    
    /**
     * 构造函数
     */
    public ConfigScreen(Screen parent) {
        super(Component.translatable("screen.luckier_clover.config.title"));
        this.parent = parent;
        this.currentDropChance = Config.getDropFromGrassChance();
        this.currentCreeperChance = Config.getCreeperSpawnChance();
    }
    
    @Override
    protected void init() {
        // 创建保存按钮
        Button saveButton = Button.builder(
                Component.translatable("screen.luckier_clover.config.save"), 
                (button) -> {
                    // 保存配置
                    try {
                        // 解析输入的百分比值
                        String dropChanceStr = dropChanceInput.getValue().replace(",", ".");
                        String creeperChanceStr = creeperChanceInput.getValue().replace(",", ".");
                        
                        double dropChancePercent = Double.parseDouble(dropChanceStr);
                        double creeperChancePercent = Double.parseDouble(creeperChanceStr);
                        
                        // 将百分比转换为小数（0-1范围）
                        double dropChance = dropChancePercent / 100.0;
                        double creeperChance = creeperChancePercent / 100.0;
                        
                        // 确保概率值在合理范围内
                        this.currentDropChance = Math.max(0.0, Math.min(1.0, dropChance));
                        this.currentCreeperChance = Math.max(0.0, Math.min(1.0, creeperChance));
                        
                        // 保存到配置
                        Config.setDropFromGrassChance(this.currentDropChance);
                        Config.setCreeperSpawnChance(this.currentCreeperChance);
                        
                        // 更新输入框显示
                        updateInputBoxes();
                    } catch (NumberFormatException e) {
                        // 输入无效，不保存
                    }
                })
                .bounds(this.width / 2 - 100, this.height - 30, 200, 20)
                .build();
        
        // 创建重置按钮
        Button resetButton = Button.builder(
                Component.translatable("screen.luckier_clover.config.reset"), 
                (button) -> {
                    // 重置配置
                    Config.resetToDefaults();
                    this.currentDropChance = Config.getDropFromGrassChance();
                    this.currentCreeperChance = Config.getCreeperSpawnChance();
                    updateInputBoxes();
                })
                .bounds(this.width / 2 - 100, this.height - 60, 200, 20)
                .build();
        
        // 创建进入黑名单管理界面按钮
        Button blacklistButton = Button.builder(
                Component.translatable("screen.luckier_clover.config.blacklist_button"), 
                (button) -> {
                    // 打开黑名单管理界面
                    this.minecraft.setScreen(new BlacklistScreen(this));
                })
                .bounds(this.width / 2 - 100, this.height - 90, 200, 20)
                .build();
        
        // 计算输入框Y坐标，使用相对位置适配窗口高度
        int inputY1 = (int)(this.height * 0.2); // 顶部20%位置
        int inputY2 = (int)(this.height * 0.3); // 顶部30%位置
        
        // 创建打草获得四叶草概率输入框
        this.dropChanceInput = new EditBox(
                this.font, 
                this.width / 2 + 50, inputY1, 100, 20, 
                Component.translatable("screen.luckier_clover.config.drop_chance"));
        this.dropChanceInput.setMaxLength(10);
        // 显示百分比前数字（如5.0表示5%）
        this.dropChanceInput.setValue(String.format("%.1f", this.currentDropChance * 100));
        this.dropChanceInput.setResponder((value) -> {
            // 实时更新当前值显示
            try {
                double inputValue = Double.parseDouble(value.replace(",", "."));
                // 将输入的百分比转换为小数
                this.currentDropChance = Math.max(0.0, Math.min(1.0, inputValue / 100.0));
            } catch (NumberFormatException e) {
                // 输入无效，保持当前值
            }
        });
        
        // 创建生成苦力怕概率输入框
        this.creeperChanceInput = new EditBox(
                this.font, 
                this.width / 2 + 50, inputY2, 100, 20, 
                Component.translatable("screen.luckier_clover.config.creeper_chance"));
        this.creeperChanceInput.setMaxLength(10);
        // 显示百分比前数字（如5.0表示5%）
        this.creeperChanceInput.setValue(String.format("%.1f", this.currentCreeperChance * 100));
        this.creeperChanceInput.setResponder((value) -> {
            // 实时更新当前值显示
            try {
                double inputValue = Double.parseDouble(value.replace(",", "."));
                // 将输入的百分比转换为小数
                this.currentCreeperChance = Math.max(0.0, Math.min(1.0, inputValue / 100.0));
            } catch (NumberFormatException e) {
                // 输入无效，保持当前值
            }
        });
        
        // 添加组件到屏幕
        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(resetButton);
        this.addRenderableWidget(blacklistButton);
        this.addRenderableWidget(this.dropChanceInput);
        this.addRenderableWidget(this.creeperChanceInput);
        
        // 设置初始焦点
        this.setInitialFocus(this.dropChanceInput);
    }
    
    /**
     * 更新输入框的值
     */
    private void updateInputBoxes() {
        // 显示百分比前数字（如5.0表示5%）
        this.dropChanceInput.setValue(String.format("%.1f", this.currentDropChance * 100));
        this.creeperChanceInput.setValue(String.format("%.1f", this.currentCreeperChance * 100));
    }
    
    @Override
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景
        this.renderBackground(guiGraphics);
        
        // 渲染标题
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // 渲染概率配置
        Component chancesTitle = Component.translatable("screen.luckier_clover.config.chances_title");
        guiGraphics.drawCenteredString(this.font, chancesTitle, this.width / 2, 40, 0xFFFFFF);
        
        // 渲染打草获得四叶草概率标签，位置与输入框对齐
        Component dropChanceLabel = Component.translatable("screen.luckier_clover.config.drop_chance", 
                String.format("%.1f", this.currentDropChance * 100));
        guiGraphics.drawString(this.font, dropChanceLabel, this.width / 2 - 150, dropChanceInput.getY() + 5, 0xFFFFFF);
        
        // 渲染生成苦力怕概率标签，位置与输入框对齐
        Component creeperChanceLabel = Component.translatable("screen.luckier_clover.config.creeper_chance", 
                String.format("%.1f", this.currentCreeperChance * 100));
        guiGraphics.drawString(this.font, creeperChanceLabel, this.width / 2 - 150, creeperChanceInput.getY() + 5, 0xFFFFFF);
        
        // 渲染黑名单配置，使用相对位置
        int blacklistTitleY = (int)(this.height * 0.4); // 顶部40%位置
        Component blacklistTitle = Component.translatable("screen.luckier_clover.config.blacklist_title");
        guiGraphics.drawCenteredString(this.font, blacklistTitle, this.width / 2, blacklistTitleY, 0xFFFFFF);
        
        // 渲染黑名单物品数量
        Component blacklistCount = Component.translatable("screen.luckier_clover.config.blacklist_count", 
                Config.getBlacklistedItems().size());
        guiGraphics.drawCenteredString(this.font, blacklistCount, this.width / 2, blacklistTitleY + 20, 0xFFFFFF);
        
        // 渲染说明文字 - 动态显示当前绑定的按键
        KeyMapping keyMapping = KeyBindings.OPEN_CONFIG_GUI;
        String keyName = keyMapping.getTranslatedKeyMessage().getString();
        Component description = Component.translatable("screen.luckier_clover.config.description", keyName);
        guiGraphics.drawCenteredString(this.font, description, this.width / 2, this.height - 120, 0xAAAAAA);
        
        // 渲染所有组件
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}