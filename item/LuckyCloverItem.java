package com.hqx.luckier_clover.item;

import com.hqx.luckier_clover.Config;
import com.hqx.luckier_clover.LuckierClover;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 幸运四叶草物品类
 */
@MethodsReturnNonnullByDefault
public class LuckyCloverItem extends Item {
    private static final Random RANDOM = new Random();
    
    // 缓存可用物品列表，避免每次都遍历所有物品
    private static List<Item> cachedAvailableItems = null;
    private static int cachedBlacklistHash = 0;

    public LuckyCloverItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // 检查是否生成闪电苦力怕
            if (RANDOM.nextDouble() < Config.getCreeperSpawnChance()) {
                // 计算物品掉落位置
                double x = player.getX();
                double y = player.getEyeY() - 0.1;
                double z = player.getZ();
                
                spawnChargedCreeper(level, player, x, y, z);
                player.displayClientMessage(Component.literal("AwA"), true);
            } else {
                // 生成随机物品并丢出
                ItemStack droppedItem = generateLuckyDrop();

                // 从玩家头部位置丢出物品
                ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.2, player.getZ(), droppedItem);
                itemEntity.setPickUpDelay(40);
                
                // 计算向前的速度（类似玩家丢出物品）
                float speed = 0.3f;
                double vx = -Math.sin(player.getYRot() * Math.PI / 180.0) * Math.cos(player.getXRot() * Math.PI / 180.0);
                double vy = -Math.sin(player.getXRot() * Math.PI / 180.0) * 0.1;
                double vz = Math.cos(player.getYRot() * Math.PI / 180.0) * Math.cos(player.getXRot() * Math.PI / 180.0);
                
                itemEntity.setDeltaMovement(vx * speed, 0.1, vz * speed);
                level.addFreshEntity(itemEntity);
                
                // 播放物品获得音效
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                
                // 显示消息，包含物品名称
                Component message = Component.translatable(
                    "四叶草带来了一个 %s！",
                    droppedItem.getHoverName()
                );
                player.displayClientMessage(message, true);
            }

            // 消耗物品（只在非创造模式下）
            if (!player.isCreative()) {
                itemInHand.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide);
    }

    /**
     * 生成苦力怕
     * @param x 物品掉落位置的 X 坐标
     * @param y 物品掉落位置的 Y 坐标
     * @param z 物品掉落位置的 Z 坐标
     */
    private void spawnChargedCreeper(Level level, Player player, double x, double y, double z) {
        var creeper = EntityType.CREEPER.create(level);
        if (creeper != null) {
            creeper.moveTo(x, y, z, player.getYRot(), 0.0f);
            creeper.ignite(); // 使用 ignite() 方法使苦力怕带电
            
            // 给予与物品相同的向前速度
            float speed = 0.3f;
            double vx = -Math.sin(player.getYRot() * Math.PI / 180.0) * Math.cos(player.getXRot() * Math.PI / 180.0);
            double vy = 0.1;
            double vz = Math.cos(player.getYRot() * Math.PI / 180.0) * Math.cos(player.getXRot() * Math.PI / 180.0);
            
            creeper.setDeltaMovement(vx * speed, vy, vz * speed);
            
            level.addFreshEntity(creeper);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1.0F, 1.0F);
            level.addFreshEntity(Objects.requireNonNull(EntityType.LIGHTNING_BOLT.create(level)));
        }
    }

    /**
     * 生成幸运掉落物品
     * 从所有已注册的物品中随机选择一个（排除黑名单）
     * 使用缓存优化性能
     */
    private ItemStack generateLuckyDrop() {
        List<Item> availableItems = getAvailableItems();
        
        // 如果没有可用物品，返回钻石作为默认值
        if (availableItems.isEmpty()) {
            return new ItemStack(Items.DIAMOND, 1);
        }

        // 直接从缓存列表中随机选择一个物品
        Item randomItem = availableItems.get(RANDOM.nextInt(availableItems.size()));
        return new ItemStack(randomItem, 1);
    }

    /**
     * 获取可用物品列表（使用缓存优化）
     */
    private static List<Item> getAvailableItems() {
        int currentBlacklistHash = Config.getBlacklistHash();
        
        // 如果缓存有效，直接返回缓存
        if (cachedAvailableItems != null && cachedBlacklistHash == currentBlacklistHash) {
            return cachedAvailableItems;
        }
        
        // 缓存失效，重新构建可用物品列表
        List<Item> items = new ArrayList<>();
        net.minecraftforge.registries.ForgeRegistries.ITEMS.getKeys().forEach(key -> {
            if (!Config.isBlacklisted(key.toString())) {
                Item item = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(key);
                if (item != null) {
                    items.add(item);
                }
            }
        });
        
        // 更新缓存
        cachedAvailableItems = items;
        cachedBlacklistHash = currentBlacklistHash;
        
        return items;
    }
}