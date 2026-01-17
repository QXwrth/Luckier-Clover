package com.hqx.luckier_clover;

import com.hqx.luckier_clover.item.ModItems;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

/**
 * 幸运四叶草Mod的事件处理器
 */
@Mod.EventBusSubscriber(modid = LuckierClover.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventsHandler {
    private static final Random RANDOM = new Random();

    /**
     * 处理方块破坏事件，实现打草掉落四叶草
     */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        // 只处理草方块
        if (event.getState().is(Blocks.GRASS) || event.getState().is(Blocks.TALL_GRASS)) {
            // 检查是否掉落四叶草
            if (RANDOM.nextDouble() < Config.getDropFromGrassChance()) {
                ItemStack cloverStack = new ItemStack(ModItems.LUCKY_CLOVER.get(), 1);
                
                // 从方块位置掉落
                ItemEntity itemEntity = new ItemEntity(
                    (Level) event.getLevel(),
                    event.getPos().getX() + 0.5,
                    event.getPos().getY(),
                    event.getPos().getZ() + 0.5,
                    cloverStack
                );
                
                event.getLevel().addFreshEntity(itemEntity);
            }
        }
    }
}