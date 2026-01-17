package com.hqx.luckier_clover.screen;

import com.hqx.luckier_clover.Config;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 黑名单管理界面
 */
public class BlacklistScreen extends Screen {
    private final Screen parent;
    private EditBox searchBox;
    private List<Item> allItems;
    private List<Item> filteredItems;
    private List<Button> itemButtons;
    private int scrollOffset;
    
    // 滑条相关字段
    private boolean isDraggingScrollbar;
    private final int SCROLLBAR_WIDTH = 10;
    private final int SCROLLBAR_COLOR = 0x888888;
    private final int SCROLLBAR_HANDLE_COLOR = 0xFFFFFF;
    private int scrollbarHeight;
    private int scrollbarHandleHeight;
    private int scrollbarHandleY;
    
    /**
     * 构造函数
     */
    public BlacklistScreen(Screen parent) {
        super(Component.translatable("screen.luckier_clover.blacklist.title"));
        this.parent = parent;
        this.allItems = new ArrayList<>();
        this.filteredItems = new ArrayList<>();
        this.itemButtons = new ArrayList<>();
        this.scrollOffset = 0;
        this.isDraggingScrollbar = false;
    }
    
    @Override
    protected void init() {
        // 获取所有物品
        this.allItems = ForgeRegistries.ITEMS.getValues().stream()
                .sorted(Comparator.comparing(item -> item.getDescription().getString()))
                .collect(Collectors.toList());
        this.filteredItems = new ArrayList<>(this.allItems);
        
        // 创建返回按钮
        Button backButton = Button.builder(
                Component.translatable("screen.luckier_clover.blacklist.back"), 
                (button) -> {
                    this.minecraft.setScreen(parent);
                })
                .bounds(this.width / 2 - 100, this.height - 30, 200, 20)
                .build();
        
        // 创建搜索框
        this.searchBox = new EditBox(
                this.font, 
                this.width / 2 - 150, 20, 300, 20, 
                Component.translatable("screen.luckier_clover.blacklist.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setResponder(this::onSearchChanged);
        
        // 添加组件到屏幕
        this.addRenderableWidget(backButton);
        this.addRenderableWidget(this.searchBox);
        
        // 刷新物品按钮
        this.refreshItemButtons();
        
        // 设置初始焦点
        this.setInitialFocus(this.searchBox);
        
        // 计算滑条尺寸
        calculateScrollbarDimensions();
    }
    
    /**
     * 计算滑条尺寸
     */
    private void calculateScrollbarDimensions() {
        // 重新计算滚条高度，确保适配窗口大小
        this.scrollbarHeight = this.height - 170; // 70顶部偏移 + 100底部偏移
        
        int visibleItems = (this.height - 100) / 30;
        int totalItems = this.filteredItems.size();
        
        // 计算滑块高度，确保至少有最小高度
        if (totalItems > visibleItems) {
            this.scrollbarHandleHeight = Math.max(20, (int)((double)visibleItems / totalItems * this.scrollbarHeight));
        } else {
            this.scrollbarHandleHeight = this.scrollbarHeight;
        }
        
        // 更新滑块位置
        updateScrollbarHandlePosition();
    }
    
    /**
     * 更新滑块位置
     */
    private void updateScrollbarHandlePosition() {
        int visibleItems = (this.height - 100) / 30;
        int totalItems = this.filteredItems.size();
        
        if (totalItems > visibleItems) {
            double scrollRatio = (double)this.scrollOffset / (totalItems - visibleItems);
            // 使用相对位置计算滑块Y坐标
            this.scrollbarHandleY = 70 + (int)(scrollRatio * (this.scrollbarHeight - this.scrollbarHandleHeight));
        } else {
            this.scrollbarHandleY = 70;
        }
    }
    
    /**
     * 搜索框内容变化时的处理
     */
    private void onSearchChanged(String searchText) {
        if (searchText.isEmpty()) {
            this.filteredItems = new ArrayList<>(this.allItems);
        } else {
            String lowerSearch = searchText.toLowerCase(Locale.ROOT);
            this.filteredItems = this.allItems.stream()
                    .filter(item -> {
                        // 搜索物品名称和ID
                        String itemName = item.getDescription().getString().toLowerCase(Locale.ROOT);
                        String itemId = ForgeRegistries.ITEMS.getKey(item).toString().toLowerCase(Locale.ROOT);
                        return itemName.contains(lowerSearch) || itemId.contains(lowerSearch);
                    })
                    .collect(Collectors.toList());
        }
        this.scrollOffset = 0;
        this.refreshItemButtons();
        this.calculateScrollbarDimensions();
    }
    
    /**
     * 刷新物品按钮列表
     */
    private void refreshItemButtons() {
        // 移除所有物品按钮
        for (Button button : this.itemButtons) {
            this.removeWidget(button);
        }
        this.itemButtons.clear();
        
        // 计算可见的物品数量
        int visibleItems = (this.height - 100) / 30;
        
        // 添加可见的物品按钮
        int startIndex = this.scrollOffset;
        int endIndex = Math.min(this.filteredItems.size(), startIndex + visibleItems);
        
        for (int i = startIndex; i < endIndex; i++) {
            Item item = this.filteredItems.get(i);
            String itemId = ForgeRegistries.ITEMS.getKey(item).toString();
            boolean isBlacklisted = Config.isBlacklisted(itemId);
            
            int yPos = 70 + (i - startIndex) * 30;
            
            // 创建切换按钮
            Button toggleButton = Button.builder(
                    Component.translatable(isBlacklisted ? "screen.luckier_clover.blacklist.remove" : "screen.luckier_clover.blacklist.add"),
                    (button) -> {
                        // 切换黑名单状态
                        if (Config.isBlacklisted(itemId)) {
                            Config.removeFromBlacklist(itemId);
                        } else {
                            Config.addToBlacklist(itemId);
                        }
                        // 刷新按钮状态
                        this.refreshItemButtons();
                    })
                    .bounds(this.width - 120, yPos, 100, 20)
                    .build();
            
            // 设置按钮样式
            toggleButton.active = true;
            
            // 添加按钮
            this.addRenderableWidget(toggleButton);
            this.itemButtons.add(toggleButton);
        }
        
        // 更新滑块位置
        updateScrollbarHandlePosition();
    }
    
    @Override
    public void resize(net.minecraft.client.Minecraft p_96581_, int p_96582_, int p_96583_) {
        super.resize(p_96581_, p_96582_, p_96583_);
        // 窗口大小变化时重新计算滚条尺寸
        calculateScrollbarDimensions();
        refreshItemButtons();
    }
    
    @Override
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景
        this.renderBackground(guiGraphics);
        
        // 渲染标题
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 45, 0xFFFFFF);
        
        // 渲染搜索框
        this.searchBox.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // 计算可见的物品数量
        int visibleItems = (this.height - 100) / 30;
        
        // 显示物品名称和ID
        int startIndex = this.scrollOffset;
        int endIndex = Math.min(this.filteredItems.size(), startIndex + visibleItems);
        
        for (int i = startIndex; i < endIndex; i++) {
            Item item = this.filteredItems.get(i);
            String itemId = ForgeRegistries.ITEMS.getKey(item).toString();
            int yPos = 70 + (i - startIndex) * 30;
            
            // 显示物品名称
            guiGraphics.drawString(this.font, item.getDescription(), 30, yPos + 5, 0xFFFFFF, false);
            
            // 显示物品ID
            guiGraphics.drawString(this.font, Component.literal("(" + itemId + ")"), 30, yPos + 15, 0xAAAAAA, false);
            
            // 显示黑名单状态
            boolean isBlacklisted = Config.isBlacklisted(itemId);
            Component status = Component.translatable(isBlacklisted ? "screen.luckier_clover.blacklist.status.blacklisted" : "screen.luckier_clover.blacklist.status.allowed");
            int statusColor = isBlacklisted ? 0xFF5555 : 0x55FF55;
            // 使用相对位置，确保在不同窗口大小下都能正确显示
            guiGraphics.drawString(this.font, status, this.width - 230, yPos + 10, statusColor, false);
        }
        
        // 渲染物品列表（按钮等）
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // 渲染滑条背景 - 使用相对位置，确保在窗口最大化时可见
        int scrollbarX = this.width - 20;
        int scrollbarY = 70;
        int scrollbarHeight = this.height - 170; // 70顶部偏移 + 100底部偏移
        
        // 确保滚条高度至少为20像素
        scrollbarHeight = Math.max(20, scrollbarHeight);
        
        // 绘制滚条背景（使用更明显的颜色，确保可见）
        guiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollbarHeight, 0x88000000);
        
