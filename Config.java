package com.hqx.luckier_clover;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 幸运四叶草Mod配置类
 * 提供四叶草物品的黑名单配置和概率配置
 */
public class Config
{
    // 默认配置值
    private static final double DEFAULT_DROP_FROM_GRASS_CHANCE = 0.05;
    private static final double DEFAULT_CREEPER_SPAWN_CHANCE = 0.05;
    private static final List<String> DEFAULT_BLACKLISTED_ITEMS = new ArrayList<>();
    
    // 当前配置值
    private static double dropFromGrassChance = DEFAULT_DROP_FROM_GRASS_CHANCE;
    private static double creeperSpawnChance = DEFAULT_CREEPER_SPAWN_CHANCE;
    private static List<String> blacklistedItems = new ArrayList<>(DEFAULT_BLACKLISTED_ITEMS);
    
    /**
     * 获取打草获得四叶草的概率
     */
    public static double getDropFromGrassChance() {
        return dropFromGrassChance;
    }
    
    /**
     * 设置打草获得四叶草的概率
     */
    public static void setDropFromGrassChance(double chance) {
        dropFromGrassChance = chance;
    }
    
    /**
     * 获取生成苦力怕的概率
     */
    public static double getCreeperSpawnChance() {
        return creeperSpawnChance;
    }
    
    /**
     * 设置生成苦力怕的概率
     */
    public static void setCreeperSpawnChance(double chance) {
        creeperSpawnChance = chance;
    }
    
    /**
     * 检查物品是否在黑名单中
     */
    public static boolean isBlacklisted(String itemId) {
        return blacklistedItems.contains(itemId);
    }
    
    /**
     * 添加物品到黑名单
     */
    public static void addToBlacklist(String itemId) {
        if (!blacklistedItems.contains(itemId)) {
            blacklistedItems.add(itemId);
        }
    }
    
    /**
     * 从黑名单中移除物品
     */
    public static void removeFromBlacklist(String itemId) {
        blacklistedItems.remove(itemId);
    }
    
    /**
     * 获取黑名单物品列表
     */
    public static List<String> getBlacklistedItems() {
        return new ArrayList<>(blacklistedItems);
    }
    
    /**
     * 设置黑名单物品列表
     */
    public static void setBlacklistedItems(List<String> items) {
        blacklistedItems = new ArrayList<>(items);
    }
    
    /**
     * 获取黑名单的哈希值，用于缓存验证
     */
    public static int getBlacklistHash() {
        return Set.copyOf(blacklistedItems).hashCode();
    }
    
    /**
     * 重置所有配置到默认值
     */
    public static void resetToDefaults() {
        dropFromGrassChance = DEFAULT_DROP_FROM_GRASS_CHANCE;
        creeperSpawnChance = DEFAULT_CREEPER_SPAWN_CHANCE;
        blacklistedItems = new ArrayList<>(DEFAULT_BLACKLISTED_ITEMS);
    }
}