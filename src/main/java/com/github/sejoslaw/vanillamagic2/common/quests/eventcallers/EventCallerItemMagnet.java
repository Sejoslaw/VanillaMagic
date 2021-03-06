package com.github.sejoslaw.vanillamagic2.common.quests.eventcallers;

import com.github.sejoslaw.vanillamagic2.common.files.VMForgeConfig;
import com.github.sejoslaw.vanillamagic2.common.quests.EventCaller;
import com.github.sejoslaw.vanillamagic2.common.quests.types.QuestItemMagnet;
import com.github.sejoslaw.vanillamagic2.common.utils.WorldUtils;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class EventCallerItemMagnet extends EventCaller<QuestItemMagnet> {
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        this.executor.onPlayerUpdate(event,
                (player) -> {
                    QuestItemMagnet quest = this.quests.get(0);
                    return this.isPlayerInventoryValid(player, quest) ? quest : null;
                },
                (player, quest) -> {
                    float itemMotion = 0.45F;

                    double x = player.getPosX();
                    double y = player.getPosY() + 0.75;
                    double z = player.getPosZ();
                    List<ItemEntity> items = WorldUtils.getEntities(player.world, ItemEntity.class, player.getPosition().add(0, 0.75, 0), quest.range, entity -> true);

                    Vector3d playerVec = new Vector3d(x, y, z);

                    for (ItemEntity itemEntity : items) {
                        Vector3d itemEntityVec = new Vector3d(itemEntity.getPosX(), itemEntity.getPosY(), itemEntity.getPosZ());
                        Vector3d finalVec = playerVec.subtract(itemEntityVec);

                        if (finalVec.length() > 1) {
                            finalVec = finalVec.normalize();
                        }

                        itemEntity.setMotion(new Vector3d(finalVec.getX() * itemMotion, finalVec.getY() * itemMotion, finalVec.getZ() * itemMotion));
                    }
                });
    }

    /**
     * @return True if Player has N iconStack as separate stacks anywhere in inventory or EnderChest; otherwise false.
     */
    private boolean isPlayerInventoryValid(PlayerEntity player, QuestItemMagnet quest) {
        return player.inventory.count(quest.iconStack.getItem()) + player.getInventoryEnderChest().count(quest.iconStack.getItem()) >= VMForgeConfig.ITEM_MAGNET_STACK_NUMBER.get();
    }
}