        // 绘制滚条边框
        guiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + 1, 0xFFFFFF); // 上边框
        guiGraphics.fill(scrollbarX, scrollbarY + scrollbarHeight - 1, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollbarHeight, 0xFFFFFF); // 下边框
        guiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + 1, scrollbarY + scrollbarHeight, 0xFFFFFF); // 左边框
        guiGraphics.fill(scrollbarX + SCROLLBAR_WIDTH - 1, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollbarHeight, 0xFFFFFF); // 右边框
        
        // 渲染滑条滑块（使用更明显的颜色）
        int sliderColor = isDraggingScrollbar ? 0xFFFFFFFF : 0xFFAAAAAA;
        guiGraphics.fill(scrollbarX + 1, this.scrollbarHandleY, scrollbarX + SCROLLBAR_WIDTH - 1, this.scrollbarHandleY + this.scrollbarHandleHeight, sliderColor);
        
        // 渲染提示文字
        Component hintText = Component.translatable("screen.luckier_clover.blacklist.hint");
        guiGraphics.drawCenteredString(this.font, hintText, this.width / 2, this.height - 50, 0xAAAAAA);
        
        // 渲染滚动信息
        String scrollInfo = String.format("%d-%d/%d", this.scrollOffset + 1, endIndex, this.filteredItems.size());
        guiGraphics.drawString(this.font, Component.literal(scrollInfo), this.width - 60, this.height - 110, 0xFFFFFF, false);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 滚条的X坐标，使用相对位置
        int scrollbarX = this.width - 20;
        int scrollbarY = 70;
        int scrollbarHeight = this.height - 170;
        
        // 检查是否点击了滑条滑块
        if (mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLLBAR_WIDTH &&
            mouseY >= this.scrollbarHandleY && mouseY <= this.scrollbarHandleY + this.scrollbarHandleHeight) {
            this.isDraggingScrollbar = true;
            return true;
        }
        // 检查是否点击了滑条背景
        else if (mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLLBAR_WIDTH &&
                 mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {
            // 计算点击位置对应的滚动偏移量
            int clickRelativeY = (int)mouseY - scrollbarY;
            double scrollRatio = (double)clickRelativeY / scrollbarHeight;
            
            int visibleItems = (this.height - 100) / 30;
            int totalItems = this.filteredItems.size();
            
            if (totalItems > visibleItems) {
                this.scrollOffset = (int)(scrollRatio * (totalItems - visibleItems));
                this.scrollOffset = Math.max(0, Math.min(totalItems - visibleItems, this.scrollOffset));
                this.refreshItemButtons();
            }
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isDraggingScrollbar) {
            // 滚条的Y坐标和高度，使用相对位置
            int scrollbarY = 70;
            int scrollbarHeight = this.height - 170;
            
            // 计算拖动位置对应的滚动偏移量
            int dragRelativeY = (int)mouseY - scrollbarY;
            // 限制拖动范围在滚条内
            dragRelativeY = Math.max(0, Math.min(scrollbarHeight, dragRelativeY));
            
            double scrollRatio = (double)dragRelativeY / scrollbarHeight;
            
            int visibleItems = (this.height - 100) / 30;
            int totalItems = this.filteredItems.size();
            
            if (totalItems > visibleItems) {
                this.scrollOffset = (int)(scrollRatio * (totalItems - visibleItems));
                this.scrollOffset = Math.max(0, Math.min(totalItems - visibleItems, this.scrollOffset));
                this.refreshItemButtons();
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isDraggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // 处理鼠标滚轮事件
        if (delta > 0) {
            // 向上滚动，减少偏移量
            this.scrollOffset = Math.max(0, this.scrollOffset - 1);
            this.refreshItemButtons();
            return true;
        } else if (delta < 0) {
            // 向下滚动，增加偏移量
            int visibleItems = (this.height - 100) / 30;
            if (this.scrollOffset + visibleItems < this.filteredItems.size()) {
                this.scrollOffset++;
                this.refreshItemButtons();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    
    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}